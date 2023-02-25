// Imports the package
package elevator;

import java.util.ArrayList;
import scheduler.Request;

/**
 * A class to emulate an elevator object
 * This object is used for when we represent
 * the state of the elevator
 * @author Farhan Mahamud
 *
 */
public class Elevator implements Runnable {

	private int currentFloor; // The current floor of the elevator
	private Direction currentDirection; // The current direction of the elevator
	private int carNumber; // The unique car number
	private ElevatorState elevatorState; // The current state of the elevator
	private ArrayList<Request> elevatorQueue; // The requests stored in an elevator

	/**
	 * The default constructor
	 * @param carNum // The unique car number
	 */
	public Elevator(int carNum) {
		currentFloor = 1; // Sets current floor
		currentDirection = Direction.IDLE; // Sets current direction
		carNumber = carNum; // Sets car number
		this.elevatorState = ElevatorState.STOP_OPENED; // Initial state is opened to wait for requests
		elevatorQueue = new ArrayList<>();
	}
	
	/**
	 * Method used to get the elevator's current state.
	 * @return ElevatorState, the state of the elevator
	 */
	public ElevatorState getCurrentElevatorState() {
		return this.elevatorState;
	}
	
	/**
	 * Method used to transition to the next state.
	 * STOP_OPENED becomes STOP_CLOSED and vice versa
	 * MOVING_UP or MOVING_DOWN becomes STOP_CLOSED
	 */
	public void nextElevatorState() {
		this.elevatorState = elevatorState.nextState();
	}
	
	/**
	 * Setter method used to manually set the elevator's state.
	 * @param newState ElevatorState, the new state of the elevator
	 */
	public void setElevatorStateManually(ElevatorState newState) {
		this.elevatorState = newState;
	}

	/**
	 * Gets the car number of the elevator
	 * @return int carNumber
	 */
	public int getCarNumber() {
		return carNumber;
	}

	/**
	 * Gets the current floor of the elevator
	 * @return int getCurrentFloor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Gets the current direction of the elevator
	 * @return Direction currentDirection
	 */
	public Direction getCurrentDirection() {
		return currentDirection;
	}

	/**
	 * Sets the current floor of the elevator
	 * @param currentFloor // The current floor
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * Sets the current direction of the elevator
	 * @param currentDirection Direction, the new direction of the elevator
	 */
	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	
	/**
	 * Getter method used to get the elevators list of requests
	 * @return ArrayList<Request>, the list of requests in the elevator
	 */
	public ArrayList<Request> getElevatorQueue(){
		return this.elevatorQueue;
	}
	
	/**
	 * Method used to add requests to the elevator's request queue
	 * @param r Request, the request to add
	 */
	public void addPeople(Request r) {
		elevatorQueue.add(r);
	}

	/**
	 * The default run function inherited from the implement class
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method checks through requests to verify if the elevator completed all requests
	 * @return boolean, true = all requests have been picked up, false otherwise
	 * @author Subear Jama
	 */
	public boolean allPeoplePickedUp() {
		if (this.elevatorQueue.isEmpty()) {
			return true;
		}
		
		int verifyCount = 0;
		for (Request r: this.elevatorQueue) {
			if (r.getRequestComplete() == true) {
				verifyCount++;
			}
		}
		if (verifyCount == this.elevatorQueue.size()) {
			return true;
		} else {
			return false;
		}
		
	}

	/**
	 * Method used to stop if an elevator is at a request's starting floor.
	 * If true, the request's "reachedStartFloor" is marked as true
	 * 
	 * @return boolean, true = elevator stop at starting floor, false otherwise.
	 * @author Subear Jama
	 */
	public boolean stopStartFloorCheck() {
		for (Request r: this.elevatorQueue) {
			//if the elevator currentFloor reached a request starting floor, STOP and set request (reachedStartFloor)
			if (r.getFloorNumber() == currentFloor && r.getReachedStartFloor() == false) {
				r.setReachedStartFloor(true);
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * Method used to stop if an elevator is at a request's destination floor
	 * if true, the request's "requestComplete" is marked as true
	 * 
	 * @return boolean, true = elevator stop at destination, false otherwise.
	 * @author Subear Jama
	 */
	public boolean stopDestinationCheck() {
		//stop for destination floors that have already been to the request's starting floor
		for (Request r: this.elevatorQueue) {
			//if the elevator currentFloor reached a request starting floor, STOP and set request (reachedStartFloor)
			if (r.getCarButton() == currentFloor && r.getReachedStartFloor() == true) {
				r.setRequestComplete(true);
				return true;
			}
		}
		return false;
	}

	/**
	 *  Method used to remove requests at their destination from the elevator
	 *  only if that request has been to its start floor.
	 * @return int, the people (requests) removed
	 */
	public int clearFloor() {
		int people = 0;
		// have to increment down since elevatorQueue will be shrinking
		for (int i = elevatorQueue.size() - 1; i >= 0; i--) {
			if (elevatorQueue.get(i).getCarButton() == currentFloor && elevatorQueue.get(i).getReachedStartFloor() == true ) {
				people++;
				elevatorQueue.remove(i);
			}
		}
		return people;
	}
}
