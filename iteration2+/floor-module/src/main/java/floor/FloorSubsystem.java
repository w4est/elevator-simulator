package floor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import common.Direction;
import common.ElevatorInfoRequest;
import common.PacketUtils;
import common.Request;

/**
 * FloorSubsystem Class communicates with Scheduler (Scheduler shared with the Elevator) using UDP.
 * 
 * The FloorSubsystem has 3 Threads:
 *    1 thread receives read requests from the SimulationRunner to store in the FloorSubsystem
 *       this thread also receives info about the Elevator from the Scheduler and updates all the Floors.
 *    1 thread sends new requests to the Scheduler.
 *    1 thread updates the SimulationGUI using FloorSimulationListener class.
 *    
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable {

	private ArrayList<Floor> allFloors;		// All the floors in the FloorSubsystem
	private int peopleWaitingOnAllFloors;	// total people waiting on all the floors
	private final int MAX_FLOOR;
	private ArrayList<Request> allRequests; // Each requests that represents a person
	private boolean firstThreadActive;		//used to differentiate between the 2 threads
	
	private DatagramSocket receiveSocket;	//receiving requests from simulation & info from Scheduler (port 5001)
	private DatagramSocket sendSocket;		//sending requests to scheduler (port 5003)
	private DatagramPacket receivePacket,sendPacket;
	
	/**
	 * FloorSubsystem Constructor sets up the number of floors and DatagramSockets.
	 * @param maxFloor int, represents the number of floors the FloorSubsystem should create.
	 * @param numOfElevators int, the elevators per floor
	 */
	public FloorSubsystem(int maxFloor, int numOfElevators) {
		this.MAX_FLOOR = maxFloor;
		this.allFloors = new ArrayList<Floor>(MAX_FLOOR);
		this.peopleWaitingOnAllFloors = 0;
		this.allRequests = new ArrayList<Request>();
		// initialize all floors to have 0 people
		for (int i = 1; i < MAX_FLOOR + 1; i++) {
			addFloor(i, 0, numOfElevators);
		}
		this.firstThreadActive = false;
		try {
			receiveSocket = new DatagramSocket(PacketUtils.FLOOR_PORT); // Floor port 5001
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {
		      se.printStackTrace();
		      System.exit(1);
	    }
	}
	
	/**
	 * Constructor only used for testing in FloorSubsystemTest.
	 * @param maxFloor int, the max amount of floors in the building
	 * @param s DatagramSocket, the send socket.
	 * @param r DatagramSocket, the receive socket.
	 */
	public FloorSubsystem(int maxFloor, int numOfElevators, DatagramSocket s, DatagramSocket r) {
		this(maxFloor, numOfElevators);
		this.closeSocket(); // close the other constructor's ports and use new sockets
		this.sendSocket = s;
		this.receiveSocket = r;
	}
	
	/**
	 * Getter method used for testing and getting all the requests.
	 */
	public ArrayList<Request> getFloorRequests(){
		return this.allRequests;
	}
	
	/**
	 * Getter method used in FloorSubsystemUpdateGUI to get all the floors to update.
	 */
	public ArrayList<Floor> getAllFloors(){
		return this.allFloors;
	}
	
	/**
	 * Method used in testing to add a requests to the floorsybsystem
	 */
	public void addFloorRequests(Request r) {
		this.allRequests.add(r);
	}
	
	/*
	 * Method used for testing only since floorsubsystem should be running "forever"
	 */
	private void closeSocket() {
		receiveSocket.close();
		sendSocket.close();
	}

	/**
	 * Private method adds a floor to the FloorSubsystem within the Constructor.
	 * It uses the floor's number and the number of people on that floor.
	 * @param floorNumber int, the floor's number.
	 * @param numPeople   int, the number of people on that floor.
	 * @param numOfElevators int, the elevators per floor
	 */
	private void addFloor(int floorNumber, int numPeople, int numOfElevators) {
		if (allFloors.size() != MAX_FLOOR) {
			Floor newFloor = new Floor(floorNumber, numOfElevators);
			newFloor.addNumberOfPeople(numPeople);
			allFloors.add(newFloor);
		} else {
			System.out.println("Error: FloorSubsystem building has max amount of floors "
					+ "(" + MAX_FLOOR + ") and cannot add another floor");
		}
	}
	
	/**
	 * Private method used in operate() to update the 
	 * total number of people waiting on all floors. It does this by resetting the 
	 * count and then adding people from every floor.
	 */
	private void updatePeopleWaitingOnAllFloors() {
		this.peopleWaitingOnAllFloors = 0;
		for (Floor oneFloor : allFloors) {
			this.peopleWaitingOnAllFloors += oneFloor.getNumPeople();
		}
	}

	/**
	 * This method returns the current total amount of people waiting on all floors. 
	 * 
	 * Note: Used to test if data is being passed back and forth + performance tests.
	 * @return The number of people waiting on all floors
	 */
	public int getPeopleWaitingOnAllFloors() {
		return peopleWaitingOnAllFloors;
	}
	
	/**
	 * Private method is used in getElevatorInfoFromScheduler() to remove 1 person from a floor.
	 * @param floorNumber int, the floor number to remove 1 person from.
	 */
	private void removePersonFromFloor(int floorNumber) {
		this.peopleWaitingOnAllFloors -=1;
		for (Floor oneFloor : allFloors) {
			if (floorNumber == oneFloor.getFloorNumber()) {
				oneFloor.removePeople(1);
			}
		}
	}
	
	/**
	 * When the thread starts this communicates with scheduler until all people on all floors have gotten a response
	 * so that they are not waiting.
	 * Steps: 1. Reads request input from file and stores in FloorSubsystem.
	 * 		  2. Sends data to Scheduler to request an elevator. 
	 *		  3. Receives elevator data from scheduler to print that the elevator will arrive.
	 */
	public void operate() {
		// if there are requests that haven't been sent yet, send them to scheduler using sendInfoToScheduler
		// loop through allRequests and send each (new) request to scheduler
		for (Request r: allRequests) {
			// if the request hasn't been sent, send to scheduler (ex. byte array of string "03:50:5.010 1 Up 3")
			if (r.getRequestStatus() == false) {
				sendInfoToScheduler(r.toByteArray());
				r.setRequest(true); //mark request as sent
				System.out.println(
						"FloorSubsystem Sending to Scheduler:    Time: " + r.toString()
								+ ", Departure: Floor " + r.getFloorNumber()
								+ ", Direction: " + r.getFloorButton()
								+ ", Destination: Floor " + r.getCarButton());
			}
		}
	}
	
	/*
	 * Method receives two things from FloorSubsystem port 5001
	 * Case 1: Receiving requests from Simulation to store in FloorSubsystem
	 * Case 2: Receiving message from Scheduler to update FloorSubsystem
	 */
	public void receiveInfo() {
		byte data[] = new byte[PacketUtils.BUFFER_SIZE]; // space for received data (128)
		receivePacket = new DatagramPacket(data, data.length); // for receiving packet 
		try {
			receiveSocket.receive(receivePacket); // Blocked until packet is received
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		
		
		//Case 1: if receivePacket is a request from simulation (first 2 bytes "03") then store in FloorSubsystem
		if (receivePacket.getData()[0] == (byte) 0 && receivePacket.getData()[1] == (byte) 3) {
			// Set up request list
			List<Request> inputRequests = Request.fromByteArray(receivePacket.getData());
			allRequests.add(inputRequests.get(0));
			// Set up Floor for that receive packet (increase # of people and set direction
			for (Floor oneFloor : allFloors) {

				for (Request inputRequest : inputRequests) {
					if (oneFloor.getFloorNumber() == inputRequest.getFloorNumber()) {
						// anytime there's another floor keep adding (incrementing) # of people
						oneFloor.addNumberOfPeople(1);
						this.peopleWaitingOnAllFloors += 1;
						if (inputRequest.getFloorButton() == Direction.UP) {
							oneFloor.setUpButton(true);
						} else if (inputRequest.getFloorButton() == Direction.DOWN) {
							oneFloor.setDownButton(true);
						}
					}
				}
			}
		}
		
		//**Case 2: if the packet is from the scheduler (first 2 bytes "01"), update the FloorSubsystem lamp
		else if(receivePacket.getData()[0] == (byte)0 && receivePacket.getData()[1] == (byte)1) {
			ElevatorInfoRequest elevatorStatus = ElevatorInfoRequest.fromByteArray(receivePacket.getData());

			// check through all Floors and remove people from floor if the elevator is on it
			for (Floor f: allFloors) {
				//update floor lamp for every floor
				f.setFloorLamp(elevatorStatus.getCarNumber(),elevatorStatus.getFloorNumber()); 
				if (f.getFloorNumber() == elevatorStatus.getFloorNumber()) {
					this.removePersonFromFloor(f.getFloorNumber()); //remove person from floor
					//turn off lamp (can use setUpButton or setDownButton, will turn off both)
					f.setUpButton(false);
				}
			}
		}
		updatePeopleWaitingOnAllFloors(); //update peopleWaitingOnAllFloors count

	}	
	
	/**
	 * Method used in run() to send requests to the scheduler via port 5003
	 * @param requestByte byte[],the request to store into a packet and send
	 */
	public void sendInfoToScheduler(byte[] requestByte) {
		try {
			sendPacket = new DatagramPacket(requestByte, requestByte.length,InetAddress.getLocalHost(), PacketUtils.SCHEDULER_FLOOR_PORT); // scheduler port 5003
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	@Override
	public void run() {
		if (firstThreadActive == false) {
			// "receiving thread" for receiving both scheduler and simulation's read requests (port 5001)
			firstThreadActive = true;
			System.out.println("FloorSubsystem's Receive Thread");
			synchronized(this) {
				while(true) {
					//either receive request from simulation and update floor subsystem
					//or receive info from scheduler (lamp, etc) and update floor subsystem
					while (!firstThreadActive) {
						try {
							wait();
						} catch (InterruptedException e) {
							System.err.println(e);
						}
					}
					receiveInfo();
					firstThreadActive = false;
					notifyAll();
				}
			}
			
		} else {
			// "sending thread" for sending requests to scheduler (port 5003)
			System.out.println("FloorSubsystem's Send Thread");
			synchronized(this) {
				while(true) {
					while (firstThreadActive) {
						try {
							wait();
						} catch (InterruptedException e) {
							System.err.println(e);
						}
					}
					operate();
					updatePeopleWaitingOnAllFloors(); //update peopleWaitingOnAllFloors count
					
					firstThreadActive = true;
					notifyAll();
				}
			}
		}
		
	}
	
	public static void main(String[] args) {

		Optional<Integer> maxFloor = getElevatorMaxFloorInArgs(args);
		Optional<Integer> numElevators = getElevatorNumberInArgs(args);

		
		FloorSubsystem fs = new FloorSubsystem(maxFloor.orElse(22), numElevators.orElse(4)); // Create floor object (# of floors, # of elevators)
		FloorSimulationListener fsListener = new FloorSimulationListener(fs);
		// Create & start 2 threads with same object
		// thread "FloorSubstystem sending" for sending requests to the scheduler (port 5003)
		// thread "FloorSubsystem receiving" for receiving request input from simulation & info from Scheduler (port 5001)
		Thread floorThread1 = new Thread(fs, "FloorSubsystem1");  // Create thread passing its respective object
		Thread floorThread2 = new Thread(fs, "FloorSubsystem2");  // Another thread with same object
		Thread floorUpdateThread = new Thread(fsListener, "FloorSimulationListener"); // Thread used for updating GUI
		floorThread1.start();                                 // Starts run() method for thread
		floorThread2.start();
		floorUpdateThread.start();
	}
	
	private static Optional<Integer> getElevatorMaxFloorInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--max_floor") && i + 1 < max) {
				return Optional.ofNullable(Integer.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}

	private static Optional<Integer> getElevatorNumberInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--elevator_count") && i + 1 < max) {
				return Optional.ofNullable(Integer.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}
}