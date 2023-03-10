package common;

public enum PacketHeaders {
	Request(new byte[]{0,3});
	
	
	byte[] headerBytes;
	
	PacketHeaders(byte[] headerBytes) {
		this.headerBytes = headerBytes;
	}
	
	public byte[] getHeaderBytes() {
		return this.headerBytes;
	}
}
