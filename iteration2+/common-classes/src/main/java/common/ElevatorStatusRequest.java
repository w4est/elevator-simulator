package common;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class used to store information when sending elevator status updates to simulation
 * @author Farhan Mahamud
 *
 */
public class ElevatorStatusRequest {

	private int elevatorNumber; // The elevator number
	private int floorNumber; // The current floor
	private int pendingRequests; // The current number of pending requests
	private boolean broken; // Whether the elevator is broken or not
	private ElevatorState state; // The current state
	
	/**
	 * The defualt constructor
	 * @param elevatorNumber
	 * @param floorNumber
	 * @param pendingRequests
	 * @param broken
	 * @param e
	 */
	public ElevatorStatusRequest(int elevatorNumber, int floorNumber, int pendingRequests, boolean broken,
			ElevatorState e) {
		this.elevatorNumber = elevatorNumber;
		this.floorNumber = floorNumber;
		this.pendingRequests = pendingRequests;
		this.broken = broken;
		this.state = e;
	}
	
	/**
	 * Converts an ElevatorStatusReport into a byte array
	 * @return
	 */
	public byte[] toByteArray() {
		byte[] message = new byte[22];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put(PacketHeaders.ElevatorStatus.getHeaderBytes());
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(elevatorNumber);
		byteBuffer.putInt(pendingRequests);
		byteBuffer.putInt(broken ? 1 : 0);
		byteBuffer.putInt(state.toInt());
		return message;
	}

	/**
	 * Converts a byte array to an ElevatorStatusRequest object
	 * @param message
	 * @return
	 */
	public static ElevatorStatusRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (!Arrays.equals(header, PacketHeaders.ElevatorStatus.getHeaderBytes())) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 4 }");
		}

		int floorNumber = byteBuffer.getInt();
		int elevatorNumber = byteBuffer.getInt();
		int pendingRequests = byteBuffer.getInt();
		boolean broken = byteBuffer.getInt() == 1 ? true : false;
		ElevatorState state = ElevatorState.fromInt(byteBuffer.getInt());
		return new ElevatorStatusRequest(elevatorNumber, floorNumber, pendingRequests, broken, state);
	}

	/**
	 * Gets the current floor
	 * @return
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Checks if the elevator is broken
	 * @return
	 */
	public boolean isBroken() {
		return broken;
	}

	/**
	 * Gets the current state
	 * @return
	 */
	public ElevatorState getState() {
		return state;
	}

	/**
	 * Gets the elevator number
	 * @return
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Sets the elevator number
	 * @param elevatorNumber
	 */
	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}
	
	/**
	 * Gets the number of pending requests
	 * @return
	 */
	public int getPendingRequests() {
		return pendingRequests;
	}

	/**
	 * An overridden function to has the object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(broken, elevatorNumber, floorNumber, pendingRequests, state);
	}

	/**
	 * An overridden function to check if an Object obj is equal to the class
	 * @param obj
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElevatorStatusRequest other = (ElevatorStatusRequest) obj;
		return broken == other.broken && elevatorNumber == other.elevatorNumber && floorNumber == other.floorNumber
				&& pendingRequests == other.pendingRequests && state == other.state;
	}

}
