package scheduler;

/**
 * @author Jacob Hovey
 *
 * Request objects are used to save all relevant request information to use
 * for optimizing scheduling.
 */
public class Request {
	private int floorNumber;
	private elevator.ElevatorSubsystem.Direction floorButton;
	private int carButton;

	public Request(int floorNumber, elevator.ElevatorSubsystem.Direction floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
	}
}
