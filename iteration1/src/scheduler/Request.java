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

	public Request(int floorNumber, String floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
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

}
