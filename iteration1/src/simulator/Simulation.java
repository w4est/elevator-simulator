package simulator;

import java.util.ArrayList;
import java.util.List;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

public class Simulation {
	public static void main(String[] args) {
		
		// TODO pass in a filepath
		
		SimulatorReader reader = new SimulatorReader("testfile.txt");
		
		List<SimulationEntry> entries = new ArrayList<>();
		
		Scheduler schedular = new Scheduler();
		Thread floorSubsystemThread = new Thread(new FloorSubsystem(schedular));
		Thread elevatorSubsystemThread = new Thread(new ElevatorSubsystem(schedular, 1));
		
		floorSubsystemThread.start();
		elevatorSubsystemThread.start();
		
		
		// TODO this should maybe go into the floor? Debatable, ask TA
		/*WaitingQueue()
		
		while (SimulationEntry entry: entries) {
			
			Floorsubsystem
			//waits
		}*/
	}
}
