package common;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.LocalTime;

public class PacketUtils {

	public static final int BUFFER_SIZE = 128;
	public static final int ELEVATOR_PORT = 5002;
	public static final int SCHEDULER_FLOOR_PORT = 5003;
	public static final int SCHEDULER_ELEVATOR_PORT = 5004;
	public static final int FLOOR_PORT = 5001;
	public static final int SYNC_PORT = 5055;

	/**
	 * Converts UTF-8 strings into byte[] for transport, and places them into the
	 * provided buffer at the offset provided. Returns the position of the next
	 * empty character in the array.
	 * 
	 * This method adds an addition '0' byte to the end of every converted string
	 * 
	 * @param offset     The position to start writing to the array
	 * @param buffer     The array to write to
	 * @param characters The string to convert
	 * @return The next position that is empty in the buffer
	 */
	public static int putStringIntoByteBuffer(int offset, byte[] buffer, String characters) {
		CharBuffer charBuffer = CharBuffer.wrap(characters.toCharArray());
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);

		// Copy the converted bytes into the packet
		int position = offset;
		for (byte b : byteBuffer.array()) {
			buffer[position++] = b;
		}

		return position;
	}

	public static boolean packetContainsString(byte[] buffer, String message) {
		CharBuffer charBuffer = Charset.forName("UTF-8").decode(ByteBuffer.wrap(buffer));

		char[] packetCharacters = charBuffer.array();

		if (packetCharacters.length != message.length()) {
			return false;
		}

		for (int i = 0; i < packetCharacters.length; i++) {
			if (packetCharacters[i] != message.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if a buffer is empty, and empty buffer will have all 0 bytes.
	 * 
	 * @return whether a buffer is empty
	 */
	public static boolean isEmptyBuffer(byte[] buffer) {
		for (byte bite : buffer) {
			if (bite != 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Converts a localTime to byte[].
	 * 
	 * The byte[] will be 16 bytes long,
	 * in the format of hour, minute, seconds, nanoseconds
	 * @param localTime
	 * @return the byte array representation
	 */
	public static byte[] localTimeToByteArray(LocalTime localTime) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.putInt(localTime.getHour());
		byteBuffer.putInt(localTime.getMinute());
		byteBuffer.putInt(localTime.getSecond());
		byteBuffer.putInt(localTime.getNano());
		return byteBuffer.array();
	}
	
	/**
	 * Converts a byte[] to LocalTime.
	 * 
	 * The byte[] should be 16 bytes long,
	 * in the format of hour, minute, seconds, nanoseconds
	 * 
	 * @param localTime
	 * @return the byte array representation
	 */
	public static LocalTime byteArrayToLocalTime(byte[] message) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(message);
		int hour = byteBuffer.getInt();
		int minute = byteBuffer.getInt();
		int second = byteBuffer.getInt();
		int nano = byteBuffer.getInt();
		return LocalTime.of(hour, minute, second, nano);
	}
	
	public static byte[] stateToByteArray(ElevatorState state) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.putInt(state.toInt());
		return byteBuffer.array();
	}
}
