// Import packages
package elevator;

// Import libraries
import java.util.ArrayList;
import scheduler.Request;
import scheduler.Scheduler;

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
	private Scheduler scheduler; // The scheduler associated with the subsystem
	public final static int DEFAULT_MAX_FLOOR = 7; // The default max floor
	public final static int DEFAULT_MIN_FLOOR = 1; // The default min floor
	private final int MAX_FLOOR; // The variable max floor set by the constructor
	private final int MIN_FLOOR; // The variable min floor set by the constructor
	private ArrayList<Request> floorQueues; // The list of requests given by the scheduler
	private boolean operateComplete; // Check if all requests have been moved

	/**
	 * The default constructor if no minimum and maximum floors are not given
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 */
	public ElevatorSubsystem(Scheduler s, int carNumber) {
		this.MIN_FLOOR = DEFAULT_MIN_FLOOR;
		this.MAX_FLOOR = DEFAULT_MAX_FLOOR;
		this.scheduler = s;
		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
		this.operateComplete = false;
	}

	/**
	 * This is another constructor for the user for passing in custom maximum and
	 * minimum floor levels
	 * 
	 * @param s         // The scheduler
	 * @param carNumber // The unique car number for the elevator
	 * @param max       // The maximum floor level
	 * @param min       // The minimum floor level
	 */
	public ElevatorSubsystem(Scheduler s, int carNumber, int max, int min) {
		this.scheduler = s;

		if (min >= max) {
			throw new IllegalArgumentException("Elevator needs at least 2 floors");
		}

		this.MIN_FLOOR = min;
		this.MAX_FLOOR = max;

		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
		this.operateComplete = false;

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

	/**
	 * Method used by the scheduler to update the list of the requests 
	 * assigned by the scheduler.
	 * 
	 * @param r Request, the highest priority request from the Scheduler
	 */
	public synchronized void updateFloorQueue(Request r) {
		floorQueues.add(r);
		// for now all requests in ElevatorSubsystem get added to the 1 elevator.
		// in a future iteration, ElevatorSubsystem will add requests to different elevators.
		this.elevator.addPeople(r); 
		System.out.println(String.format(
				"ElevatorSubsystem Received from Scheduler:		"
						+ " Elevator %d, Requested from Floor %d, Destination Floor: %d",
				elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton()));
		scheduler.requestReceived(elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton());
	}

	/**
	 * Private method repeatedly called in run until the scheduler is done.
	 * Used to setup elevator movement and handle requests.
	 * 
	 * Note: future iteration change this to handle multiple elevators.
	 * @author Subear Jama and Farhan Mahamud
	 */
	private void operate() {
		// 1: check with scheduler to wait for request. 
		// Scheduler sends request stored in floorQueues using updateFloorQueue method
		this.scheduler.elevatorNeeded();
		
		if (!floorQueues.isEmpty()) {
			this.operateComplete = false;
		}
		
		if (!operateComplete) {
			System.out.println("	ElevatorSubsystem: Start Operate Cycle");
			System.out.println("	1. Elevator Current Floor & State: " + 
					"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED
			
			this.elevator.nextElevatorState(); // STOP_OPENED -> STOP_CLOSED
			System.out.println("	2. Elevator Current Floor & State: " + 
			"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED

			// 2: set the elevator's current direction
			changeDirection();
			//System.out.println("	3. Elevator Current Floor & State: " + 
			//		"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // MOVING_UP/DOWN
			
			// special case: if elevator didn't change direction then check for request on current floor
			if (this.elevator.getCurrentElevatorState() == ElevatorState.STOP_CLOSED) {
				if (stopElevator()) {
					this.elevator.nextElevatorState();  // STOP_CLOSED -> STOP_OPENED
					System.out.println("	Special Case: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED
					movePeopleOnElevator(elevator.getCurrentFloor());
				}
			}

			// 3: loop until elevator has moved to the floor the request came from
			while (this.elevator.getCurrentElevatorState() != ElevatorState.STOP_CLOSED && this.elevator.getCurrentElevatorState() != ElevatorState.STOP_OPENED ) {
				if (stopElevator()) {
					this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
					System.out.println("	3: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
					
					this.elevator.nextElevatorState();  // STOP_CLOSED -> STOP_OPENED
					System.out.println("	4: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED
					
					movePeopleOnElevator(elevator.getCurrentFloor());
					break;
				}
				//move elevator up or down 1 floor based on current elevator state
				moveElevator();
				System.out.println("	Moving: Elevator Current Floor & State: " + 
						"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // Floor move
				/*
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

			}

			// 4: if the elevator current floor has reached a request destination (stopped), 
			// remove all requests from that floor.
			/*
			int currentFloor = elevator.getCurrentFloor();
			if (elevator.stop()) {
				int people = elevator.clearFloor();
				System.out.println(
						String.format("%d people have gotten off of the elevator on floor %d", people, currentFloor));
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			// 5: if a request's destination floor matches the elevator's current floor,
			// remove from elevatorSubsystem request queue and add to elevator's request queue
			// NOTE: peopleOnFloor and stopElevator are similar
			//if (peopleOnFloor(currentFloor)) {
			//	movePeopleOnElevator(currentFloor);
			//}

			// check operateComplete condition (have all requests been picked up)
			if (this.elevator.allPeoplePickedUp()) {
				this.operateComplete = true;
			}
		}
		
	}
	
	/**
	 * Private method used in operate to actually move the elevator
	 * up or down 1 floor depending on the elevator's current state
	 * 
	 * @author Subear Jama
	 */
	private void moveElevator() {
		int nextFloorUp = this.elevator.getCurrentFloor() + 1;
		int nextFloorDown = this.elevator.getCurrentFloor() - 1;
		
		if (this.elevator.getCurrentElevatorState() == ElevatorState.MOVING_UP && elevator.getCurrentFloor() <= this.MAX_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorUp);
		} else if (this.elevator.getCurrentElevatorState() == ElevatorState.MOVING_DOWN && elevator.getCurrentFloor() >= this.MIN_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorDown);
		}
	}

	/**
	 * Private method used in operate that checks if a request starting floor from 
	 * ElevatorSubsystem floorQueues matches the elevator's current floor. 
	 * it then removes that request from ElevatorSubsystem.
	 * @param currentFloor int, the elevator's current floor
	 */
	private void movePeopleOnElevator(int currentFloor) {
		int people = 0;
		
		for (int i = floorQueues.size() - 1; i >= 0; i--) {
			if (floorQueues.get(i).getFloorNumber() == currentFloor) {
				people++;
				floorQueues.remove(i);
			}
		} 
		
		System.out.println(String.format("	ElevatorSubsystem: %d request from Floor %d got on the Elevator", people, currentFloor));
	}

	
	/**
	 * Private method used in operate that checks through all requests
	 * to see if a request's destination floor matches the elevator's current floor.
	 * @param currentFloor int, the elevator's current floor
	 * @return boolean, true = a request destination matches current floor, otherwise false.
	 */
	/*
	private boolean peopleOnFloor(int currentFloor) {
		
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getCarButton() == currentFloor) {
				return true;
			}
		}
		return false;
	}*/

	/**
	 * Private method used in operate method to verify and
	 * set the elevator's current direction.
	 * 
	 * NOTE: direction is also used in ElevatorState
	 * future iteration: sort through elevators and check their request lists
	 * @author Subear Jama and Farhan Mahamud
	 */
	private void changeDirection() {
		if (floorQueues.isEmpty()) {
			elevator.setCurrentDirection(Direction.IDLE);
			this.elevator.setElevatorStateManually(ElevatorState.STOP_CLOSED);
			return;
		}
		
		// to set elevator direction, compare elevator floor with subsystem request start floors 
		for (Request r: floorQueues) {
			int currentFloor = this.elevator.getCurrentFloor();
			//if the first request received & hasn't been dealt with yet, set elevator direction and break
			if (currentFloor < r.getFloorNumber() && r.getReachedStartFloor() == false) {
				elevator.setCurrentDirection(Direction.UP);
				break;
			} else if (currentFloor > r.getFloorNumber() && r.getReachedStartFloor() == false) {
				elevator.setCurrentDirection(Direction.DOWN);
				break;
			}
		}
		
		/*
		// elevator needs requests and a way to switch current direction
		// use the first requests direction
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getReachedStartFloor() == false) {
				Direction directionFirstRequest = floorQueues.get(0).getFloorButton();
				elevator.setCurrentDirection(directionFirstRequest);
				break;
			}
		}*/
		
		// Set elevator state based off of elevator direction
		if (elevator.getCurrentDirection().equals(Direction.UP)) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
		} else if (elevator.getCurrentDirection().equals(Direction.DOWN)) {
			this.elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
		} else {
			elevator.setCurrentDirection(Direction.IDLE);
		}
		
		/*
		if (elevator.getCurrentDirection().equals(Direction.UP)) {
			if (goUp()) {
				return;
			} else if (goDown()) {
				elevator.setCurrentDirection(Direction.DOWN);
			}

		} else if (elevator.getCurrentDirection().equals(Direction.DOWN)) {
			if (goDown()) {
				return;
			} else if (goUp()) {
				elevator.setCurrentDirection(Direction.UP);
			}
		} else {
			elevator.setCurrentDirection(Direction.IDLE);
		}*/
		
	}

	/**
	 * Private method used in changeDirection that checks all elevator requests to see 
	 * if one of the requests destnation floor is > than the elevator's current floor.
	 * @return boolean, true = elevator should go up,  otherwise false.
	 * 
	 * NOTE: elevator's queue only gets set at the end when movePeopleOnElevator is called?
	 */
	/*private boolean goUp() {

		ArrayList<Request> elevatorQueue = elevator.getElevatorQueue();

		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() > elevator.getCurrentFloor()
					|| floorQueues.get(i).getCarButton() > elevator.getCurrentFloor()) {
				return true;
			}
		}

		return false;
	}*/
	/*
	private boolean goDown() {

		ArrayList<Request> elevatorQueue = elevator.getElevatorQueue();

		for (int i = 0; i < elevatorQueue.size(); i++) {
			if (elevatorQueue.get(i).getCarButton() < elevator.getCurrentFloor()
					|| floorQueues.get(i).getCarButton() < elevator.getCurrentFloor()) {
				return true;
			}
		}

		return false;
	}*/

	/**
	 * Private method used in operate method to stop when the elevator has
	 * reached its destination. 
	 * 
	 * @return boolean, true = stop elevator, otherwise false.
	 * @author Subear Jama & Farhan Mahamud
	 */
	private boolean stopElevator() {
		
		// In a future iteration, stopElevator method will handle multiple elevator stops
		if (elevator.stop()) {
			return true;
		}
		
		/*
		// check elevatorSubsystem's request queue
		for (int i = 0; i < floorQueues.size(); i++) {
			if (floorQueues.get(i).getCarButton() == elevator.getCurrentFloor()) {
				return true;
			}
		}*/
		return false;
	}

	/**
	 * The run function when you start the thread
	 */
	@Override
	public void run() {

		while (!scheduler.isDone()) {
			/*// NOTE: a high sleep value affects ElevatorSubsystemTest
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			operate();
		}

		System.out.println("ElevatorSubsystem Finished");

	}
}
