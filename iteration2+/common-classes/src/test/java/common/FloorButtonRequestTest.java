package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class FloorButtonRequestTest {

	private static final byte[] testBytes = { 0, 2, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 };

	@Test
	void shouldSerializeToByteArray() {

		FloorButtonRequest floorButtonPress = new FloorButtonRequest((short) 1, Direction.UP, LocalTime.of(2, 1, 1, 1));

		byte[] transformedData = floorButtonPress.toByteArray();

		assertArrayEquals(testBytes, transformedData);
	}
	
	@Test
	void shouldDeserializeToObject() {

		FloorButtonRequest floorButtonPress = FloorButtonRequest.fromByteArray(testBytes);

		assertEquals(Direction.UP, floorButtonPress.getDirection());
		assertEquals((short) 1, floorButtonPress.getFloorNumber());
		assertEquals(LocalTime.of(2, 1, 1, 1), floorButtonPress.getLocalTime());
	}
}
