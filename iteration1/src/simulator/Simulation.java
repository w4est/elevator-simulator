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
		// TODO figure out elevator
		Thread elevatorSubsystemThread = new Thread(new ElevatorSubsystem(new Elevator(ElevatorSubsystem.DEFAULT_MAX_FLOOR, 0), schedular));
		
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
