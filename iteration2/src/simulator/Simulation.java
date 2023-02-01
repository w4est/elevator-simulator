package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import floor.SimulationEntry;
import scheduler.Scheduler;

public class Simulation {
	public static void main(String[] args) throws IOException {
				
		//SimulatorReader reader = determineFileToRead(args);
		
		List<SimulationEntry> entries = new ArrayList<>();
		
		Scheduler scheduler = new Scheduler();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler, 1);
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, 7);
		
		scheduler.addElevatorSubsys(elevatorSubsystem);
		scheduler.addFloorSubsys(floorSubsystem);
		
		Thread floorSubsystemThread = new Thread(floorSubsystem);
		Thread elevatorSubsystemThread = new Thread(elevatorSubsystem);
		
		floorSubsystemThread.start();
		elevatorSubsystemThread.start();
		
		
		// TODO this should maybe go into the floor? Debatable, ask TA
		//Optional<SimulationEntry> entry;
		//do {
			//entry = reader.getNextEntry();
			//if (entry.isPresent()) {
				//entries.add(entry.get());
				//System.out.println(entry.get());
			//}
		//} while (entry.isPresent());
	}
	
	//private static SimulatorReader determineFileToRead(String[] args)
			//throws FileNotFoundException, IllegalStateException {

		//if (args.length % 2 != 0) {
			//throw new IllegalArgumentException();
		//}

		//for (int i = 0; i < args.length; i += 2) {
			//if (args[i].equals("-file") && args[i + 1].endsWith(".txt")) {
				//return new SimulatorReader(args[i + 1]);
			//}
		//}
		//throw new IllegalStateException("No file given, cannot run.");
	//}
}
