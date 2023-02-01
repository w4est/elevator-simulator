package floor;

import java.time.LocalTime;

/**
 * Entry Class used in FloorSubsystem to represent a data structure for each request
 * @author Subear Jama
 */
public class Entry {
	private final LocalTime timestamp;
	private final int currentFloor;
	private final String direction;
	private final int destinationFloor;
	
	private boolean requestComplete;
	
	/**
	 * Entry Constructor sets up an individual person's request. The request will also be set to false until it has been completed
	 * ex: 03:50:5.010 1 Up 3
	 * @param time
	 * @param floorNumber
	 * @param direction
	 * @param destinationFloor
	 */
	public Entry(LocalTime time, int floorNumber, String direction, int floorDestination) {
		this.timestamp = time;
		this.currentFloor = floorNumber;
		this.direction = direction; //"Up" or "Down"
		this.destinationFloor = floorDestination;
		
		this.requestComplete = false;
	}
	
	public void setRequest(boolean finishedRequest) {
		this.requestComplete = finishedRequest;
	}
	
	public boolean getRequestStatus(){
		return this.requestComplete;
	}
	
	public LocalTime getTimestamp() {
		return this.timestamp;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public String getDirection() {
		return this.direction;
	}
	
	public int getFloorDestination() {
		return this.destinationFloor;
	}
}
