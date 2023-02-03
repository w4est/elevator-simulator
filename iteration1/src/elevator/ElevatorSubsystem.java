package elevator;

import java.util.ArrayList;
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
		scheduler.requestReceived(elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton());
		r.setRequest(true);
	}

	public void addJob(int destination, int people) {
		System.out.println("Elevator got the request");
	}

	public synchronized void move() {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scheduler.elevatorNeeded();
		
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

	private boolean goUp() {

		int currentFloor = elevator.getCurrentFloor();

		if (currentFloor == MAX_FLOOR) {
			return false;
		}

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

		for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
			if (getPeopleWaiting(i) > 0) {
				return true;
			}
		}

		return false;
	}

	public boolean allCompleted() {
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getRequestStatus() == false) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void run() {
		
		while (!scheduler.isDone()) {
			move();
		}
		
		System.out.println("Elevator subsystem is done");

	}
}
