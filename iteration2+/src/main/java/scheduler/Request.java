package scheduler;

import elevator.Direction;

/**
 * @author Jacob Hovey
 *
 * Request objects are used to save all relevant request information to use
 * for optimizing scheduling.
 */
public class Request {
	//the number of the floor that the request is sent from
	private final int floorNumber;
	// the direction the request wants to go; Up or Down
	private final Direction floorButton;
	//the number of the floor that the request wants to go to
	private final int carButton;
	// initially false but true when request has been sent by the FloorSubsystem
	private boolean requestSent;
	// check if the request starting floor has been completed from the Elevator
	private boolean reachedStartFloor;

	/**
	 * Request Constructor sets up an individual person's request. The request will also be set to false until it has been sent
	 * @param floorNumber int, the current floor number.
	 * @param floorButton String, the direction either being "Up" or "Down".
	 * @param carButton int, the destination floor number.
	 */
	public Request(int floorNumber, Direction floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
		this.requestSent = false;
		this.reachedStartFloor = false;
	}
	
	/**
	 * Used to get the floor number of the request.
	 * 
	 * @return	int, the floor number of the request.
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Used to get the floor button input of the request (up or down).
	 * 
	 * @return	String, the direction of the request; up or down.
	 */
	public Direction getFloorButton() {
		return floorButton;
	}

	/**
	 * Used to get the car button input of the request (target floor)
	 * 
	 * @return	int, target floor of the request.
	 */
	public int getCarButton() {
		return carButton;
	}
	
	public void setRequest(boolean sentRequest) {
		this.requestSent = sentRequest;
	}
	
	/**
	 * Used to get the request status (false if not sent to scheduler, true if sent).
	 * 
	 * @return	boolean, the sent status of the request.
	 */
	public boolean getRequestStatus() {
		return this.requestSent;
	}
	
	public boolean getReachedStartFloor() {
		return this.reachedStartFloor;
	}
	
	public void setReachedStartFloor(boolean arrived) {
		this.reachedStartFloor = arrived;
	}

}
