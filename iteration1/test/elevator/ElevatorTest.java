package elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ElevatorTest {

	@Test
	void shouldCreateElevator() {

		Elevator elevator = new Elevator(7, 2);

		// Assert the standard settings
		assertEquals(2, elevator.getCarNumber());
		assertEquals(1, elevator.getCurrentFloor());
		assertEquals("Idle", elevator.getCurrentDirection());
	}

}
