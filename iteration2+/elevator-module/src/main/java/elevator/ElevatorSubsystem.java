// Import packages
package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
// Import libraries
import java.util.ArrayList;

import common.Direction;
import common.ElevatorInfoRequest;
import common.ElevatorState;
import common.PacketUtils;
import common.Request;

/**
 * This is a class used to simulate an elevator subsystem. This class is used to
 * communicate to the scheduler and receive messages to manage the elevator
 * associated with the subsystem.
 * 
 * @author Farhan Mahamud
 *
 */
public class ElevatorSubsystem {

	private Elevator elevator; // The elevator associated with the subsystem
	private DatagramPacket sendPacket, receivePacket; // Packets for sending and receiveing
	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets
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
	 * @author Farhan Mahamud
	 */
	public ElevatorSubsystem(int carNumber) {
		this.MIN_FLOOR = DEFAULT_MIN_FLOOR;
		this.MAX_FLOOR = DEFAULT_MAX_FLOOR;
		this.elevator = new Elevator(carNumber);
		floorQueues = new ArrayList<>();
		this.operateComplete = false;
		this.elevator.setCurrentFloor(MIN_FLOOR);
		this.setupSocket();
	}

	/**
	 * Constructor used for testing with a Mockito socket
	 * @param carNumber
	 * @param s
	 * @author Farhan Mahamud
	 */
	public ElevatorSubsystem(int carNumber, DatagramSocket s) {
		this(carNumber);
		this.closeSocket();
		this.socket = s;
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
		this.operateComplete = false;
		this.elevator.setCurrentFloor(MIN_FLOOR);
		this.setupSocket();

	}

