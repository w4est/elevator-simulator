package common;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A class used as a data structure to convert elevator info into byte arrays and vice versa
 * @author Farhan Mahamud
 *
 */
public class ElevatorInfoRequest {

	private int carNumber; // The car number
	private int floorNumber; // The current floor
	private Direction direction; // The current direction
	private ElevatorState state; // The current state

	/**
	 * Default constructor
	 * @param carNumber
	 * @param floorNumber
	 * @param direction
	 * @param state
	 */
	public ElevatorInfoRequest(int carNumber, int floorNumber, Direction direction, ElevatorState state) {
		this.carNumber = carNumber;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.state = state;
	}

	/**
	 * Converts an ElevatorInfoRequest into a byte array
	 * @return
	 */
	public byte[] toByteArray() {
		byte[] message = new byte[22];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put(PacketHeaders.ElevatorInfoRequest.getHeaderBytes());
		byteBuffer.putInt(carNumber);
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(direction.toInt());
		byteBuffer.putInt(state.toInt());
		return message;
	}

	/**
	 * Converts a byte array to an ElevaotrInfoRequest object
	 * @param message
	 * @return
	 */
	public static ElevatorInfoRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (!Arrays.equals(header, PacketHeaders.ElevatorInfoRequest.getHeaderBytes())) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 1 }");
		}

		int carNumber = byteBuffer.getInt();
		int floorNumber = byteBuffer.getInt();
		Direction direction = Direction.fromInt(byteBuffer.getInt());
		ElevatorState state = ElevatorState.fromInt(byteBuffer.getInt());
		return new ElevatorInfoRequest(carNumber, floorNumber, direction, state);
	}

	/**
	 * Gets the car number
	 * @return
	 */
	public int getCarNumber() {
		return carNumber;
	}

	/**
	 * Gets the floor number
	 * @return
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Sets the current floor
	 * @param floorNumber
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	/**
	 * Gets the current direction
	 * @return
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Sets the current direction
	 * @param direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Gets the current state
	 * @return
	 */
	public ElevatorState getState() {
		return state;
	}

	/**
	 * Sets the current state
	 * @param state
	 */
	public void setState(ElevatorState state) {
		this.state = state;
	}

}