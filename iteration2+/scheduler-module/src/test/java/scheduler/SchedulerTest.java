package scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import common.Direction;
import common.Request;

public class SchedulerTest {
	
	@Test
	void shouldPrioritizeRequestsProperly() {
		
		Scheduler scheduler = new Scheduler();
		
		Request request1 = new Request(LocalTime.parse("01:00:00"), 2, Direction.DOWN, 1);
		Request request2 = new Request(LocalTime.parse("01:10:00"), 4, Direction.UP, 6);
		Request request3 = new Request(LocalTime.parse("01:20:00"), 4, Direction.DOWN, 1);
		Request request4 = new Request(LocalTime.parse("01:30:00"), 5, Direction.UP, 7);
		
		scheduler.organizeRequest(request3.getLocalTime(), request3);
		scheduler.organizeRequest(request1.getLocalTime(), request1);
		scheduler.organizeRequest(request4.getLocalTime(), request4);
		scheduler.organizeRequest(request2.getLocalTime(), request2);
		
		assertEquals(request1, scheduler.sendPriorityRequest(Direction.IDLE, 1));
		assertEquals(null, scheduler.sendPriorityRequest(Direction.DOWN, 2));
		assertEquals(request2, scheduler.sendPriorityRequest(Direction.IDLE, 1));
		assertEquals(request4, scheduler.sendPriorityRequest(Direction.UP, 2));
		assertEquals(null, scheduler.sendPriorityRequest(Direction.UP, 3));
		assertEquals(null, scheduler.sendPriorityRequest(Direction.UP, 4));
		assertEquals(null, scheduler.sendPriorityRequest(Direction.UP, 5));
		assertEquals(null, scheduler.sendPriorityRequest(Direction.UP, 6));
		assertEquals(request3, scheduler.sendPriorityRequest(Direction.IDLE, 7));
	}
}
