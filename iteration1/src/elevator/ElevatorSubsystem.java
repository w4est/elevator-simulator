package elevator;

import java.util.ArrayList;
import java.util.HashMap;

import elevator.ElevatorSubsystem.Direction;
import scheduler.Request;
import scheduler.Scheduler;

public class ElevatorSubsystem implements Runnable {

	public enum Direction {
		UP, DOWN, IDLE
	}

	private Elevator elevator;
	private Scheduler scheduler;
	public final static int DEFAULT_MAX_FLOOR = 7;
	public final static int DEFAULT_MIN_FLOOR = 1;
	private final int MAX_FLOOR;
	private final int MIN_FLOOR;
	private ArrayList<Request> floorQueues;

	public ElevatorSubsystem(Scheduler s, int carNumber) {
		this(s, carNumber, DEFAULT_MAX_FLOOR, DEFAULT_MIN_FLOOR);
		floorQueues = new ArrayList<>();
	}

	public ElevatorSubsystem(Scheduler s, int carNumber, int max, int min) {
		this.scheduler = s;

		if (min >= max) {
			throw new IllegalArgumentException("Elevator needs at least 2 floors");
		}

		this.MIN_FLOOR = min;
		this.MAX_FLOOR = max;

		this.elevator = new Elevator(this.MAX_FLOOR, carNumber);
		floorQueues = new ArrayList<>();

	}

	public Elevator getElevator() {
		return elevator;
	}

	public int getMinFloor() {
		return MIN_FLOOR;
	}

	public int getMaxFloor() {
		return MAX_FLOOR;
	}

	public ArrayList<Request> getFloorQueues() {
		return floorQueues;
	}

	public synchronized void updateFloorQueue(Request r) {
		// Called by scheduler to add a job to the queue
		floorQueues.add(r);
		scheduler.requestReceived(elevator.getCarNumber(), elevator.getCurrentFloor(), r.getFloorNumber());
		System.out.println("Elevator subsystem got the request and will send to elevator");
	}

	public void addJob(int destination, int people) {
//		elevator.addJob(destination, people);
		System.out.println("Elevator got the request");
	}

	public synchronized void move() {

		scheduler.elevatorNeeded();

		changeDirection();

		String direction = elevator.getCurrentDirection();
		int currentFloor = elevator.getCurrentFloor();

		if (direction.equals("Up")) {
			System.out.println("Elevator is going up");
//			elevator.setCurrentFloor(currentFloor + 1);
		} else if (direction.equals("Down")) {
			System.out.println("Elevator is going down");
			elevator.setCurrentFloor(currentFloor - 1);
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// notifySubsys();

//		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();
//
//		if (destinationQueue.get(currentFloor) != 0) {
//			// Notifies scheduler to open doors
//			// Door opens
//			// Add people to elevator
//			int people = elevator.clearCurrentFloor();
//		}

//		int peopleOn = getCallingPeople(currentFloor);
//
//		if (peopleOn != 0) {
//			// If people need to get on
//			addPassengers();
//		}

		// Notifies scheduler to close doors
	}

	private synchronized void changeDirection() {

		if (goUp()) {
			elevator.setCurrentDirection("Up");
		} else if (goDown()) {
			elevator.setCurrentDirection("Down");
		} else {
			elevator.setCurrentDirection("Idle");
		}
	}

	public int getPeopleWaiting(int floor) {

		int people = 0;

		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getFloorNumber() == floor) {
				people++;
			}
		}

		return people;
	}

//	public void getsOn() {
//		for (int i = 0; i < floorQueues.size(); i++) {
//			if (floorQueues.get(i).getFloorNumber() == elevator.getCurrentFloor()) {
//				floorQueues.remove(i);
//			}
//		}
//	}

//	public void addPassengers() {
//		for (int i = 0; i < floorQueues.size(); i++) {
//			Request r = floorQueues.get(i);
//			if (r.getFloorNumber() == elevator.getCurrentFloor()) {
//				elevator.addJob(r.getCarButton(), 1);
//				floorQueues.get(i).setRequest(true);;
//			}
//		}
//	}

	private boolean goUp() {

		int currentFloor = elevator.getCurrentFloor();

		if (currentFloor == MAX_FLOOR) {
			return false;
		}

//		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();
//
//		for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
//			if (destinationQueue.containsKey(i)) {
//				return true;
//			}
//		}

		for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
			if (getPeopleWaiting(i) > 0) {
				return true;
			}
		}

		return false;
	}

	private boolean goDown() {

		int currentFloor = elevator.getCurrentFloor();

		if (currentFloor == MIN_FLOOR) {
			return false;
		}

//		HashMap<Integer, Integer> destinationQueue = elevator.getDestinationQueue();
//
//		for (int i = currentFloor - 1; i >= MIN_FLOOR; i--) {
//			if (destinationQueue.containsKey(i)) {
//				return true;
//			}
//		}

		for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
			if (getPeopleWaiting(i) > 0) {
				return true;
			}
		}

		return false;
	}

	//public boolean allCompleted() {
		//for (int i = 0; i < floorQueues.size(); i++) {
			//if (floorQueues.get(i).getRequestStatus() == false) {
				//return false;
			//}
		//}

		//return true;
	//}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (scheduler.getElevatorNeeded()) {
			move();
		}

	}
}
