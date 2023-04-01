package common;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Used to store floor information (per elevator lamp)
 * @author Subear Jama
 */
public class FloorStatusRequest {
	private int floorNumber;
	private int numOfPeople;
	private boolean upButtonPressed;
	private boolean downButtonPressed;
	private int elevatorCarNum;
	private int elevatorCurrentFloor;
	
	
	public FloorStatusRequest(int floorNum, int peopleCount, boolean up, boolean down, int elevatorNum, int currentFloor) {
		this.floorNumber = floorNum;
		this.numOfPeople = peopleCount;
		this.upButtonPressed = up;
		this.downButtonPressed = down;
		this.elevatorCarNum = elevatorNum;
		this.elevatorCurrentFloor = currentFloor;
	}
	
	public byte[] toByteArray() {
		byte[] message = new byte[PacketUtils.BUFFER_SIZE];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byteBuffer.put(PacketHeaders.FloorStatus.getHeaderBytes());
		byteBuffer.putInt(floorNumber);
		byteBuffer.putInt(numOfPeople);
		
		byteBuffer.putInt(upButtonPressed? 1:0); // either true = 1 or false = 0
		byteBuffer.putInt(downButtonPressed? 1:0); // either true = 1 or false = 0
		
		byteBuffer.putInt(elevatorCarNum);
		byteBuffer.putInt(elevatorCurrentFloor);
		return message;
	}

	// o5
	public static FloorStatusRequest fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (!Arrays.equals(header, PacketHeaders.FloorStatus.getHeaderBytes())) {
			throw new IllegalArgumentException("Header is invalid, expected { 0, 5 }");
		}

		int floorNumber = byteBuffer.getInt();
		int peopleCount = byteBuffer.getInt();
		
		boolean up = byteBuffer.getInt() == 1 ? true: false;
		boolean down = byteBuffer.getInt() == 1 ? true: false;
		
		int elevatorCarNum = byteBuffer.getInt();
		int elevatorCurrentFloor = byteBuffer.getInt();
		
		return new FloorStatusRequest(floorNumber, peopleCount, up, down, elevatorCarNum, elevatorCurrentFloor);
	}

	public int getFloorNumber() {
		return this.floorNumber;
	}

	public int getNumOfPeople() {
		return this.numOfPeople;
	}

	public boolean getUpButton() {
		return this.upButtonPressed;
	}
	
	public boolean getDownButton() {
		return this.downButtonPressed;
	}
	
	public int getElevatorCurrentFloor() {
		return this.elevatorCurrentFloor;
	}
}
