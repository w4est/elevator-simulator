package scheduler;

/**
 * @author Jacob Hovey
 *
 * Request objects are used to save all relevant request information to use
 * for optimizing scheduling.
 */
public class Request {
	private final int floorNumber;
	private final String floorButton;
	private final int carButton;
	
	private boolean requestComplete;

	/**
	 * Request Constructor sets up an individual person's request. The request will also be set to false until it has been completed
	 * @param floorNumber
	 * @param floorButton
	 * @param carButton
	 */
	public Request(int floorNumber, String floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
		this.requestComplete = false;
	}
	
	public int getFloorNumber() {
		return floorNumber;
	}

	public String getFloorButton() {
		return floorButton;
	}

	public int getCarButton() {
		return carButton;
	}
	
	public void setRequest(boolean finishedRequest) {
		this.requestComplete = finishedRequest;
	}
	
	public boolean getRequestStatus(){
		return this.requestComplete;
	}

}
