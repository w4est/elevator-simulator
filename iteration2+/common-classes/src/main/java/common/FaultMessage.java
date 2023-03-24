package common;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class FaultMessage {

	Fault fault;

	public FaultMessage(Fault fault) {
		this.fault = fault;
	}

	public Fault getFault() {
		return fault;
	}

	public void setFault(Fault fault) {
		this.fault = fault;
	}

	public byte[] toByteArray() {
		byte[] message = new byte[2];
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);

		if (fault == Fault.DoorFault) {
			byteBuffer.put(PacketHeaders.DoorFault.getHeaderBytes());
		} else if (fault == Fault.SlowFault) {
			byteBuffer.put(PacketHeaders.SlowFault.getHeaderBytes());
		}
		return message;
	}

	public static FaultMessage fromByteArray(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		byte[] header = new byte[2];
		byteBuffer.get(header, 0, 2);

		if (Arrays.equals(header, PacketHeaders.DoorFault.getHeaderBytes())) {
			return new FaultMessage(Fault.DoorFault);
		} else if (Arrays.equals(header, PacketHeaders.SlowFault.getHeaderBytes())) {
			return new FaultMessage(Fault.SlowFault);
		} else {
			throw new IllegalArgumentException("Header is not a valid fault, expected { 9, 1 } or { 9, 2 }");
		}
	}
}
