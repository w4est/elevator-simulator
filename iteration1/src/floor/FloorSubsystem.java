package floor;

import java.time.LocalTime;
import java.util.*;

import scheduler.Request;
import scheduler.Scheduler;

/**
 * FloorSubsystem Class communicates with Scheduler (Scheduler shared with the
 * Elevator) Gets call from person, sends info to Scheduler. Receives elevator
 * status from Scheduler, shows person (light indicator, #)
 * 
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable {

	private ArrayList<Floor> allFloors;
	private Scheduler scheduler;
	private int peopleWaitingOnAllFloors;
	private final int MAX_FLOOR;
	private TreeMap<LocalTime, Request> allRequests; // data structure holds timestamp key and
														// (currentFloor,direction,destination) value

	/**
	 * FloorSybsystem Constructor shares a scheduler and sets up floors
	 * 
	 * @param schedule Scheduler, the scheduler shared with the ElevatorSubsystem
	 */
	public FloorSubsystem(Scheduler schedule, int maxFloor) {
		this.scheduler = schedule;
		this.MAX_FLOOR = maxFloor;
		this.allFloors = new ArrayList<Floor>(MAX_FLOOR);
		this.peopleWaitingOnAllFloors = 0;
		this.allRequests = new TreeMap<LocalTime, Request>();
		// initialize all floors to have 0 people
		for (int i = 1; i < MAX_FLOOR + 1; i++) {
			addFloor(i, 0);
		}
	}

	/**
	 * private method adds a floor to the FloorSubsystem. It uses the floor's number
	 * & the number of people on that floor
	 * 
	 * @param floorNumber int, the floor's number
	 * @param numPeople   int, the number of people on that floor
	 */
	private void addFloor(int floorNumber, int numPeople) {
		if (allFloors.size() != MAX_FLOOR) {
			Floor newFloor = new Floor(floorNumber);
			newFloor.addNumberOfPeople(numPeople);
			allFloors.add(newFloor);
		} else {
			System.out.println("Error: FloorSubsystem building has max amount of floors (" + MAX_FLOOR
					+ ") and cannot add another floor");
		}
	}

	/**
	 * Private method used in run() to update the total number of people waiting on
	 * floors by checking the while loop condition until there are no more people
	 * waiting. Package level for testing
	 */
	void updatePeopleWaitingOnAllFloors() {
		this.peopleWaitingOnAllFloors = 0;
		for (Floor oneFloor : allFloors) {
			this.peopleWaitingOnAllFloors += oneFloor.getNumPeople();
		}
		
		System.out.println("Number of people waiting = " + this.peopleWaitingOnAllFloors);
	}

	/**
	 * Returns the current people waiting on all floors. To be used with performance
	 * tests.
	 * 
	 * Package level for testing
	 * 
	 * @return The number of people waiting on all floors
	 */
	int getPeopleWaitingOnAllFloors() {
		return peopleWaitingOnAllFloors;
	}

	/**
	 * Private method used in run() to Read file inputs & store data in allEntries
	 * list and allFloors list. FloorSubsystem reads an input file format where each
	 * row represents 1 person. request format: "timestamp floornumber "up"
	 * floorPersonWantsToGoTo" timestamp format: LocalTime(hh:mm:ss.mmm)
	 * 
	 * Package level function to allow testing
	 */
	void readInputFromFile() {
		String TEST_FILE = "src/input/request_test.txt";
		try (InputFileReader iReader = new InputFileReader(TEST_FILE)) {
			Optional<SimulationEntry> entry = iReader.getNextEntry();

			while (entry.isPresent()) {
				SimulationEntry currentEntry = entry.get();
				System.out.println(entry.get());

				allRequests.put(currentEntry.getTimestamp(), new Request(currentEntry.getSourceFloor(),
						currentEntry.isUp() ? "Up" : "Down", currentEntry.getDestinationFloor()));

				// Set up Floor (increase # of people and set direction for every line)
				for (Floor oneFloor : allFloors) {
					if (oneFloor.getFloorNumber() == currentEntry.getSourceFloor()) {
						oneFloor.addNumberOfPeople(1); // anytime there's another floor keep adding (incrementing) # of
														// people
						if (currentEntry.isUp()) {
							oneFloor.setUpButton(true);
						} else {
							oneFloor.setDownButton(true); // assume "Down"
						}
					}
				}
				entry = iReader.getNextEntry(); // next line
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private method used in getElevatorInfoFromScheduler() to remove all people
	 * from a floor
	 */
	private void removeAllPeopleFromFloor(int floorNumber) {
		for (Floor oneFloor : allFloors) {
			if (floorNumber == oneFloor.getFloorNumber()) {
				oneFloor.removePeople(oneFloor.getNumPeople());
			}
		}
	}

	/**
	 * Method used by Scheduler to share with the FloorSubsystem the Elevator info
	 * which will be printed to the console.
	 */
	public void getElevatorInfoFromScheduler(int elevatorNumber, int departureFloorNumber, int targetFloorNumber) {
		System.out.println(
				String.format("FloorSubsystem: Elevator# %s recieved the request and will go from Floor# %s to %s",
						elevatorNumber, departureFloorNumber, targetFloorNumber));
		removeAllPeopleFromFloor(departureFloorNumber);
	}
	
	/**
	 * Communicate with scheduler until there are no more people to move on all
	 * floors. Steps: 1. Reads request input from file and stores in FloorSubsystem.
	 * 2. Sends data to Scheduler to request an elevator. 3. Receives data from
	 * scheduler to print that the elevator will arrive.
	 */
	@Override
	public void run() {
		// read and set up allEntries and allFloors
		readInputFromFile();
		updatePeopleWaitingOnAllFloors();
		// another way could have a condition to check if all requests (from allEntries)
		// have been completed.

		while (peopleWaitingOnAllFloors != 0) {
			// loop through each key value pair in allRequests and send to simulator
			for (Map.Entry<LocalTime, Request> timestampRequest : allRequests.entrySet()) {
				// if the request hasn't been complete, send to scheduler (ex. 03:50:5.010 1 Up
				// 3)
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (timestampRequest.getValue().getRequestStatus() == false) {
					scheduler.requestElevator(timestampRequest.getKey(), timestampRequest.getValue());
					System.out.println(
							"FloorSubsystem Sending to Scheduler: Time: " + timestampRequest.getKey().toString()
									+ " Departure: Floor " + timestampRequest.getValue().getFloorNumber()
									+ " Direction: " + timestampRequest.getValue().getFloorButton()
									+ " Destination: Floor " + timestampRequest.getValue().getCarButton());
					// FloorSubsystem will receive messages from Scheduler about the elevator using
					// getElevatorInfoFromScheduler()
					// Mark request as complete
					//timestampRequest.getValue().setRequest(true);

				} else {
					// System.out.println("FloorSubsystem: Already completed request with
					// timestamp:" + timestampRequest.getKey().toString());
				}
			}
			// update the count for peopleWaitingOnAllFloors
			updatePeopleWaitingOnAllFloors();
		}
		System.out.println("People on all floors have successfully reached their destination!");
		System.out.println("Floor subsystem is sending finishing message to scheduler");
		scheduler.toggleDone();
	}
}