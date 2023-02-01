package scheduler;

/**
 * @author Jacob Hovey
 *
 * Request objects are used to save all relevant request information to use
 * for optimizing scheduling.
 */
public class Request {
	private final int floorNumber;
	private final elevator.ElevatorSubsystem.Direction floorButton;
	private final int carButton;

	public Request(int floorNumber, elevator.ElevatorSubsystem.Direction floorButton, int carButton) {
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
	}
	
	public int getFloorNumber() {
		return floorNumber;
	}

	public elevator.ElevatorSubsystem.Direction getFloorButton() {
		return floorButton;
	}

	public int getCarButton() {
		return carButton;
	}

}
