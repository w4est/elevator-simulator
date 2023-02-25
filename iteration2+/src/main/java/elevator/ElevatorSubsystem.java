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
	 * future iteration: change method to handle multiple elevators.
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

			// 2: set the elevator's current direction (MOVING_UP/DOWN)
			changeDirection();

			// 3: Move elevator until it has moved the request
			while (this.elevator.getCurrentElevatorState() != ElevatorState.STOP_OPENED && this.elevator.getCurrentElevatorState() != ElevatorState.STOP_CLOSED) {
				//stop for starting request
				if (stopElevator() == 1) {
					this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
					System.out.println("	3: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
					
					this.elevator.nextElevatorState();  // STOP_CLOSED -> STOP_OPENED
					System.out.println("	4: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED
					
					movePeopleOnElevator(elevator.getCurrentFloor()); // in method will go to STOP_CLOSED then MOVING_UP/DOWN
					
				} 
				//stop for destination request
				else if (stopElevator() == 2) {
					this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
					System.out.println("	5: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
					
					this.elevator.nextElevatorState();  // STOP_CLOSED -> STOP_OPENED
					System.out.println("	6: Elevator Current Floor & State: " + 
							"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED
					
					// elevator reached destination, clear floor and break while loop
					int peopleRemoved = this.elevator.clearFloor();
					System.out.println("	7. Elevator reached destination and dropped off " + peopleRemoved + " request(s)!");
					break;
				}
				
				//move elevator up or down 1 floor based on current elevator state
				moveElevator();
			}
			

			// check operateComplete condition (have all requests been picked up & completed)
			if (this.elevator.allPeoplePickedUp()) {
				this.operateComplete = true;
			}
		}
		
	}
	
	/**
	 * Private method used in operate to actually move the elevator
	 * up or down 1 floor depending on the elevator's current state.
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
		System.out.println("	Moving: Elevator Current Floor & State: " + 
				"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // Floor move
		
		//Notify scheduler that elevator has moved
		this.scheduler.notifyElevatorArrival(this.elevator.getCarNumber(),this.elevator.getCurrentFloor());
	}

	/**
	 * Private method used in operate that checks if a request starting floor from 
	 * ElevatorSubsystem floorQueues matches the elevator's current floor. 
	 * it then removes that request from ElevatorSubsystem.
	 * 
	 * @param currentFloor int, the elevator's current floor
	 * @author Farhan Mahamud and Subear Jama
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
		// close elevator
		this.elevator.nextElevatorState();  // STOP_OPENED -> STOP_CLOSED
		System.out.println("	Elevator Current Floor & State: " + 
				"Floor "+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
		//set elevator's direction again
		changeDirection(); //STOP_CLOSED -> MOVING_UP/DOWN
	}

	/**
	 * Private method used in operate method to verify and
	 * set the elevator's current direction to go up/down/unchanged.
	 * 
	 * future iteration: sort through elevators and check their request lists
	 * @author Subear Jama and Farhan Mahamud
	 */
	private void changeDirection() {
		// to set elevator direction, compare elevator floor with subsystem request start floors 
		for (Request r: elevator.getElevatorQueue()) {
			int currentFloor = this.elevator.getCurrentFloor();
			int requestStartFloor = r.getFloorNumber();
			int requestDestination = r.getCarButton();
			
			//if the first request received & hasn't been dealt with yet, set elevator direction and break
			if ((currentFloor < requestStartFloor && r.getReachedStartFloor() == false) 
					|| (currentFloor < requestDestination && r.getReachedStartFloor() == true)) {
				this.elevator.setCurrentDirection(Direction.UP);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
				break;
			} else if ((currentFloor > requestStartFloor && r.getReachedStartFloor() == false) ||
					(currentFloor > requestDestination && r.getReachedStartFloor() == true)) {
				this.elevator.setCurrentDirection(Direction.DOWN);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_DOWN);
				break;
			} else if (currentFloor == requestStartFloor && r.getReachedStartFloor() == false) {
				//special case: elevator is already at the floor. set a random direction for the operate while loop to handle
				this.elevator.setCurrentDirection(Direction.IDLE);
				this.elevator.setElevatorStateManually(ElevatorState.MOVING_UP);
			}
		}

	}

	/**
	 * Private method used in operate method to stop when the elevator has
	 * reached its destination. 
	 * 
	 * future iteration: stopElevator method will handle multiple elevator stops
	 * @return int, 1 = elevator at starting floor, 2 = destination floor, 0 = false, don't stop
	 * @author Subear Jama & Farhan Mahamud
	 */
	private int stopElevator() {
		
		if (elevator.stopStartFloorCheck()) {
			return 1;
		}
		else if (elevator.stopDestinationCheck()) {
			return 2;
		}
		return 0;
	}

	/**
	 * The run function when you start the thread
	 */
	@Override
	public void run() {

		while (!scheduler.isDone()) {
			operate();
		}

		System.out.println("ElevatorSubsystem Finished");

	}
}
