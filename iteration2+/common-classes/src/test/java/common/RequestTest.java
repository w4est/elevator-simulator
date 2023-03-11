package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class RequestTest {

	private static final byte[] testBytes = { 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0,
			1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 };

	@Test
	void shouldSerializeToByteArray() {

		Request floorButtonPress = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4);

		byte[] transformedData = floorButtonPress.toByteArray();

		assertArrayEquals(testBytes, transformedData);
	}
	
	@Test
	void shouldDeserializeToObject() {

		Request floorButtonPress = Request.fromByteArray(testBytes);

		assertEquals(Direction.UP, floorButtonPress.getFloorButton());
		assertEquals(1, floorButtonPress.getFloorNumber());
		assertEquals(LocalTime.of(2, 1, 1, 1), floorButtonPress.getLocalTime());
	}
}
