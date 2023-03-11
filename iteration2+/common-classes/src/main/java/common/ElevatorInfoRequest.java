package common;

import java.nio.ByteBuffer;

public class ElevatorInfoRequest {

	private int floorNumber;
	private Direction direction;
	private ElevatorState state;

	public ElevatorInfoRequest(int floorNumber, Direction direction, ElevatorState state) {
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.state = state;
	}

	public byte[] toByteArray() {
		byte[] message = new byte[18];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put((byte) 0);
		byteBuffer.put((byte) 1);
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(direction.toInt());
		byteBuffer.putInt(state.toInt());
		return message;
	}

	public static ElevatorInfoRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (header[0] != 0 && header[1] != 2) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 2 }");
		}

		int floorNumber = byteBuffer.getInt();
		Direction direction = Direction.fromInt(byteBuffer.getInt());
		ElevatorState state = ElevatorState.fromInt(byteBuffer.getInt());
		return new ElevatorInfoRequest(floorNumber, direction, state);
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public ElevatorState getState() {
		return state;
	}

	public void setState(ElevatorState state) {
		this.state = state;
	}

}