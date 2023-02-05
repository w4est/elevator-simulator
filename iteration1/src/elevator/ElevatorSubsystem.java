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
	private ArrayList<Request> floorQueues; // THe list of requests given by the scheduler

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
	 * Updates the list of the requests assigned by the scheduler
	 * 
	 * @param r
	 */
	public synchronized void updateFloorQueue(Request r) {
		floorQueues.add(r);
		System.out.println(String.format("Elevator subsystem has received the following request from the scheduler:"
				+ " Elevator %d, Requested from floor %d, Destination Floor: %d", elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton()));
		scheduler.requestReceived(elevator.getCarNumber(), r.getFloorNumber(), r.getCarButton());
	}

	public void addJob(int destination, int people) {
		System.out.println("Elevator got the request");
	}

	/**
	 * Moves the elevator along the floor and gets any new requests from the
	 * scheduler
	 */
	public synchronized void move() {

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scheduler.elevatorNeeded();

	}

	/**
	 * The run function when you start the thread
	 */
	@Override
	public void run() {

		while (!scheduler.isDone()) {
			move();
		}

		System.out.println("Elevator subsystem is done");

	}
}
