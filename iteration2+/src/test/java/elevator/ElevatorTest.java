package elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import scheduler.Request;

/**
 * ElevatorTest uses Junit to test the Elevator Class.
 * It also includes testing the states of an Elevator.
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
	 * This test case goes through all states of an Elevator.
	 * Methods Used: getCurrentElevatorState(), nextElevatorState(), setElevatorStateManually()
	 * An Elevator has 4 states: STOP_OPENED, STOP_CLOSED, MOVING_UP, MOVING_DOWN
	 */
	@Test
	void testingElevatorStates() {
		// Setup elevator with carnum 2
		Elevator elevator = new Elevator(2);
		// Initially elevator should be STOP_OPENED
		assertEquals(ElevatorState.STOP_OPENED, elevator.getCurrentElevatorState());
		
		//change to next state, should be STOP_CLOSED
		elevator.nextElevatorState();
		assertEquals(ElevatorState.STOP_CLOSED, elevator.getCurrentElevatorState());
		
		// Manually set state to MOVING_UP
		elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
		assertEquals(ElevatorState.MOVING_UP, elevator.getCurrentElevatorState());
		//change to next state, should be STOP_CLOSED
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
	 * This test case goes through the process of adding and removing requests from an Elevator
	 * Methods Used: clearFloor(), stop(), addPeople(), getElevatorQueue()
	 */
	@Test
	void removeRequestsFromElevator() {
		// Elevator starts off at floor 1 with IDLE direction and an empty request list
		Elevator elevator = new Elevator(2);
		assertEquals(1, elevator.getCurrentFloor());
		assertEquals(Direction.IDLE, elevator.getCurrentDirection());
		assertEquals(true, elevator.getElevatorQueue().isEmpty());
		
		// stop() and clearFloor() can't do anything with empty request list
		assertEquals(false, elevator.stop());
		assertEquals(0, elevator.clearFloor());
		
		// Make requests and store inside elevator
		Request r1 = new Request(2, Direction.UP, 5);
		Request r2 = new Request(4, Direction.UP, 5);
		Request r3 = new Request(3, Direction.UP, 7);
		elevator.addPeople(r1);
		elevator.addPeople(r2);
		elevator.addPeople(r3);
		assertEquals(false, elevator.getElevatorQueue().isEmpty());
		
		// stop() and clearFloor() still can't do anything since elevator isn't at destination
		assertEquals(false, elevator.stop());
		assertEquals(0, elevator.clearFloor());
		
		// setting current floor to 5, elevator CANNOT STOP at request destination without the starting floor
		elevator.setCurrentFloor(5);
		assertEquals(false, elevator.stop());
		
		// pick up r1
		elevator.setCurrentFloor(2);
		assertEquals(true, elevator.stop());
		
		// pick up r2
		elevator.setCurrentFloor(4);
		assertEquals(true, elevator.stop());
		
		
		// future iteration: elevator can stop since elevator is at r1 and r2's request destination floor
		elevator.setCurrentFloor(5);
		//assertEquals(true, elevator.stop());
		
		// at current floor 5, can now remove 2 people from elevator (r1 and r2)
		System.out.println(elevator.getElevatorQueue().size()); // 3
		assertEquals(2, elevator.clearFloor());
		System.out.println(elevator.getElevatorQueue().size()); // 1
		
		//finally remove last request (r3) when the current floor is 7
		elevator.setCurrentFloor(6);
		assertEquals(false, elevator.stop());
		assertEquals(0, elevator.clearFloor());
		
		elevator.setCurrentFloor(3);
		assertEquals(true, elevator.stop());
		elevator.setCurrentFloor(7);
		//assertEquals(true, elevator.stop());
		assertEquals(1, elevator.clearFloor());
		
	}

}
