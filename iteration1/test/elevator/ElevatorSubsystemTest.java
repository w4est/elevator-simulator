package elevator;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import scheduler.Scheduler;

public class ElevatorSubsystemTest {

	
	@Test
	void shouldPollSchedulerWhenIdle() {
		
		Thread elevatorThread = new Thread();
		elevatorThread.start();
		
		Scheduler schedularMock = Mockito.mock(Scheduler.class);
		
		assertTrue(elevatorThread.isAlive());
		
		
	}
	
	
}
