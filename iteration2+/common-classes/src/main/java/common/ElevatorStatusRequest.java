package common;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class used to store information when sending elevator status updates to simulation
 * @author farha
 *
 */
public class ElevatorStatusRequest {

	private int elevatorNumber;
	private int floorNumber;
	private Direction direction;
	private ElevatorState state;
	
	public ElevatorStatusRequest(int elevatorNumber, int f, Direction d, ElevatorState e) {
		this.elevatorNumber = elevatorNumber;
		floorNumber = f;
		direction = d;
		state = e;
	}
	
	public byte[] toByteArray() {
		byte[] message = new byte[22];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put(PacketHeaders.ElevatorStatus.getHeaderBytes());
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(elevatorNumber);
		byteBuffer.putInt(direction.toInt());
		byteBuffer.putInt(state.toInt());
		return message;
	}

	public static ElevatorStatusRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (!Arrays.equals(header, PacketHeaders.ElevatorStatus.getHeaderBytes())) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 4 }");
		}

		int floorNumber = byteBuffer.getInt();
		int elevatorNumber = byteBuffer.getInt();
		Direction direction = Direction.fromInt(byteBuffer.getInt());
		ElevatorState state = ElevatorState.fromInt(byteBuffer.getInt());
		return new ElevatorStatusRequest(elevatorNumber, floorNumber, direction, state);
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public Direction getDirection() {
		return direction;
	}

	public ElevatorState getState() {
		return state;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}

}
