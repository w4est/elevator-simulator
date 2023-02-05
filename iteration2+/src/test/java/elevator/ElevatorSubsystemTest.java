package elevator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}
