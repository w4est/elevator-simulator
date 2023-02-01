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
