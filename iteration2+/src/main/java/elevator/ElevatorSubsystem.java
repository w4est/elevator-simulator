// Import packages
package elevator;

// Import libraries
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
//		int floor = r.getFloorNumber();
//		int people = floorQueues2.get(floor) + 1;
//		floorQueues2.put(floor, people);

		System.out.println(String.format(
				"Elevator subsystem has received the following request from the scheduler:"
						+ " Elevator %d, Requested from floor %d, Destination Floor: %d",
				elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton()));
		scheduler.requestReceived(elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton());
	}

	/**
	 * Private method used when the scheduler updates the floor queue to go to the
	 * floor requested.
	 * 
	 * Note: future iteration change this to handle multiple elevators.
	 * 
	 * @param elevatorNumber   int, The elevator's number
	 * @param destinationFloor int,The target floor
	 * @param direction        String, the direction the elevator should move based
	 *                         on the request
	 * @param requestToRemove  Request, the request to remove after completing it
	 */
	private void operate() {
		// Set Elevator state to its respective direction

		this.scheduler.elevatorNeeded();

		changeDirection();
		this.elevator.setElevatorStateManually(ElevatorState.STOP_CLOSED);

		if (elevator.getCurrentDirection().equals(Direction.UP)) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
		} else if (elevator.getCurrentDirection().equals(Direction.DOWN)) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
		}

		// Move elevator to floor (could add time delay here)
		while (true) {

			if (stopElevator()) {
				break;
			} else if (elevator.getCurrentDirection().equals(Direction.IDLE)) {
				break;
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		this.elevator.nextElevatorState(); // moving to STOP_CLOSED
		// request completed, remove request
		this.elevator.setElevatorStateManually(ElevatorState.STOP_CLOSED);
		this.elevator.setElevatorStateManually(ElevatorState.STOP_OPENED);

		int currentFloor = elevator.getCurrentFloor();

		if (elevator.stop()) {
			int people = elevator.clearFloor();
			System.out.println(
					String.format("%d people have gotten off of the elevator on floor %d", people, currentFloor));
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (peopleOnFloor(currentFloor)) {
			movePeopleOnElevator(currentFloor);
		}

//		this.floorQueues.remove(requestToRemove);
	}

	private void movePeopleOnElevator(int currentFloor) {
		int people = 0;
		
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getCarButton() == currentFloor) {
				people++;
				Request r = floorQueues.get(i);
				floorQueues.remove(r);
				elevator.addPeople(r);
			}
		} 
		
		System.out.println(String.format("%d people from floor %d got on the elevator", people, currentFloor));

	}

	private boolean peopleOnFloor(int currentFloor) {
		
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getCarButton() == currentFloor) {
				return true;
			}
		}
		return false;
	}

	private void changeDirection() {
		if (floorQueues.isEmpty()) {
			elevator.setCurrentDirection(Direction.IDLE);
			return;
		}

		if (elevator.getCurrentDirection().equals(Direction.UP)) {
			if (goUp()) {
				return;
			} else if (goDown()) {
				elevator.setCurrentDirection(Direction.DOWN);
			}

		} else if (elevator.getCurrentDirection().equals(Direction.DOWN)) {
			if (goDown()) {
				return;
			} else if (goUp()) {
				elevator.setCurrentDirection(Direction.UP);
			}
		} else {
			elevator.setCurrentDirection(Direction.IDLE);
		}
		
	}

	private boolean goUp() {

		ArrayList<Request> elevatorQueue = elevator.getElevatorQueue();

		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() > elevator.getCurrentFloor()
					|| floorQueues.get(i).getCarButton() > elevator.getCurrentFloor()) {
				return true;
			}
		}

		return false;
	}

	private boolean goDown() {

		ArrayList<Request> elevatorQueue = elevator.getElevatorQueue();

		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() < elevator.getCurrentFloor()
					|| floorQueues.get(i).getCarButton() < elevator.getCurrentFloor()) {
				return true;
			}
		}

		return false;
	}

	private boolean stopElevator() {

		if (elevator.stop()) {
			return true;
		}
		
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getFloorNumber() == elevator.getCurrentFloor()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * The run function when you start the thread
	 */
	@Override
	public void run() {

		while (!scheduler.isDone()) {
			operate();
		}

		System.out.println("Elevator subsystem is done");

	}
}
