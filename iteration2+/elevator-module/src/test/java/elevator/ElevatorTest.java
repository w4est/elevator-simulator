package elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import common.Direction;
import common.ElevatorState;
import common.Request;

/**
 * ElevatorTest uses Junit to test the Elevator Class. It also includes testing
 * all states of an Elevator.
 * 
 * @author Subear Jama
 */
public class ElevatorTest {

	@Test
	void shouldCreateElevator() {

		Elevator elevator = new Elevator(2);

		// Assert the standard settings
		assertEquals(2, elevator.getCarNumber());
		assertEquals(1, elevator.getCurrentFloor());
		assertEquals(Direction.IDLE, elevator.getCurrentDirection());
	}

	/**
	 * This test case goes through all states of an Elevator. Methods Used:
	 * getCurrentElevatorState(), nextElevatorState(), setElevatorStateManually() An
	 * Elevator has 4 states: STOP_OPENED, STOP_CLOSED, MOVING_UP, MOVING_DOWN
	 * 
	 * @author Subear Jama
	 */
	@Test
	void testingElevatorStates() {
		// Setup elevator with carnum 2
		Elevator elevator = new Elevator(2);
		// Initially elevator should be STOP_OPENED
		assertEquals(ElevatorState.STOP_OPENED, elevator.getCurrentElevatorState());

		// change to next state, should be STOP_CLOSED
		elevator.nextElevatorState();
		assertEquals(ElevatorState.STOP_CLOSED, elevator.getCurrentElevatorState());

		// Manually set state to MOVING_UP
		elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
		assertEquals(ElevatorState.MOVING_UP, elevator.getCurrentElevatorState());
		// change to next state, should be STOP_CLOSED
		elevator.nextElevatorState();
		assertEquals(ElevatorState.STOP_CLOSED, elevator.getCurrentElevatorState());

		// Manually set state to MOVING_DOWN & next state should be STOP_CLOSED
		elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
		assertEquals(ElevatorState.MOVING_DOWN, elevator.getCurrentElevatorState());
		elevator.nextElevatorState();
		assertEquals(ElevatorState.STOP_CLOSED, elevator.getCurrentElevatorState());

		// Next state after STOP_CLOSED goes to STOP_OPENED & vice versa
		elevator.nextElevatorState();
		assertEquals(ElevatorState.STOP_OPENED, elevator.getCurrentElevatorState());
	}

	/**
	 * This test case goes through the process of adding and removing requests from
	 * an Elevator Methods Used: clearFloor(), stop(), addPeople(),
	 * getElevatorQueue()
	 * 
	 * @author Subear Jama
	 */
	@Test
	void removeRequestsFromElevator() {
		// Elevator starts off at floor 1 with IDLE direction and an empty request list
		Elevator elevator = new Elevator(2);
		assertEquals(1, elevator.getCurrentFloor());
		assertEquals(Direction.IDLE, elevator.getCurrentDirection());
		assertEquals(true, elevator.getElevatorQueue().isEmpty());

		// stop() and clearFloor() can't do anything with empty request list
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());
		assertEquals(0, elevator.clearFloor());

		// Make requests and store inside elevator
		Request r1 = new Request(LocalTime.of(1, 1), 2, Direction.UP, 5);
		Request r2 = new Request(LocalTime.of(1, 2), 4, Direction.UP, 5);
		Request r3 = new Request(LocalTime.of(1, 3), 3, Direction.UP, 7);
		elevator.addDestination(r1);
		elevator.addDestination(r2);
		elevator.addDestination(r3);
		assertEquals(false, elevator.getElevatorQueue().isEmpty());

		// stop() and clearFloor() still can't do anything since elevator isn't at
		// destination
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(0, elevator.clearFloor());

		// setting current floor to 5, elevator CANNOT STOP at request destination
		// without the starting floor
		elevator.setCurrentFloor(5);
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());

		// pick up r1 (start floor)
		elevator.setCurrentFloor(2);
		assertEquals(true, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());

		// pick up r2 (start floor)
		elevator.setCurrentFloor(4);
		assertEquals(true, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());

		// elevator can stop at destination since elevator is at r1 and r2's request
		// destination floor
		elevator.setCurrentFloor(5);
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(true, elevator.stopDestinationCheck());

		// at current floor 5, can now remove 2 people from elevator (r1 and r2) using
		// clearFloor()
		System.out.println(elevator.getElevatorQueue().size()); // 3
		assertEquals(2, elevator.clearFloor());
		System.out.println(elevator.getElevatorQueue().size()); // 1

		// finally remove last request (r3) when the current floor is 7
		elevator.setCurrentFloor(6);
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());
		assertEquals(0, elevator.clearFloor());

		elevator.setCurrentFloor(3);
		assertEquals(true, elevator.stopStartFloorCheck());
		assertEquals(false, elevator.stopDestinationCheck());

		elevator.setCurrentFloor(7);
		assertEquals(false, elevator.stopStartFloorCheck());
		assertEquals(true, elevator.stopDestinationCheck());
		assertEquals(1, elevator.clearFloor());

	}

	private class ElevatorThreadTester implements Runnable {

		private Elevator testElevator;

		public ElevatorThreadTester(Elevator testElevator) {
			this.testElevator = testElevator;
		}

		@Override
		public void run() {

			testElevator.setElevatorStateManually(ElevatorState.STOP_OPENED);
			testElevator.openOrCloseDoor();
		}

	}

	@Test
	public void shouldHandleDoorFault() {
		Elevator elevator = new Elevator(1);

		Thread thread = new Thread(new ElevatorThreadTester(elevator));
		thread.start();

		try {
			Thread.sleep(1000);
			thread.interrupt();

			// At 3 seconds, the elevator thread should still be running because of door
			// fault

			Thread.sleep(2000);
			assertEquals(true, thread.isAlive());
			// Doors have not closed yet
			assertEquals(ElevatorState.STOP_OPENED, elevator.getCurrentElevatorState());

			// At 5 seconds, the elevator thread should be done with the door
			Thread.sleep(2000);
			assertEquals(false, thread.isAlive());
			// Doors have closed
			assertEquals(ElevatorState.STOP_CLOSED, elevator.getCurrentElevatorState());
			
		} catch (InterruptedException e) {
			fail();
			e.printStackTrace();
		}
	}

}
