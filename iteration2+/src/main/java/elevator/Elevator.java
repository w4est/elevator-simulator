// Imports the package
package elevator;

/**
 * A class to emulate an elevator object
 * This object is used for when we represent
 * the state of the elevator
 * @author Farhan Mahamud
 *
 */
public class Elevator implements Runnable {

	private int currentFloor; // The current floor of the elevator
	private String currentDirection; // The current direction of the elevator
	private int carNumber; // The unique car number

	/**
	 * The default constructor
	 * @param carNum // The unique car number
	 */
	public Elevator(int carNum) {
		currentFloor = 1; // Sets current floor
		currentDirection = "Idle"; // Sets current direction
		carNumber = carNum; // Sets car number
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
	 * @return String currentDirection
	 */
	public String getCurrentDirection() {
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
	public void setCurrentDirection(String currentDirection) {
		this.currentDirection = currentDirection;
	}

	/**
	 * The default run function inherited from the implement class
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
