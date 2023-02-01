package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

public class Simulation {
	public static void main(String[] args) throws IOException {
				
		SimulatorReader reader = determineFileToRead(args);
		
		List<SimulationEntry> entries = new ArrayList<>();
		
		Scheduler schedular = new Scheduler();
		Thread floorSubsystemThread = new Thread(new FloorSubsystem(schedular));
		Thread elevatorSubsystemThread = new Thread(new ElevatorSubsystem(schedular, 1));
		
		//floorSubsystemThread.start();
		//elevatorSubsystemThread.start();
		
		
		// TODO this should maybe go into the floor? Debatable, ask TA
		Optional<SimulationEntry> entry;
		do {
			entry = reader.getNextEntry();
			if (entry.isPresent()) {
				entries.add(entry.get());
				System.out.println(entry.get());
			}
		} while (entry.isPresent());
	}
	
	private static SimulatorReader determineFileToRead(String[] args)
			throws FileNotFoundException, IllegalStateException {

		if (args.length % 2 != 0) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equals("-file") && args[i + 1].endsWith(".txt")) {
				return new SimulatorReader(args[i + 1]);
			}
		}
		throw new IllegalStateException("No file given, cannot run.");
	}
}
