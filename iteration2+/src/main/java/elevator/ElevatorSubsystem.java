// Import packages
package elevator;

// Import libraries
import java.util.ArrayList;

import scheduler.Request;
import scheduler.Scheduler;

/**
 * This is a class used to simulate an elevator subsystem. This class is used to
 * communicate to the scheduler and receive messages to manage the elevator
 * associated with the subsystem.
 * 
 * @author Farhan Mahamud
 *
 */
public class ElevatorSubsystem implements Runnable {

	private Elevator elevator; // The elevator associated with the subsystem
	private Scheduler scheduler; // The scheduler associated with the subsystem
	public final static int DEFAULT_MAX_FLOOR = 7; // The default max floor
	public final static int DEFAULT_MIN_FLOOR = 1; // The default min floor
	private final int MAX_FLOOR; // The variable max floor set by the constructor
	private final int MIN_FLOOR; // The variable min floor set by the constructor
	private ArrayList<Request> floorQueues; // The list of requests given by the scheduler

	/**
	 * The default constructor if no minimum and maximum floors are not given
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 */
	public ElevatorSubsystem(Scheduler s, int carNumber) {
		this.MIN_FLOOR = DEFAULT_MIN_FLOOR;
		this.MAX_FLOOR = DEFAULT_MAX_FLOOR;
		this.scheduler = s;
		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
	}

	/**
	 * This is another constructor for the user for passing in custom maximum and
	 * minimum floor levels
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 * @param max       // The maximum floor level
	 * @param min       // The minimum floor level
	 */
	public ElevatorSubsystem(Scheduler s, int carNumber, int max, int min) {
		this.scheduler = s;

		if (min >= max) {
			throw new IllegalArgumentException("Elevator needs at least 2 floors");
		}

		this.MIN_FLOOR = min;
		this.MAX_FLOOR = max;

		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();

	}

	/**
	 * Gets the elevator associated with the subsystem
	 * 
	 * @return Elevator elevator
	 */
	public Elevator getElevator() {
		return elevator;
	}

	/**
	 * Gets the minimum floor of the subsystem
	 * 
	 * @return int MIN_FLOOR
	 */
	public int getMinFloor() {
		return MIN_FLOOR;
	}

	/**
	 * Gets the maximum floor of the subsystem
	 * 
	 * @return int MAX_FLOOR
	 */
	public int getMaxFloor() {
		return MAX_FLOOR;
	}

	/**
	 * Gets the list of requests sent by the scheduler
	 * 
	 * @return ArrayList<Request> floorQueues
	 */
	public ArrayList<Request> getFloorQueues() {
		return floorQueues;
	}

	/**
	 * Updates the list of the requests assigned by the scheduler
	 * 
	 * @param r Request, the highest priority request from the Scheduler
	 */
	public synchronized void updateFloorQueue(Request r) {
		floorQueues.add(r);
		System.out.println(String.format("Elevator subsystem has received the following request from the scheduler:"
				+ " Elevator %d, Requested from floor %d, Destination Floor: %d", elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton()));
		scheduler.requestReceived(elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton());
		
		// If the elevator is already moving...check if there are requests along the way
		// else move the elevator to that floor
		if (this.elevator.getCurrentElevatorState().equals(ElevatorState.MOVING_DOWN) || this.elevator.getCurrentElevatorState().equals(ElevatorState.MOVING_UP)) {
			//Depending on direction, if the new request floor is on the same path as the elevator , move there first
			if (r.getCarButton() > elevator.getCurrentFloor() && elevator.getCurrentDirection().equals(ElevatorState.MOVING_UP)) {
				this.moveElevator(elevator.getCarNumber(), r.getCarButton(), r.getFloorButton(), r);
			} else if (r.getCarButton() > elevator.getCurrentFloor() && elevator.getCurrentDirection().equals(ElevatorState.MOVING_DOWN)) {
				this.moveElevator(elevator.getCarNumber(), r.getCarButton(), r.getFloorButton(), r);
			}
			
		} else {
			elevator.nextElevatorState(); // Request added, Change Elevator State to close doors (STOP_CLOSED).
			this.moveElevator(elevator.getCarNumber(), r.getCarButton(), r.getFloorButton(), r); // Move elevator in the request's direction
		}
		// Note: 1 request received will close doors and move, but what if there are more people on that floor? (ex. need delay to collect request data?)
	}
	
	/**
	 * Private method used when the scheduler updates the floor queue to go to the floor requested.
	 * 
	 * Note: future iteration change this to handle multiple elevators. 
	 * @param elevatorNumber int, The elevator's number
	 * @param destinationFloor int,The target floor
	 * @param direction String, the direction the elevator should move based on the request
	 * @param requestToRemove Request, the request to remove after completing it
	 */
	private void moveElevator(int elevatorNumber, int destinationFloor, String direction, Request requestToRemove) {
		// Set Elevator state to its respective direction
		if (direction.equals("Up")) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
		} else if (direction.equals("Down")) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
		}
		
		// Move elevator to floor (could add time delay here)
		this.elevator.setCurrentFloor(destinationFloor);
		this.elevator.setCurrentDirection(direction);
		this.elevator.nextElevatorState(); // moving to STOP_CLOSED
		
		// request completed, remove request
		this.elevator.setElevatorStateManually(ElevatorState.STOP_OPENED);
		this.floorQueues.remove(requestToRemove);
	}

	//public void addJob(int destination, int people) {
	//	System.out.println("Elevator got the request");
	//}

	/**
	 * Moves the elevator along the floor and gets any new requests from the
	 * scheduler
	 */
	//public synchronized void move() {
	//	scheduler.elevatorNeeded();
	//}

	/**
	 * The run function when you start the thread
	 */
	@Override
	public void run() {

		while (!scheduler.isDone()) {
			//move();
			this.scheduler.elevatorNeeded();
		}

		System.out.println("Elevator subsystem is done");

	}
}
