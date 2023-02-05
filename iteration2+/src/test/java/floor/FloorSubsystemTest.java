package floor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import scheduler.Scheduler;

/**
 * FloorSubsystemTest uses Junit and Mockito to test FloorSubsystem Class on reading input files.
 * @author Subear Jama and William Forrest
 */
public class FloorSubsystemTest {
	
	/**
	 * This test case shows this FloorSubsystem can read input files.
	 */
	@Test
	void shouldReadInputFileAndHavePeopleWaitingTest() {
		String testFile = "test/resources/request_test.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		// create FloorSubsystem with 7 floors
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile, schedulerMock, 7);
		// read the file & update the number of people waiting on all floors
		floorSubsystem.readInputFromFile();
		// there should be 4 people total (4 requests)
		assertEquals(4, floorSubsystem.getPeopleWaitingOnAllFloors());
	}
	
	/**
	 * This test case shows this FloorSubsystem can read input files with 10 requests.
	 */
	@Test
	void readBigInputFileAndHavePeopleWaitingTest() {
		String testFile = "test/resources/reader_test2.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		// create FloorSubsystem with 7 floors
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile, schedulerMock, 6);
		// read the file & update the number of people waiting on all floors
		floorSubsystem.readInputFromFile();
		// there should be 10 people total (10 requests)
		assertEquals(10, floorSubsystem.getPeopleWaitingOnAllFloors());
	}
	
	/**
	 * This test case shows this FloorSubsystem can receive elevator info from scheduler &
	 * then removes that person
	 */
	@Test
	void removePeopleWaitingPeopleFromFloorTest() {
		String testFile = "test/resources/request_test.txt";
		Scheduler schedulerMock = Mockito.mock(Scheduler.class);
		// create FloorSubsystem with 7 floors
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile,schedulerMock, 7);
		// read the file & update the number of people waiting on all floors
		floorSubsystem.readInputFromFile();
		// there should be 4 people total (4 requests)
		assertEquals(4, floorSubsystem.getPeopleWaitingOnAllFloors());
		
		// Scheduler uses this method: Floor 1 opens elevator 1 and removes that 1 person from that floor
		floorSubsystem.getElevatorInfoFromScheduler(1, 1, 3); //elevator 1 goes from Floor 1 to Floor 3
		// Should be 3 people left on all floors
		assertEquals(3, floorSubsystem.getPeopleWaitingOnAllFloors());
		
	}
}
