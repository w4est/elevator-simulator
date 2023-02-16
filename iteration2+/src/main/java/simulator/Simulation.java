package simulator;

import java.io.IOException;
import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

/**
 * Simulation class is responsible for program initialization & runs the simulation from the console
 * @author Will Forest
 */
public class Simulation {
	
	private static Thread floorSubsystemThread;
	private static Thread elevatorSubsystemThread;
	private static Thread schedulerThread;
	
	public static Thread getFloorSubsystemThread() {
		return floorSubsystemThread;
	}

	public static Thread getElevatorSubsystemThread() {
		return elevatorSubsystemThread;
	}

	public static Thread getSchedulerThread() {
		return schedulerThread;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Iteration 2 should read elevator number, floor number, and file input from command line
		
		String testFile = "src/test/resources/request_test.txt"; // directory of test file for FloorSubsystem
		int maxFloors = 7; // the maximum amount of floors in the FloorSubsystem and ElevatorSubsystem
		
		// Create objects for Scheduler, ElevatorSubsystem, and FloorSubsystem.
		Scheduler scheduler = new Scheduler();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler, 1, maxFloors, 1);
		FloorSubsystem floorSubsystem = new FloorSubsystem(testFile,scheduler, maxFloors);
		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);
		
		// Create threads passing them their respective objects 
		floorSubsystemThread = new Thread(floorSubsystem);
		elevatorSubsystemThread = new Thread(elevatorSubsystem);
		schedulerThread = new Thread(scheduler);
		
		// Start to activate all run() methods for each thread
		floorSubsystemThread.start();
		elevatorSubsystemThread.start();
		schedulerThread.start();
	}
}
