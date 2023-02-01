package elevator;

import java.util.*;

import elevator.ElevatorSubsystem.Direction;

public class Elevator implements Runnable {

	private int currentFloor;
	private Direction currentDirection;
	private int carNumber;
	private HashMap<Integer, Integer> destinationQueue;

	public Elevator(int maxFloor, int carNum) {
		currentFloor = 1;
		currentDirection = Direction.NOT_MOVING;
		carNumber = carNum;
		destinationQueue = new HashMap<Integer, Integer>();

		for (int i = 0; i < maxFloor; i++) {
			destinationQueue.put(i + 1, 0);
		}
	}

	public int getCarNumber() {
		return carNumber;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public synchronized void addJob(int destination, int people) {
		int newPeople = destinationQueue.get(destination) + people;
		destinationQueue.put(destination, newPeople);
	}

	public synchronized void peopleLeave(int floor) {
		destinationQueue.put(floor, 0);
	}

	public synchronized int getPeople() {
		int people = 0;

		for (Map.Entry<Integer, Integer> floor : destinationQueue.entrySet()) {
			people += floor.getValue();
		}

		return people;
	}

	public HashMap<Integer, Integer> getDestinationQueue() {
		return destinationQueue;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	
	public void moveUp() {
		currentFloor++;
	}
	
	public void moveDown() {
		currentFloor--;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public int clearCurrentFloor() {
		int people = destinationQueue.get(currentFloor);
		destinationQueue.put(currentFloor, 0);
		
		return people;
		
	}
}
