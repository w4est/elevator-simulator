package elevator;

import java.util.ArrayList;

public class ElevatorSubsystem implements Runnable{
	
	public enum Direction{
		UP, DOWN, NOT_MOVING
	}
	
	ArrayList<Elevator> elevators;
	//ArrayList<Instructions> queue;
	
	public ElevatorSubsystem(Object o) {
		elevators = new ArrayList<>();
	}
	
	public void updateQueue() {
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true) {
			updateQueue();
		}
		
	}
}
