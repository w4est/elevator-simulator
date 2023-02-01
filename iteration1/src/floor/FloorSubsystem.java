package floor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import scheduler.Request;
import scheduler.Scheduler;

/**
 * FloorSubsystem Class communicates with Scheduler (Scheduler shared with the Elevator)
 * Gets call from person, sends info to Scheduler.
 * Receives elevator status from Scheduler, shows person (light indicator, #)
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable{	
	
	private ArrayList<Floor> allFloors;
	private Scheduler scheduler;
	private int peopleWaitingOnAllFloors;
	private final int MAX_FLOOR;
	private HashMap<LocalTime, Request> allRequests; //data structure holds timestamp key and (currentFloor,direction,destination) value
	
	/**
	 * FloorSybsystem Constructor shares a scheduler and sets up floors
	 * @param schedule Scheduler, the scheduler shared with the ElevatorSubsystem
	 */
	public FloorSubsystem(Scheduler schedule, int maxFloor) {
		this.scheduler = schedule;
		this.MAX_FLOOR = maxFloor;
		this.allFloors = new ArrayList<Floor>(MAX_FLOOR);
		this.peopleWaitingOnAllFloors = 0;
		this.allRequests = new HashMap<LocalTime,Request>();
		//initialize all floors to have 0 people
		for (int i = 1; i < MAX_FLOOR + 1; i++) {
			addFloor(i,0);
		}
	}
	
	/**
	 * private method adds a floor to the FloorSubsystem. 
	 * It uses the floor's number & the number of people on that floor
	 * @param floorNumber int, the floor's number
	 * @param numPeople int, the number of people on that floor
	 */
	private void addFloor(int floorNumber, int numPeople) {
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
				
				//Set up allRequests Hashmap with timestamp key & Request value
				LocalTime timestamp = LocalTime.parse(splitTokens[0]);
				int floorNumber = Integer.parseInt(splitTokens[1]);
				String direction = splitTokens[2];
				int floorDestination = Integer.parseInt(splitTokens[3]);
				
				allRequests.put(timestamp, new Request(floorNumber,direction, floorDestination));
				
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
		System.out.println("FloorSubsystem: Elevator#"+ elevatorNumber + "recieved the request and will go from Floor#"
		+ departureFloorNumber + " to " + targetFloorNumber);
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
			//loop through each key value pair in allRequests and send to simulator
			for (HashMap.Entry<LocalTime, Request> timestampRequest : allRequests.entrySet()) {
			    //if the request hasn't been complete, send to scheduler (ex. 03:50:5.010 1 Up 3)
			    if (timestampRequest.getValue().getRequestStatus() == false) {
					scheduler.requestElevator(timestampRequest.getKey(), timestampRequest.getValue());
					System.out.println("FloorSubsystem Sending to Scheduler: Time "+ timestampRequest.getKey().toString() +
							" Floor#" + timestampRequest.getValue().getFloorNumber() + " Direction"+ timestampRequest.getValue().getFloorButton() +
							" Destination " + timestampRequest.getValue().getCarButton());
					//FloorSubsystem will receive messages from Scheduler about the elevator using getElevatorInfoFromScheduler()
					//Mark request as complete
					timestampRequest.getValue().setRequest(true);
					
				} else {
					//System.out.println("FloorSubsystem: Already completed request with timestamp:" + timestampRequest.getKey().toString());
				}
			}
			//update the count for peopleWaitingOnAllFloors
			updatePeopleWaitingOnAllFloors();
			try {
				Thread.sleep(120); // slow down for correct order of print statements
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		System.out.println("People on all floors have successfully reached their destination!\n");
	}
}