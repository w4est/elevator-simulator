package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ElevatorInfoRequestTest {

	private static final byte[] testBytes = { 0, 1, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0 };

	@Test
	void shouldSerializeToByteArray() {

		ElevatorInfoRequest elevatorInfo = new ElevatorInfoRequest(1, 2, Direction.UP, ElevatorState.MOVING_UP);

		byte[] transformedData = elevatorInfo.toByteArray();

		assertArrayEquals(testBytes, transformedData);
	}

	@Test
	void shouldDeserializeToObject() {

		ElevatorInfoRequest elevatorInfo = ElevatorInfoRequest.fromByteArray(testBytes);

		assertEquals(Direction.UP, elevatorInfo.getDirection());
		assertEquals(2, elevatorInfo.getFloorNumber());
		assertEquals(1, elevatorInfo.getCarNumber());
		assertEquals(ElevatorState.MOVING_UP, elevatorInfo.getState());
	}

}
