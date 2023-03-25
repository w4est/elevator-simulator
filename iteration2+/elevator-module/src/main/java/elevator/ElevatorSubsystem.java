// Import packages
package elevator;

// Import libraries
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import common.Direction;
import common.ElevatorState;
import common.Request;

/**
 * This is a class used to simulate an elevator subsystem. This class is used to
 * communicate to the scheduler and receive messages to manage the elevator
 * associated with the subsystem.
 * 
 * @author Farhan Mahamud
 *
 */
public class ElevatorSubsystem implements Runnable {

	private Elevator elevator; // The elevator associated with the subsystem
//	private DatagramPacket sendPacket, receivePacket; // Packets for sending and receiveing
//	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets
	public final static int DEFAULT_MAX_FLOOR = 7; // The default max floor
	public final static int DEFAULT_MIN_FLOOR = 1; // The default min floor
	private final int MAX_FLOOR; // The variable max floor set by the constructor
	private final int MIN_FLOOR; // The variable min floor set by the constructor
	private ArrayList<Request> floorQueues; // The list of requests given by the scheduler

	private final long FLOOR_MOVEMENT_DEADLINE = 5000L;
	private boolean running = true;

	/**
	 * The default constructor if no minimum and maximum floors are not given
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 * @author Farhan Mahamud
	 */
	public ElevatorSubsystem(int carNumber) {
		this.MIN_FLOOR = DEFAULT_MIN_FLOOR;
		this.MAX_FLOOR = DEFAULT_MAX_FLOOR;
		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
		this.elevator.setCurrentFloor(MIN_FLOOR);
//		this.setupSocket();
	}

	/**
	 * Constructor used for testing with a Mockito socket
	 * 
	 * @param carNumber
	 * @param s
	 * @author Farhan Mahamud
	 */
	public ElevatorSubsystem(int carNumber, DatagramSocket s) {
		this(carNumber);
//		this.closeSocket();
//		this.socket = s;
	}

	/**
	 * This is another constructor for the user for passing in custom maximum and
	 * minimum floor levels
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 * @param max       // The maximum floor level
	 * @param min       // The minimum floor level
	 * @author Farhan Mahamud
	 */
	public ElevatorSubsystem(/* Scheduler s, */ int carNumber, int max, int min) {

		if (min >= max) {
			throw new IllegalArgumentException("Elevator needs at least 2 floors");
		}

		this.MIN_FLOOR = min;
		this.MAX_FLOOR = max;

		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
		this.elevator.setCurrentFloor(MIN_FLOOR);
	}

	/**
	 * Gets the elevator associated with the subsystem
	 * 
	 * @return Elevator elevator
	 */
	public Elevator getElevator() {
		return elevator;
	}

	/**
	 * Gets the minimum floor of the subsystem
	 * 
	 * @return int MIN_FLOOR
	 */
	public int getMinFloor() {
		return MIN_FLOOR;
	}

	/**
	 * Gets the maximum floor of the subsystem
	 * 
	 * @return int MAX_FLOOR
	 */
	public int getMaxFloor() {
		return MAX_FLOOR;
	}

	/**
	 * Gets the list of requests sent by the scheduler
	 * 
	 * @return ArrayList<Request> floorQueues
	 */
	public ArrayList<Request> getFloorQueues() {
		return floorQueues;
	}

	public synchronized void addRequests(List<Request> requests) {
		for (Request r : requests) {
			floorQueues.add(r);
			elevator.getElevatorQueue().add(r);
		}
		notifyAll();
	}

