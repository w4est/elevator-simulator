package common;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ElevatorInfoRequest {

	private int carNumber;
	private int floorNumber;
	private Direction direction;
	private ElevatorState state;

	public ElevatorInfoRequest(int carNumber, int floorNumber, Direction direction, ElevatorState state) {
		this.carNumber = carNumber;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.state = state;
	}

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

	public int getCarNumber() {
		return carNumber;
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