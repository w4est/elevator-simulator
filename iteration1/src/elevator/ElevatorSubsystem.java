package elevator;

import java.util.ArrayList;
import java.util.HashMap;

import elevator.ElevatorSubsystem.Direction;
import scheduler.Scheduler;

public class ElevatorSubsystem implements Runnable {

	public enum Direction {
		UP, DOWN, NOT_MOVING
	}

	private Elevator elevator;
	private Scheduler scheduler;
	public final static int DEFAULT_MAX_FLOOR = 7;
	public final static int DEFAULT_MIN_FLOOR = 1;
	private final int MAX_FLOOR;
	private final int MIN_FLOOR;

	public ElevatorSubsystem(Elevator e, Scheduler s) {
		this(e, s, DEFAULT_MAX_FLOOR, DEFAULT_MIN_FLOOR);
	}

	public ElevatorSubsystem(Elevator e, Scheduler s, int max, int min) {
		elevator = e;
		scheduler = s;
		
		if (min >= max) {
			throw new IllegalArgumentException("Elevator needs at least 2 floors");
		}
		
		this.MIN_FLOOR = min;
		this.MAX_FLOOR = max;
	}

	public int getMinFloor() {
		return MIN_FLOOR;
	}

	public int getMaxFloor() {
		return MAX_FLOOR;
	}

	public void updateQueue(int destination, int people) {
		// Used by scheduler to update the queue of floors elevator needs to go to
		elevator.addJob(destination, people);
	}

	public synchronized void updateScheduler() {
		// Called by scheduler to add a job to the queue
	}

	public void notifySceduler() {
		// Notifies the scheduler
	}

	public void addJob(int destination, int people) {
		elevator.addJob(destination, people);
	}
	public synchronized void move() {

		changeDirection();

		Direction direction = elevator.getCurrentDirection();
		int currentFloor = elevator.getCurrentFloor();

		if (direction.equals(Direction.UP)) {
			elevator.setCurrentFloor(currentFloor + 1);
		} else if (direction.equals(Direction.DOWN)) {
			elevator.setCurrentFloor(currentFloor - 1);
		}

		// notifySubsys();

		HashMap<Integer, Integer> floorQueues = elevator.getFloorQueues();

		if (floorQueues.get(currentFloor) != 0) {
			// Notifies scheduler to open doors
			// Door opens
			// Add people to elevator
		}

		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();

		if (destinationQueue.containsKey(currentFloor)) {
			int people = floorQueues.get(currentFloor);
			elevator.addJob(currentFloor, people);
		}

		// Notifies scheduler to close doors
	}

	private synchronized void changeDirection() {

		if (goUp()) {
			elevator.setCurrentDirection(Direction.UP);
		} else if (goDown()) {
			elevator.setCurrentDirection(Direction.DOWN);
		} else {
			elevator.setCurrentDirection(Direction.NOT_MOVING);
		}
	}

	private boolean goUp() {

		int currentFloor = elevator.getCurrentFloor();

		if (currentFloor == DEFAULT_MAX_FLOOR) {
			return false;
		}

		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();

		for (int i = currentFloor; i <= DEFAULT_MAX_FLOOR; i++) {
			if (destinationQueue.containsKey(currentFloor)) {
				return true;
			}
		}

		return false;
	}

	private boolean goDown() {

		int currentFloor = elevator.getCurrentFloor();

		if (currentFloor == DEFAULT_MIN_FLOOR) {
			return false;
		}

		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();

		for (int i = currentFloor; i >= DEFAULT_MIN_FLOOR; i--) {
			if (destinationQueue.containsKey(currentFloor)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

//		while(true) {
//			updateQueue();
//		}

	}
}
