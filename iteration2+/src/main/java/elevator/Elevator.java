// Imports the package
package elevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	private ArrayList<Request> elevatorQueue;

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
	
	public ElevatorState getCurrentElevatorState() {
		return this.elevatorState;
	}
	
	public void nextElevatorState() {
		this.elevatorState = elevatorState.nextState();
	}
	
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
	 * @param currentDirection
	 */
	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	
	public ArrayList<Request> getElevatorQueue(){
		return elevatorQueue;
	}
	
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

	public boolean stop() {

		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() == currentFloor) {
				return true;
			}
		}
		return false;
	}

	public int clearFloor() {
		int people = 0;
		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() == currentFloor) {
				people++;
				Request r = elevatorQueue.get(i);
				elevatorQueue.remove(r);
			}
		}
		return people;
	}
}
