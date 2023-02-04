package scheduler;

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
	private final String floorButton;
	//the number of the floor that the request wants to go to
	private final int carButton;
	// initially false but true when request has been sent
	private boolean requestSent;

	/**
	 * Request Constructor sets up an individual person's request. The request will also be set to false until it has been sent
	 * @param floorNumber int, the current floor number.
	 * @param floorButton String, the direction either being "Up" or "Down".
	 * @param carButton int, the destination floor number.
	 */
	public Request(int floorNumber, String floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
		this.requestSent = false;
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
	
	public void setRequest(boolean sentRequest) {
		this.requestSent = sentRequest;
	}
	
	public boolean getRequestStatus(){
		return this.requestSent;
	}

}