	/**
	 * Private method repeatedly called in run until the scheduler is done. Used to
	 * setup elevator movement and handle requests.
	 * 
	 * future iteration: change method to handle multiple elevators.
	 * 
	 * @author Subear Jama and Farhan Mahamud
	 */
	private synchronized void operate() {
		while (floorQueues.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		System.out.println("	ElevatorSubsystem: Start Operate Cycle");
		System.out.println("	1. Elevator " + this.elevator.getCarNumber() + " Current Floor & State: " + "Floor "
				+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED

		// Elevator door is closing to begin its path
		// STOP_OPENED -> STOP_CLOSED
		elevator.openOrCloseDoor();

		System.out.println("	2. Elevator " + this.elevator.getCarNumber() + " Current Floor & State: " + "Floor "
				+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED

		// 2: set the elevator's current direction (MOVING_UP/DOWN)
		changeDirection();

		// 3: Move elevator until it has moved the request
		while (this.elevator.getCurrentElevatorState() != ElevatorState.STOP_OPENED
				&& this.elevator.getCurrentElevatorState() != ElevatorState.STOP_CLOSED && running) {
			// stop for starting request
			if (stopElevator() == 1) {
				this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
				System.out.println("	3: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: "
						+ "Floor " + this.elevator.getCurrentFloor() + ", State: "
						+ this.elevator.getCurrentElevatorState()); // STOP_CLOSED

				// Elevator door is opening to pick up people
				// STOP_CLOSED -> STOP_OPENED
				elevator.openOrCloseDoor();

				System.out.println("	4: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: "
						+ "Floor " + this.elevator.getCurrentFloor() + ", State: "
						+ this.elevator.getCurrentElevatorState()); // STOP_OPENED

				movePeopleOnElevator(elevator.getCurrentFloor()); // in method will go to STOP_CLOSED then
																	// MOVING_UP/DOWN

			}
			// stop for destination request
			else if (stopElevator() == 2) {
				this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
				System.out.println("	5: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: "
						+ "Floor " + this.elevator.getCurrentFloor() + ", State: "
						+ this.elevator.getCurrentElevatorState()); // STOP_CLOSED

				// Elevator door is opening to drop off people
				// STOP_CLOSED -> STOP_OPENED
				elevator.openOrCloseDoor();

				this.elevator.setCurrentDirection(Direction.IDLE);
				System.out.println("	6: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: "
						+ "Floor " + this.elevator.getCurrentFloor() + ", State: "
						+ this.elevator.getCurrentElevatorState()); // STOP_OPENED

				// elevator reached destination, clear floor and break while loop
				int peopleRemoved = this.elevator.clearFloor();
				System.out.println("	7. Elevator " + this.elevator.getCarNumber()
						+ " reached destination and dropped off " + peopleRemoved + " request(s)!");
				if (elevator.getElevatorQueue().isEmpty()) {
					break;
				} else {
					// Elevator door is closing after dropping off people
					elevator.waitForPeopleToMoveOffOrOnElevator(peopleRemoved);
					// STOP_OPENED -> STOP_CLOSE
					elevator.openOrCloseDoor();

					System.out.println("	8: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: "
							+ "Floor " + this.elevator.getCurrentFloor() + ", State: "
							+ this.elevator.getCurrentElevatorState()); // STOP_CLOSED
					changeDirection();
				}
			}

			// move elevator up or down 1 floor based on current elevator state
			moveElevator();
		}
	}

	/**
	 * Public method used in operate to actually move the elevator up or down 1
	 * floor depending on the elevator's current state.
	 * 
	 * @author Subear Jama
	 */
	public void moveElevator() {
		int nextFloorUp = this.elevator.getCurrentFloor() + 1;
		int nextFloorDown = this.elevator.getCurrentFloor() - 1;

		if (this.elevator.getCurrentElevatorState() == ElevatorState.MOVING_UP
				&& elevator.getCurrentFloor() < this.MAX_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorUp);
		} else if (this.elevator.getCurrentElevatorState() == ElevatorState.MOVING_DOWN
				&& elevator.getCurrentFloor() > this.MIN_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorDown);
		}

		// Elevator moving floor to floor
		// Make sure we spend the exact amount of time required, event if
		// interrupts come at this time (we don't have interrupt handling here)
		long timeStartedMoving = System.currentTimeMillis();
		long timeToSpend = elevator.isSlowMode() ? 8000 : 4000;
		while (timeToSpend > 0) {
			try {
				Thread.sleep(timeToSpend);
				timeToSpend = 0L;
			} catch (InterruptedException e) {
				// Keep going, interruptions don't affect elevator motors
				if (System.currentTimeMillis() - timeStartedMoving < timeToSpend) {
					timeToSpend = System.currentTimeMillis() - timeStartedMoving;
				}
			}
		}

		// Check deadline met
		if (System.currentTimeMillis() - timeStartedMoving > FLOOR_MOVEMENT_DEADLINE) {
			emergencyStop();
		} else {
			System.out.println(
					"	Moving: Elevator " + this.elevator.getCarNumber() + " Current Floor & State: " + "Floor "
							+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // Floor
																														// move
		}
	}

	/**
	 * Public method used in operate that checks if a request starting floor from
	 * ElevatorSubsystem floorQueues matches the elevator's current floor. it then
	 * removes that request from ElevatorSubsystem.
	 * 
	 * @param currentFloor int, the elevator's current floor
	 * @author Farhan Mahamud and Subear Jama
	 */
	public void movePeopleOnElevator(int currentFloor) {
		int people = 0;

		for (int i = floorQueues.size() - 1; i >= 0; i--) {
			if (floorQueues.get(i).getFloorNumber() == currentFloor) {
				people++;
				floorQueues.remove(i);
			}
		}

		// Elevator door is closing after picking up people
		elevator.waitForPeopleToMoveOffOrOnElevator(people);
		// STOP_OPENED -> STOP_CLOSED
		elevator.openOrCloseDoor();

		System.out.println(String.format(
				"	ElevatorSubsystem: %d request from Floor %d got on the Elevator " + this.elevator.getCarNumber(),
				people, currentFloor));
		// close elevator
		System.out.println("	Elevator Current Floor & State: " + "Floor " + this.elevator.getCurrentFloor()
				+ ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
		// set elevator's direction again
		changeDirection(); // STOP_CLOSED -> MOVING_UP/DOWN
	}

	/**
	 * Public method used in operate method to verify and set the elevator's current
	 * direction to go up/down/unchanged.
	 * 
	 * future iteration: sort through elevators and check their request lists
	 * 
	 * @author Subear Jama and Farhan Mahamud
	 */
	public void changeDirection() {
		// to set elevator direction, compare elevator floor with subsystem request
		// start floors
		for (Request r : elevator.getElevatorQueue()) {
			int currentFloor = this.elevator.getCurrentFloor();
			int requestStartFloor = r.getFloorNumber();
			int requestDestination = r.getCarButton();

			// if the first request received & hasn't been dealt with yet, set elevator
			// direction and break
			if ((currentFloor < requestStartFloor && r.getReachedStartFloor() == false)
					|| (currentFloor < requestDestination && r.getReachedStartFloor() == true)) {
				this.elevator.setCurrentDirection(Direction.UP);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
				break;
			} else if ((currentFloor > requestStartFloor && r.getReachedStartFloor() == false)
					|| (currentFloor > requestDestination && r.getReachedStartFloor() == true)) {
				this.elevator.setCurrentDirection(Direction.DOWN);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
				break;
			} else if (currentFloor == requestStartFloor && r.getReachedStartFloor() == false) {
				// special case: elevator is already at the floor. set a random direction for
				// the operate while loop to handle
				this.elevator.setCurrentDirection(Direction.IDLE);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
			}
		}

	}

	/**
	 * Public method used in operate method to stop when the elevator has reached
	 * its destination.
	 * 
	 * future iteration: stopElevator method will handle multiple elevator stops
	 * 
	 * @return int, 1 = elevator at starting floor, 2 = destination floor, 0 =
	 *         false, don't stop
	 * @author Subear Jama & Farhan Mahamud
	 */
	public int stopElevator() {

		if (elevator.stopStartFloorCheck()) {
			return 1;
		} else if (elevator.stopDestinationCheck()) {
			return 2;
		}
		return 0;
	}

	/**
	 * This is called by the ElevatorHelper if a slow fault from the scheduler is
	 * detected in this elevator. This stops the elevator until the scheduler tells
	 * it to resume.
	 */
	private void emergencyStop() {
		running = false;
		System.out.println("Elevator " + this.getElevator().getCarNumber()
				+ " encountered a slow running fault and terminated. Emergency services contacted to save the stuck people.");
	}

	/*
	 * Simulate something going wrong with the elevator motor, the elevator should
	 * move slower
	 */
	public void activateSlowFault() {
		System.out.println("Elevator" + elevator.getCarNumber() + " is acting weird and slowed down!");
		elevator.setSlowMode(true);
	}

	public static void main(String[] args) {
		ElevatorSubsystem e = new ElevatorSubsystem(1);
		ElevatorSubsystem e2 = new ElevatorSubsystem(2);
		Thread eThread1 = new Thread(e);
		Thread eThread2 = new Thread(e2);

		ElevatorListener listen = new ElevatorListener(e);
		ElevatorListener listen2 = new ElevatorListener(e2);
		Thread listenThread = new Thread(listen);
		Thread listenThread2 = new Thread(listen2);

		ElevatorFaultListener faultListen = new ElevatorFaultListener(e, eThread1);
		ElevatorFaultListener faultListen2 = new ElevatorFaultListener(e2, eThread2);

		Thread faultListenThread = new Thread(faultListen);
		Thread faultListenThread2 = new Thread(faultListen2);

		eThread1.start();
		eThread2.start();
		listenThread.start();
		listenThread2.start();
		faultListenThread.start();
		faultListenThread2.start();
	}

	@Override
	public void run() {
		while (running) {
			operate();
			this.toString();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Nothing important is to happen, just start the next cycle a little early
			}
		}
	}
}
