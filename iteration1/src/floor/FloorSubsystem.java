package floor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import scheduler.Scheduler;

/**
 * FloorSybsystem Class communicates with Scheduler (Scheduler shared with the Elevator)
 * Gets call from person, sends info to Scheduler.
 * Receives elevator status from Scheduler, shows person (light indicator, #)
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable{	
	
	private ArrayList<Floor> allFloors;
	private Scheduler scheduler;
	private int peopleWaitingOnAllFloors;
	private final int MAX_FLOOR = 7;
	//my suggestion: request class have a date instead of hashmap. also have a boolean in it to complete a request.
	//created a datatype to handle all of this
	private ArrayList<Entry> allEntries;
	
	/**
	 * FloorSybsystem Constructor shares a scheduler and sets up floors
	 * @param schedule Scheduler, the scheduler shared with the ElevatorSubsystem
	 */
	public FloorSubsystem(Scheduler schedule) {
		this.allFloors = new ArrayList<Floor>(MAX_FLOOR);
		this.scheduler = schedule;
		this.peopleWaitingOnAllFloors = 0;
		this.allEntries = new ArrayList<Entry>();
		//Manually set up 7 floors
		addFloor(1,0);
		addFloor(2,0);
		addFloor(3,0);
		addFloor(4,0);
		addFloor(5,0);
		addFloor(6,0);
		addFloor(7,0);
	}
	
	/**
	 * This method adds a floor to the FloorSubsystem. 
	 * It uses the floor's number & the number of people on that floor
	 * @param floorNumber int, the floor's number
	 * @param numPeople int, the number of people on that floor
	 */
	public void addFloor(int floorNumber, int numPeople) {
		if (allFloors.size() != MAX_FLOOR) {
			Floor newFloor = new Floor(floorNumber);
			newFloor.addNumberOfPeople(numPeople);
			allFloors.add(newFloor);
		}
		else {
			System.out.println("Error: FloorSubsystem building has max amount of floors ("+ MAX_FLOOR +
					") and cannot add another floor");
		}
	}
	
	/**
	 * Private method used in run() to update the total number of people waiting on floors by
	 * checking the while loop condition until there are no more people waiting.
	 * @return int, total number of floors
	 */
	private void updatePeopleWaitingOnAllFloors() {
		this.peopleWaitingOnAllFloors = 0;
		for (Floor oneFloor: allFloors) {
			this.peopleWaitingOnAllFloors += oneFloor.getNumPeople();
		}
	}
	
	/**
	 * Private method used in run() to Read file inputs & store data in allEntries list and allFloors list.
	 * FloorSubsystem reads an input file format where each row represents 1 person.
	 * request format: "timestamp floornumber "up" floorPersonWantsToGoTo"
	 * timestamp format: LocalTime(hh:mm:ss.mmm)
	 */
	private void readInputFromFile() {
		String TEST_FILE = "src/input/request_test.txt";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(TEST_FILE));
			String fileLine = reader.readLine();

			while (fileLine != null) {
				System.out.println(fileLine);
				//fileline ex: "03:50:5.010 1 Up 3" (split fileline whitespace string into tokens)
				String[] splitTokens = fileLine.split("\\s+");
				
				//Set up Entry
				LocalTime timestamp = LocalTime.parse(splitTokens[0]);
				int floorNumber = Integer.parseInt(splitTokens[1]);
				String direction = splitTokens[2];
				int floorDestination = Integer.parseInt(splitTokens[3]);
				allEntries.add(new Entry(timestamp, floorNumber, direction, floorDestination));
				
				//Set up Floor (increase # of people and set direction for every line)
				for (Floor oneFloor: allFloors) {
					if (oneFloor.getFloorNumber() == floorNumber) {
						oneFloor.addNumberOfPeople(1); //anytime there's another floor keep adding (incrementing) # of people
						if (direction.equals("Up")) {
							oneFloor.setUpButton(true);
						}
						else {
							oneFloor.setDownButton(true); //assume "Down"
						}
					}
				}
				fileLine = reader.readLine(); //next line
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Private method used in getElevatorInfoFromScheduler() to remove all people from a floor
	 */
	private void removeAllPeopleFromFloor(int floorNumber) {
		for (Floor oneFloor: allFloors) {
			if (floorNumber == oneFloor.getFloorNumber()) {
				oneFloor.removePeople(oneFloor.getNumPeople());
			}
		}
	}
	
	/**
	 * Method used by Scheduler to share with the FloorSubsystem the Elevator info which will be printed to the console.
	 */
	public void getElevatorInfoFromScheduler(int elevatorNumber, int departureFloorNumber, int targetFloorNumber) {
		//My Suggestion: Scheduler Class should also provide which request that was so the FloorSubsystem can properly identify it? (like the timestamp)
		//Also Scheduler Class should have the same FloorSubsystem in its constructor so that it talks to the right one.
		System.out.println("FloorSubsystem: Elevator#"+ elevatorNumber + "recieved the request and will go from Floor#"
		+ departureFloorNumber + " to " + targetFloorNumber);
		//either this way or just change entry object's requestComplete to true.
		removeAllPeopleFromFloor(targetFloorNumber);
	}
	/**
	 * Communicate with scheduler until there are no more people to move on all floors.
	 * Steps: 1. Reads request input from file and stores in FloorSubsystem.
	 * 		  2. Sends data to Scheduler to request an elevator.
	 * 		  3. Receives data from scheduler to print that the elevator will arrive.
	 */
	@Override
	public void run() {
		//read and set up allEntries and allFloors
		readInputFromFile();
		updatePeopleWaitingOnAllFloors();
		//another way could have a condition to check if all requests (from allEntries) have been completed.
		while (peopleWaitingOnAllFloors != 0) {
			//loop through all entries (organized by timestamp) and send to simulator
			for (Entry oneEntry: allEntries) {
				if (oneEntry.getRequestStatus() == false) {
					//NOTE: used String "Up" "Down" to represent direction (stated in project document). Also used LocalTime.
					scheduler.requestElevator(oneEntry.getTimestamp(),oneEntry.getCurrentFloor(), oneEntry.getDirection(), oneEntry.getFloorDestination());
					System.out.println("FloorSubsystem Sending to Scheduler: Time "+ oneEntry.getTimestamp().toString() +
							" Floor#" + oneEntry.getCurrentFloor() + " Direction"+ oneEntry.getDirection() +
							" Destination " + oneEntry.getFloorDestination());
				} else {
					System.out.println("FloorSubsystem: Already completed rq Floor#" + oneEntry.getCurrentFloor() + " wants to go to Floor#" + oneEntry.getFloorDestination());
				}
				//update the count for the peopleWaitingOnAllFloors
				//NOTE: should be in else block but no way of setting true status yet (see getElevatorInfoFromScheduler() method for reasoning)
				updatePeopleWaitingOnAllFloors();
			}
			//FloorSubsystem will receive messages from Scheduler about the elevator using getElevatorInfoFromScheduler()
			try {
				Thread.sleep(120); // slow down for correct order of print statements
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		System.out.println("People on all floors have successfully reached their destination!\n");
	}
	/*
	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		FloorSubsystem test = new FloorSubsystem(s);
		test.readInputFromFile(); //tested by changing to a public method. delete later
	}*/
}