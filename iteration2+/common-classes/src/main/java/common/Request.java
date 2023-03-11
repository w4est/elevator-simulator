package common;

import java.nio.ByteBuffer;
import java.time.LocalTime;

/**
 * @author Jacob Hovey
 *
 *         Request objects are used to save all relevant request information to
 *         use for optimizing scheduling.
 */
public class Request {
	private final LocalTime localTime;
	// the number of the floor that the request is sent from
	private final int floorNumber;
	// the direction the request wants to go; Up or Down
	private final Direction floorButton;
	// the number of the floor that the request wants to go to
	private final int carButton;
	// check if the request starting floor has been completed from the Elevator
	private boolean reachedStartFloor;
	// check if the request destination floor has been completed from the Elevator
	private boolean requestComplete;
	// initially false but true when request has been sent by the FloorSubsystem
	private boolean requestSent;

	/**
	 * Request Constructor sets up an individual person's request. The request will
	 * also be set to false until it has been sent
	 * 
	 * @param localTime   the timestamp of the request
	 * @param floorNumber int, the current floor number.
	 * @param floorButton String, the direction either being "Up" or "Down".
	 * @param carButton   int, the destination floor number.
	 */
	public Request(LocalTime localTime, int floorNumber, Direction floorButton, int carButton) {
		this.localTime = localTime;
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
		this.reachedStartFloor = false;
		this.requestComplete = false;
		this.requestSent = false;
	}

	/**
	 * Request Constructor sets up an individual person's request. The request will
	 * also be set to false until it has been sent
	 * 
	 * @param localTime   the timestamp of the request
	 * @param floorNumber int, the current floor number.
	 * @param floorButton String, the direction either being "Up" or "Down".
	 * @param carButton   int, the destination floor number.
	 * @param requestSent
	 */
	public Request(LocalTime localTime, int floorNumber, Direction floorButton, int carButton,
			boolean reachedStartFloor, boolean requestComplete) {
		this.localTime = localTime;
		this.floorNumber = floorNumber;
		this.floorButton = floorButton;
		this.carButton = carButton;
		this.reachedStartFloor = reachedStartFloor;
		this.requestComplete = requestComplete;
	}

	public LocalTime getLocalTime() {
		return localTime;
	}

	/**
	 * Used to get the floor number of the request.
	 * 
	 * @return int, the floor number of the request.
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Used to get the floor button input of the request (up or down).
	 * 
	 * @return String, the direction of the request; up or down.
	 */
	public Direction getFloorButton() {
		return floorButton;
	}

	/**
	 * Used to get the car button input of the request (target floor)
	 * 
	 * @return int, target floor of the request.
	 */
	public int getCarButton() {
		return carButton;
	}

	/**
	 * Used to get the status of the request start floor
	 * 
	 * @return boolean, true = request has entered the elevator, false = request is
	 *         waiting for elevator
	 */
	public boolean getReachedStartFloor() {
		return this.reachedStartFloor;
	}

	/**
	 * Used to set the status of the request start floor
	 * 
	 * @param arrived boolean, true = request has entered the elevator, false =
	 *                request is waiting for elevator
	 */
	public void setReachedStartFloor(boolean arrived) {
		this.reachedStartFloor = arrived;
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

	/**
	 * Used to check if the request has been completed
	 * 
	 * @return boolean, true = request has reached its destination, false otherwise
	 */
	public boolean getRequestComplete() {
		return this.requestComplete;
	}

	/**
	 * Used to mark request as complete when it reaches its destination
	 * 
	 * @param destination boolean, true = request has reached its destination, false
	 *                    otherwise
	 */
	public void setRequestComplete(boolean destination) {
		this.requestComplete = destination;
	}

	/**
	 * Converts request to byte array
	 * @return
	 */
	public byte[] toByteArray() {
		byte[] message = new byte[36];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put(PacketHeaders.Request.getHeaderBytes());
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(floorButton.ordinal());
		byteBuffer.putInt(carButton);
		byteBuffer.put(PacketUtils.localTimeToByteArray(localTime));
		byteBuffer.put(reachedStartFloor ? (byte) 1 : (byte) 0);
		byteBuffer.put(requestComplete ? (byte) 1 : (byte) 0);
		return message;
	}

	/**
	 * Converts byte array to a request
	 * @param message
	 * @return
	 */
	public static Request fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		byte[] expectedHeader = PacketHeaders.Request.getHeaderBytes();
		if (header[0] != expectedHeader[0] && header[1] != expectedHeader[1]) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 2 }");
		}

		int floorNumber = byteBuffer.getInt();
		Direction direction = Direction.fromInt(byteBuffer.getInt());
		int carButton = byteBuffer.getInt();
		byte[] localTimeBytes = new byte[16];
		byteBuffer.get(localTimeBytes, 0, 16);
		LocalTime localTime = PacketUtils.byteArrayToLocalTime(localTimeBytes);
		boolean reachedStartFloor = byteBuffer.get() == (byte) 1 ? true : false;
		boolean requestComplete = byteBuffer.get() == (byte) 1 ? true : false;
		return new Request(localTime, floorNumber, direction, carButton, reachedStartFloor, requestComplete);
	}
}