	/**
	 * Sets up the socket
	 * @author Farhan Mahamud
	 */
	private void setupSocket() {
		try {
			socket = new DatagramSocket(PacketUtils.ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Private method used for closing the socket
	 */
	private void closeSocket() {
		socket.close();
	}

	/**
	 * Public function to send and receive a UDP packet to and from the socket
	 * @param data
	 * @return byte[]
	 * @author Farhan Mahamud
	 */
	public byte[] sendElevatorRequestPacket(byte[] data) {

		byte[] receiveData = new byte[PacketUtils.BUFFER_SIZE];

		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5001); // Initialize packet
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Sending packet");
		printInfo(data);

		try {
			this.socket.send(sendPacket); // Sends packet to host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		receivePacket = new DatagramPacket(receiveData, receiveData.length); // Initialize receive packet

		try {
			// Block until a datagram is received via sendReceiveSocket.
			System.out.println("Elevator: Waiting to receive message from Scheduler");
			socket.receive(receivePacket); // Waiting to receive packet from host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Elevator: Received data");
		printInfo(receivePacket.getData());

		return receivePacket.getData();
	}

	/**
	 * Prints a given array 'message' for a certain amount of length as bytes
	 * 
	 * @param message
	 * @param length
	 * @author Farhan Mahamud
	 */
	public static void printByteArray(byte[] message, int length) {
		System.out.print("Message as bytes: ");
		for (int i = 0; i < length; i++) {
			System.out.print(message[i] + " ");
		}
		System.out.println("");
	}

	/**
	 * Prints a given byte array
	 * @param data
	 * @author Farhan Mahamud
	 */
	private void printInfo(byte[] data) {
		System.out.println(new String(data, 0, data.length)); // or could print "s"

		System.out.print("Message as bytes: ");
		for (int i = 0; i < data.length; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println("");
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
	 * Method used by the scheduler to update the list of the requests assigned by
	 * the scheduler.
	 * 
	 * @param r Request, the highest priority request from the Scheduler
	 */
	public synchronized void updateFloorQueue() {

		byte[] data = new ElevatorInfoRequest((short) elevator.getCurrentFloor(), elevator.getCurrentDirection(),
				elevator.getCurrentElevatorState()).toByteArray();
		byte[] receive = this.sendElevatorRequestPacket(data);

		if (receive[0] == 0 && receive[1] == 2) {
			addRequestFromBytes(receive);
		} else {
			System.out.println("No new request received");
		}

	}

	private void addRequestFromBytes(byte[] requestData) {
		Request r = Request.fromByteArray(requestData);
		floorQueues.add(r);
	}

	/**
	 * Private method repeatedly called in run until the scheduler is done. Used to
	 * setup elevator movement and handle requests.
	 * 
	 * future iteration: change method to handle multiple elevators.
	 * 
	 * @author Subear Jama and Farhan Mahamud
	 */
	private void operate() {
		// 1: check with scheduler to wait for request.
		// Scheduler sends request stored in floorQueues using updateFloorQueue method

		if (!floorQueues.isEmpty()) {
			this.operateComplete = false;
		}

		if (!operateComplete) {
			System.out.println("	ElevatorSubsystem: Start Operate Cycle");
			System.out.println("	1. Elevator Current Floor & State: " + "Floor " + this.elevator.getCurrentFloor()
					+ ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED

			this.elevator.nextElevatorState(); // STOP_OPENED -> STOP_CLOSED
			System.out.println("	2. Elevator Current Floor & State: " + "Floor " + this.elevator.getCurrentFloor()
					+ ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED

			// 2: set the elevator's current direction (MOVING_UP/DOWN)
			changeDirection();

			// 3: Move elevator until it has moved the request
			while (this.elevator.getCurrentElevatorState() != ElevatorState.STOP_OPENED
					&& this.elevator.getCurrentElevatorState() != ElevatorState.STOP_CLOSED) {
				// stop for starting request
				if (stopElevator() == 1) {
					this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
					System.out.println("	3: Elevator Current Floor & State: " + "Floor "
							+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED

					this.elevator.nextElevatorState(); // STOP_CLOSED -> STOP_OPENED
					System.out.println("	4: Elevator Current Floor & State: " + "Floor "
							+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED

					movePeopleOnElevator(elevator.getCurrentFloor()); // in method will go to STOP_CLOSED then
																		// MOVING_UP/DOWN

				}
				// stop for destination request
				else if (stopElevator() == 2) {
					this.elevator.nextElevatorState(); // MOVING_UP/DOWN -> STOP_CLOSED
					System.out.println("	5: Elevator Current Floor & State: " + "Floor "
							+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED

					this.elevator.nextElevatorState(); // STOP_CLOSED -> STOP_OPENED
					System.out.println("	6: Elevator Current Floor & State: " + "Floor "
							+ this.elevator.getCurrentFloor() + ", State: " + this.elevator.getCurrentElevatorState()); // STOP_OPENED

					// elevator reached destination, clear floor and break while loop
					int peopleRemoved = this.elevator.clearFloor();
					System.out.println(
							"	7. Elevator reached destination and dropped off " + peopleRemoved + " request(s)!");
					break;
				}

				// move elevator up or down 1 floor based on current elevator state
				moveElevator();

				this.updateFloorQueue();
			}

			// check operateComplete condition (have all requests been picked up &
			// completed)
			if (this.elevator.allPeoplePickedUp()) {
				this.operateComplete = true;
			}
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
				&& elevator.getCurrentFloor() <= this.MAX_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorUp);
		} else if (this.elevator.getCurrentElevatorState() == ElevatorState.MOVING_DOWN
				&& elevator.getCurrentFloor() >= this.MIN_FLOOR) {
			this.elevator.setCurrentFloor(nextFloorDown);
		}

		System.out.println("	Moving: Elevator Current Floor & State: " + "Floor " + this.elevator.getCurrentFloor()
				+ ", State: " + this.elevator.getCurrentElevatorState()); // Floor move
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
				this.getElevator().addPeople(floorQueues.get(i));
				floorQueues.remove(i);
			}
		}

		System.out.println(String.format("	ElevatorSubsystem: %d request from Floor %d got on the Elevator", people,
				currentFloor));
		// close elevator
		this.elevator.nextElevatorState(); // STOP_OPENED -> STOP_CLOSED
		System.out.println("	Elevator Current Floor & State: " + "Floor " + this.elevator.getCurrentFloor()
				+ ", State: " + this.elevator.getCurrentElevatorState()); // STOP_CLOSED
		// set elevator's direction again
		changeDirection(); // STOP_CLOSED -> MOVING_UP/DOWN
	}

	/**
	 * Public method used in operate method to verify and set the elevator's
	 * current direction to go up/down/unchanged.
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

	public static void main(String args[]) {
		ElevatorSubsystem e = new ElevatorSubsystem(1);

		while (true) {
			e.operate();
		}
	}
}
