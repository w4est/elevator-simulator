package common;

import java.nio.ByteBuffer;
import java.time.LocalTime;

public class FloorButtonRequest {

	private short floorNumber;
	private Direction direction;
	private LocalTime localTime;

	public FloorButtonRequest(short floorNumber, Direction direction, LocalTime localTime) {
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.localTime = localTime;
	}

	public byte[] toByteArray() {
		byte[] message = new byte[22];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put((byte) 0);
		byteBuffer.put((byte) 2);
		byteBuffer.putShort(floorNumber);
		byteBuffer.putShort(direction.toShort());
		byteBuffer.put(PacketUtils.localTimeToByteArray(localTime));
		return message;
	}

	public static FloorButtonRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (header[0] != 0 && header[1] != 2) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 2 }");
		}

		short floorNumber = byteBuffer.getShort();
		Direction direction = Direction.fromShort(byteBuffer.getShort());
		byte[] localTimeBytes = new byte[16];
		byteBuffer.get(localTimeBytes, 0, 16);
		LocalTime localTime = PacketUtils.byteArrayToLocalTime(localTimeBytes);
		return new FloorButtonRequest(floorNumber, direction, localTime);
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

	public LocalTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

}
