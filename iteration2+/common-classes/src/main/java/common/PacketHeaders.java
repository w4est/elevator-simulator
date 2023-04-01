package common;

/**
 * Class to define header bits for UDP packets
 * @author Will Forest
 *
 */
public enum PacketHeaders {
	Request(new byte[]{0,3}),
	ElevatorInfoRequest(new byte[]{0,1}),
	ElevatorStatus(new byte[] {0,4}),
	FloorStatus(new byte[] {0,5}),
	DoorFault(new byte[]{9,1}),
	SlowFault(new byte[]{9,2});
	
	byte[] headerBytes;
	
	PacketHeaders(byte[] headerBytes) {
		this.headerBytes = headerBytes;
	}
	
	public byte[] getHeaderBytes() {
		return this.headerBytes;
	}
}
