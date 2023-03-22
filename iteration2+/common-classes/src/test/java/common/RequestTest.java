package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RequestTest {

	private static final byte[] testBytes = { 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0,
			1, 0, 0, 0, 1, 0, 0 };

	@Test
	void shouldSerializeToByteArray() {

		Request floorButtonPress = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4);

		byte[] transformedData = floorButtonPress.toByteArray();

		assertArrayEquals(testBytes, transformedData);
	}

	@Test
	void shouldDeserializeToObject() {

		List<Request> floorButtonPress = Request.fromByteArray(testBytes);

		assertEquals(1, floorButtonPress.size());
		assertEquals(Direction.UP, floorButtonPress.get(0).getFloorButton());
		assertEquals(1, floorButtonPress.get(0).getFloorNumber());
		assertEquals(LocalTime.of(2, 1, 1, 1), floorButtonPress.get(0).getLocalTime());
	}

	@Test
	void shouldDeserializeMultipleRequestsToObject() {

		// Allocate 3 requests in a single packet
		ByteBuffer buffer = ByteBuffer.allocate(testBytes.length * 3);
		buffer.put(testBytes);
		buffer.put(testBytes);
		buffer.put(testBytes);

		List<Request> floorButtonPress = Request.fromByteArray(buffer.array());

		assertEquals(3, floorButtonPress.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(Direction.UP, floorButtonPress.get(0).getFloorButton());
			assertEquals(1, floorButtonPress.get(0).getFloorNumber());
			assertEquals(LocalTime.of(2, 1, 1, 1), floorButtonPress.get(0).getLocalTime());
		}
	}
}
