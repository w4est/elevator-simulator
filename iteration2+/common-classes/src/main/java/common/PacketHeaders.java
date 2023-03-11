package common;

/**
 * Class to define header bits for UDP packets
 * @author WIll Forest
 *
 */
public enum PacketHeaders {
	FloorButtonRequest(new byte[]{0,3}),
	ElevatorInfoRequest(new byte[]{0,1});
	
	byte[] headerBytes;
	
	PacketHeaders(byte[] headerBytes) {
		this.headerBytes = headerBytes;
	}
	
	public byte[] getHeaderBytes() {
		return this.headerBytes;
	}
}
