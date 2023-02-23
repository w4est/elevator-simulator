package scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import elevator.Direction;
import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;

public class SchedulerTest {

	@Test
	public void shouldToggleDone() {
		ElevatorSubsystem elevatorSubsystem = Mockito.mock(ElevatorSubsystem.class);
		FloorSubsystem floorSubsystem = Mockito.mock(FloorSubsystem.class);
		Scheduler scheduler = new Scheduler();

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		assertEquals(false, scheduler.isDone());
		scheduler.toggleDone();
		assertEquals(true, scheduler.isDone());
		assertEquals(SchedulerStates.AllRequestsComplete, scheduler.getSchedulerState());
	}

	@Test
	public void shouldChangeStateWhenElevatorRequested() {
		ElevatorSubsystem elevatorSubsystem = Mockito.mock(ElevatorSubsystem.class);
		FloorSubsystem floorSubsystem = Mockito.mock(FloorSubsystem.class);
		Scheduler scheduler = new Scheduler();

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		assertEquals(false, scheduler.isElevatorNeeded());
		assertEquals(SchedulerStates.CheckForRequests, scheduler.getSchedulerState());
		Request request = new Request(5, Direction.UP, 1);

		scheduler.requestElevator(LocalTime.now(), request);
		assertEquals(true, scheduler.isElevatorNeeded());
		assertEquals(SchedulerStates.IncompleteRequests, scheduler.getSchedulerState());
	}

	@Test
	public void shouldTellElevatorSubsystemOfFloorRequest() {
		ElevatorSubsystem elevatorSubsystem = Mockito.mock(ElevatorSubsystem.class);
		FloorSubsystem floorSubsystem = Mockito.mock(FloorSubsystem.class);
		Scheduler scheduler = new Scheduler();

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		assertEquals(SchedulerStates.CheckForRequests, scheduler.getSchedulerState());
		Request request = new Request(5, Direction.UP, 1);

		scheduler.requestElevator(LocalTime.now(), request);
		assertEquals(SchedulerStates.IncompleteRequests, scheduler.getSchedulerState());

		assertEquals(true, scheduler.isElevatorNeeded());
		scheduler.elevatorNeeded();

		// Make sure the floorQueue on the elevator subsystem was updated
		Mockito.verify(elevatorSubsystem, times(1)).updateFloorQueue(request);
		assertEquals(SchedulerStates.CheckForResponses, scheduler.getSchedulerState());
		assertEquals(false, scheduler.isElevatorNeeded());
	}

	@Test
	public void shouldChangeStateIfRequestRecievedByElevator() {
		ElevatorSubsystem elevatorSubsystem = Mockito.mock(ElevatorSubsystem.class);
		FloorSubsystem floorSubsystem = Mockito.mock(FloorSubsystem.class);
		Scheduler scheduler = new Scheduler();

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		Request request = new Request(5, Direction.UP, 1);
		scheduler.requestElevator(LocalTime.now(), request);
		scheduler.elevatorNeeded();

		assertEquals(SchedulerStates.CheckForResponses, scheduler.getSchedulerState());

		// Emulate an elevator receiving a request
		scheduler.requestReceived(1, 1, 5);

		// Make sure the floorSubsystem was updated
		Mockito.verify(floorSubsystem, times(1)).getElevatorInfoFromScheduler(1, 1, 5);
		assertEquals(SchedulerStates.CheckForRequests, scheduler.getSchedulerState());
	}
}
