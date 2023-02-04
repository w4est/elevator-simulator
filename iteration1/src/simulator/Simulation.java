package simulator;

import java.io.IOException;
import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

public class Simulation {
	
	private static Thread floorSubsystemThread;
	private static Thread elevatorSubsystemThread;
	
	public static Thread getFloorSubsystemThread() {
		return floorSubsystemThread;
	}

	public static Thread getElevatorSubsystemThread() {
		return elevatorSubsystemThread;
	}

	public static void main(String[] args) throws IOException {

		// TODO Iteration 2 should read elevator number, floor number, and file input from command line
		String testFile = "test/resources/request_test.txt";

		Scheduler scheduler = new Scheduler();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler, 1);
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile,scheduler, 7);

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		floorSubsystemThread = new Thread(floorSubsystem);
		elevatorSubsystemThread = new Thread(elevatorSubsystem);

		floorSubsystemThread.start();
		elevatorSubsystemThread.start();

	}
	
}
