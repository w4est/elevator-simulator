package common;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class used to store information when sending elevator status updates to simulation
 * @author farha
 *
 */
public class ElevatorStatusRequest {

	private int elevatorNumber;
	private int floorNumber;
	private int pendingRequests;
	private boolean broken;
	private ElevatorState state;
	
	public ElevatorStatusRequest(int elevatorNumber, int f, int pendingRequests, boolean broken, ElevatorState e) {
		this.elevatorNumber = elevatorNumber;
		this.floorNumber = f;
		this.pendingRequests = pendingRequests;
		this.broken = broken;
		this.state = e;
	}
	
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

	public int getFloorNumber() {
		return floorNumber;
	}

	public boolean isBroken() {
		return broken;
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
	
	public int getPendingRequests() {
		return pendingRequests;
	}

	@Override
	public int hashCode() {
		return Objects.hash(broken, elevatorNumber, floorNumber, pendingRequests, state);
	}

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
