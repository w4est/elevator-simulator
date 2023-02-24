package elevator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import scheduler.Request;
import scheduler.Scheduler;

public class ElevatorSubsystemTest {

	@Test
	void shouldPollSchedulerWhenIdle() {
		
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);

		// On the first attempt, say that the scheduler is not done
		// On the second, inform the elevatorThread to shutdown
		when(schedulerMock.isDone()).thenReturn(false, true);

		Thread elevatorThread = new Thread(new ElevatorSubsystem(schedulerMock, 1));
		elevatorThread.start();

		assertTrue(elevatorThread.isAlive());
		// Give the thread some time to interact
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Verify that the elevator subsystem checked with the scheduler
		verify(schedulerMock, atLeastOnce()).elevatorNeeded();

		// Wait for the elevator to finish
		try {
			elevatorThread.join(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertFalse(elevatorThread.isAlive());
	}
	
	/**
	 * This test case goes through the states of an elevator from the
	 * perspective of the ElevatorSubsystem.
	 * 
	 * NOTE: another way to test states is to run simulation and look at the console
	 * @author Subear Jama
	 */
	@Test
	void shouldTestElevatorStates() {
		//Initialization
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(schedulerMock, 1);
		// Before starting thread, add scheduler requests manually into ElevatorSubsystem
		Request r1 = new Request(2, Direction.UP, 5);
		Request r2 = new Request(4, Direction.UP, 5);
		Request r3 = new Request(3, Direction.UP, 7);
		elevatorSubsystem.updateFloorQueue(r1);
		elevatorSubsystem.updateFloorQueue(r2);
		elevatorSubsystem.updateFloorQueue(r3);
		Thread elevatorThread = new Thread(elevatorSubsystem);
		elevatorThread.start();
		
		// check print statements in console (added into ElevatorSubsystem to see current floor & state)
		/*If you see this in the console before the ElevatorSubsystem finishes, Ignore. Doesn't happen in actual simulation:
		 * 
		 * ElevatorSubsystem: Start Operate Cycle
		 * 1. Elevator Current Floor & State: Floor 1, State: STOP_OPENED
		 * 2. Elevator Current Floor & State: Floor 1, State: STOP_CLOSED
		 */
	}
	
	
}
