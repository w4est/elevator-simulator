package common;

import java.nio.ByteBuffer;

public class ElevatorInfoRequest {

	private short floorNumber;
	private Direction direction;
	private ElevatorState state;

	public ElevatorInfoRequest(short floorNumber, Direction direction, ElevatorState state) {
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.state = state;
	}

	public byte[] toByteArray() {
		byte[] message = new byte[22];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put((byte) 0);
		byteBuffer.put((byte) 1);
		byteBuffer.putShort(floorNumber);
		byteBuffer.putShort(direction.toShort());
		byteBuffer.put(PacketUtils.stateToByteArray(state));
		return message;
	}

	public static ElevatorInfoRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (header[0] != 0 && header[1] != 2) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 2 }");
		}

		short floorNumber = byteBuffer.getShort();
		Direction direction = Direction.fromShort(byteBuffer.getShort());

		byte[] stateBytes = new byte[16];
		byteBuffer.get(stateBytes, 0, 16);
//		ElevatorState localTime = PacketUtils.byteArrayToElevatorState(stateBytes);
		ElevatorState state = ElevatorState.MOVING_UP;
		return new ElevatorInfoRequest(floorNumber, direction, state);
	}

	public short getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(short floorNumber) {
		this.floorNumber = floorNumber;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}