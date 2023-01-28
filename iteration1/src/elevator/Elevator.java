package elevator;

import elevator.ElevatorSubsystem.Direction;

public class Elevator implements Runnable{
	
	private int currentFloor;
	private Direction currentDirection;
	private ElevatorSubsystem subsys;
	private final int MAX_FLOOR = 7;
	private final int MIN_FLOOR = 1;
	private int carNumber;
	//private ArrayList<> jobs;
	
	public Elevator(ElevatorSubsystem s, int carNum) {
		currentFloor = 1;
		currentDirection = Direction.NOT_MOVING;
		subsys = s;
		carNumber = carNum;
	}
	
	public void move() {
		if (currentDirection.equals(Direction.UP)) {
			currentFloor += 1;
		}
		
		else if (currentDirection.equals(Direction.DOWN)) {
			currentFloor -= 1;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
