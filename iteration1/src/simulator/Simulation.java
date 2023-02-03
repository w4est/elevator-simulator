package simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import floor.SimulationEntry;
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

		// TODO Iteration 2 should read elevator number, floor number, and file input
		// from command line

		List<SimulationEntry> entries = new ArrayList<>();

		Scheduler scheduler = new Scheduler();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler, 1);
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, 7);

		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		floorSubsystemThread = new Thread(floorSubsystem);
		elevatorSubsystemThread = new Thread(elevatorSubsystem);

		floorSubsystemThread.start();
		elevatorSubsystemThread.start();

	}
	
	
}
