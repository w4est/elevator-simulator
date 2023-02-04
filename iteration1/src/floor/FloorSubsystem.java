package floor;

import java.time.LocalTime;
import java.util.*;

import scheduler.Request;
import scheduler.Scheduler;

/**
 * FloorSubsystem Class communicates with Scheduler (Scheduler shared with the Elevator).
 * It first takes requests from a text file to store and send to the Scheduler.
 * It then receives info about the Elevator from the Scheduler and removes people from the 
 * floor to signal as completed.
 * 
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable {

	private ArrayList<Floor> allFloors;
	private int peopleWaitingOnAllFloors;
	private final int MAX_FLOOR;
	private Scheduler scheduler;
	private final String TEST_FILE;
	//TreeMap data structure (key = time stamp, value = currentFloor,direction,destination)
	private TreeMap<LocalTime, Request> allRequests;
	
	/**
	 * FloorSubsystem Constructor takes in a text file, shares a scheduler, and sets up the number of floors.
	 * @param directory String, the directory of the request data (ex: "test/resources/request_test.txt").
	 * @param schedule Scheduler, the scheduler shared with the ElevatorSubsystem.
	 * @param maxFloor int, represents the number of floors the FloorSubsystem should create.
	 */
	public FloorSubsystem(String directory, Scheduler schedule, int maxFloor) {
		this.scheduler = schedule;
		this.MAX_FLOOR = maxFloor;
		this.TEST_FILE = directory;
		this.allFloors = new ArrayList<Floor>(MAX_FLOOR);
		this.peopleWaitingOnAllFloors = 0;
		this.allRequests = new TreeMap<LocalTime, Request>();
		// initialize all floors to have 0 people
		for (int i = 1; i < MAX_FLOOR + 1; i++) {
			addFloor(i, 0);
		}
	}

	/**
	 * Private method adds a floor to the FloorSubsystem within the Constructor.
	 * It uses the floor's number and the number of people on that floor.
	 * @param floorNumber int, the floor's number.
	 * @param numPeople   int, the number of people on that floor.
	 */
	private void addFloor(int floorNumber, int numPeople) {
		if (allFloors.size() != MAX_FLOOR) {
			Floor newFloor = new Floor(floorNumber);
			newFloor.addNumberOfPeople(numPeople);
			allFloors.add(newFloor);
		} else {
			System.out.println("Error: FloorSubsystem building has max amount of floors "
					+ "(" + MAX_FLOOR + ") and cannot add another floor");
		}
	}

	/**
	 * Private method used in readInputFromFile() and run() to update the 
	 * total number of people waiting on all floors. It does this by resetting the 
	 * count and then adding people from every floor.
	 */
	private void updatePeopleWaitingOnAllFloors() {
		this.peopleWaitingOnAllFloors = 0;
		for (Floor oneFloor : allFloors) {
			this.peopleWaitingOnAllFloors += oneFloor.getNumPeople();
		}
	}

	/**
	 * This method returns the current total amount of people waiting on all floors. 
	 * 
	 * Note: Used to test if data is being passed back and forth + performance tests.
	 * @return The number of people waiting on all floors
	 */
	public int getPeopleWaitingOnAllFloors() {
		return peopleWaitingOnAllFloors;
	}

	/**
	 * This method is used in run() to read a text file of requests where each row represents 1 person.
	 * It then stores data in allEntries and allFloors lists. 
	 * request format: "timestamp floornumber "up" floorPersonWantsToGoTo" 
	 * 
	 * Note: Set to public instead of private for test case showing this can read input files & pass the data back and forth.
	 */
	public void readInputFromFile() {
		
		try (InputFileReader iReader = new InputFileReader(this.TEST_FILE)) {
			Optional<SimulationEntry> entry = iReader.getNextEntry();
			while (entry.isPresent()) {
				// Get & print the text line entry
				SimulationEntry currentEntry = entry.get();
				System.out.println(entry.get());

				// Store line read from text into allRequests TreeMap
				allRequests.put(currentEntry.getTimestamp(), new Request(currentEntry.getSourceFloor(),
						currentEntry.isUp() ? "Up" : "Down", currentEntry.getDestinationFloor()));

				// Set up Floor (increase # of people and set direction for every line)
				for (Floor oneFloor : allFloors) {
					if (oneFloor.getFloorNumber() == currentEntry.getSourceFloor()) {
						 // anytime there's another floor keep adding (incrementing) # of people
						oneFloor.addNumberOfPeople(1);						
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
		// Finally set up a count for all the people waiting on all floors (peopleWaitingOnAllFloors) from allRequests
		updatePeopleWaitingOnAllFloors();
	}
	
	/**
	 * Private method is used in getElevatorInfoFromScheduler() to remove 1 person from a floor.
	 * @param floorNumber int, the floor number to remove 1 person from.
	 */
	private void removePersonFromFloor(int floorNumber) {
		this.peopleWaitingOnAllFloors -=1;
		for (Floor oneFloor : allFloors) {
			if (floorNumber == oneFloor.getFloorNumber()) {
				oneFloor.removePeople(1);
			}
		}
	}
	
	/**
	 * Method is used by Scheduler for the FloorSubsystem to receive the Elevator info.
	 * It prints out a message to signal its been received and then removes that person from the floor
	 * 
	 * Note: Also used to test if data is being passed back and forth.
	 * @param elevatorNumber       int, the elevator's number.
	 * @param departureFloorNumber int, the request's current floor.
	 * @param targetFloorNumber    int, the request's destination floor
	 */
	public void getElevatorInfoFromScheduler(int elevatorNumber, int departureFloorNumber, int targetFloorNumber) {
		System.out.println(
				String.format("FloorSubsystem Received from Scheduler: "
						+ "Elevator# %s recieved the request will go from Floor# %s to %s",
						elevatorNumber, departureFloorNumber, targetFloorNumber));
		removePersonFromFloor(departureFloorNumber); //remove that 1 person from the floor
	}
	
	/**
	 * When the thread starts this communicates with scheduler until all people on all floors have gotten a response
	 * so that they are not waiting.
	 * Steps: 1. Reads request input from file and stores in FloorSubsystem.
	 * 		  2. Sends data to Scheduler to request an elevator. 
	 *		  3. Receives elevator data from scheduler to print that the elevator will arrive.
	 */
	@Override
	public void run() {
		// read and set up allRequests (to send requests) and allFloors (used to check while loop)
		readInputFromFile();
		// condition is true when all the requests have received a message which removes people from the floor
		while (peopleWaitingOnAllFloors != 0) {
			// loop through each key value pair in allRequests and send each request to scheduler
			for (Map.Entry<LocalTime, Request> timestampRequest : allRequests.entrySet()) {
				// if the request hasn't been complete, send to scheduler (ex. 03:50:5.010 1 Up 3)
				if (timestampRequest.getValue().getRequestStatus() == false) {
					scheduler.requestElevator(timestampRequest.getKey(), timestampRequest.getValue());
					System.out.println(
							"FloorSubsystem Sending to Scheduler:    Time: " + timestampRequest.getKey().toString()
									+ ", Departure: Floor " + timestampRequest.getValue().getFloorNumber()
									+ ", Direction: " + timestampRequest.getValue().getFloorButton()
									+ ", Destination: Floor " + timestampRequest.getValue().getCarButton());
					// FloorSubsystem will receive elevator messages from Scheduler (using getElevatorInfoFromScheduler()) 
					// and removes that 1 person from that floor while marking request as sent (true)
					timestampRequest.getValue().setRequest(true);
				}
			}
			// update the count for peopleWaitingOnAllFloors to recheck loop
			updatePeopleWaitingOnAllFloors();
		}
		System.out.println("FloorSubsystem Finished:                "
				+ "People on all floors have successfully reached their destination!");
		// Signal the Scheduler that this Thread has been completed to then end the ElevatorSubsystem Thread
		scheduler.toggleDone();
	}
}