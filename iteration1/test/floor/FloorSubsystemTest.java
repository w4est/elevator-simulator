package floor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import scheduler.Scheduler;

public class FloorSubsystemTest {

	@Test
	void shouldReadNumberOfPeopleWaitingIfNothingHappens() {
		String testFile = "test/resources/request_test.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile, schedulerMock, 3);
		//floorSubsystem.updatePeopleWaitingOnAllFloors();
		
		assertEquals(0, floorSubsystem.getPeopleWaitingOnAllFloors());
	}
	
	@Test
	void shouldReadInputFileAndHavePeopleWaiting() {
		String testFile = "test/resources/request_test.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile,schedulerMock, 7);
		floorSubsystem.readInputFromFile();
		//floorSubsystem.updatePeopleWaitingOnAllFloors();
		
		assertEquals(4, floorSubsystem.getPeopleWaitingOnAllFloors());
	}
	
	@Test
	void shouldRemoveWaitingPeopleFromFloor() {
		String testFile = "test/resources/request_test.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile,schedulerMock, 7);
		floorSubsystem.readInputFromFile();
		
		//floorSubsystem.updatePeopleWaitingOnAllFloors();
		assertEquals(4, floorSubsystem.getPeopleWaitingOnAllFloors());
		
		// Floor 1 opens elevator 1, and removes people from floor as required
		floorSubsystem.getElevatorInfoFromScheduler(1, 1, 3);
		
		// Should be 2 left
		//floorSubsystem.updatePeopleWaitingOnAllFloors();
		assertEquals(2, floorSubsystem.getPeopleWaitingOnAllFloors());
		
	}
}
