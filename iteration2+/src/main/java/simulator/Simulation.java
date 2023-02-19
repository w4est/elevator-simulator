package simulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

/**
 * Simulation class is responsible for program initialization & runs the
 * simulation from the console
 * 
 * @author Will Forest
 */
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

		String defaultTestFile = "src/test/resources/request_test.txt"; // directory of test file for FloorSubsystem
		int maxFloors = 7; // the maximum amount of floors in the FloorSubsystem and ElevatorSubsystem

		Map<String, String> commandMap = Simulation.buildCommandLineArgumentMap(args);

		// Default usage, running in headless mode
		if (commandMap.containsKey("gui")) {
			SimulationOptionScreen simulationOptionScreen = new SimulationOptionScreen();
			simulationOptionScreen.openScreen();
		} else {
			runSimulation(defaultTestFile, maxFloors);
		}
	}

	static void runSimulation(String fileName, int maxFloors) {
		// Create objects for Scheduler, ElevatorSubsystem, and FloorSubsystem.
		Scheduler scheduler = new Scheduler();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler, 1, maxFloors, 1);
		FloorSubsystem floorSubsystem = new FloorSubsystem(fileName, scheduler, maxFloors);
		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);

		// Create threads passing them their respective objects
		floorSubsystemThread = new Thread(floorSubsystem);
		elevatorSubsystemThread = new Thread(elevatorSubsystem);

		// Start to activate all run() methods for each thread
		floorSubsystemThread.start();
		elevatorSubsystemThread.start();
	}

	private static Map<String, String> buildCommandLineArgumentMap(String args[]) {

		Map<String, String> commandMap = new HashMap<>();

		String command = null;
		for (String s : args) {
			if (s.startsWith("-")) {
				command = s.substring(1);
				commandMap.put(command, null);
			} else if (command != null) {
				commandMap.put(command, s);
				command = null;
			} else {
				throw new IllegalArgumentException(String.format("Error parsing command line, error at", s));
			}
		}

		return commandMap;
	}
}
