package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import common.ElevatorInfoRequest;
import common.PacketUtils;
import common.Request;

public class ElevatorListener implements Runnable {

	private ElevatorSubsystem elevSys;
	private DatagramPacket sendPacket, receivePacket; // Packets for sending and receiveing
	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets
	private static boolean debug = false;

	public ElevatorListener(ElevatorSubsystem e) {
		elevSys = e;
		setupSocket();
	}

	/**
	 * Sets up the socket
	 * 
	 * @author Farhan Mahamud
	 */
	private void setupSocket() {
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Method used by the scheduler to update the list of the requests assigned by
	 * the scheduler.
	 * 
	 * @param r Request, the highest priority request from the Scheduler
	 */
	public synchronized void updateFloorQueue() {

		Elevator elevator = elevSys.getElevator();

		byte[] data = new ElevatorInfoRequest(elevator.getCarNumber(), elevator.getCurrentFloor(),
				elevator.getCurrentDirection(), elevator.getCurrentElevatorState()).toByteArray();
		byte[] receive = this.sendElevatorRequestPacket(data);

		if (receive[0] == 0 && receive[1] == 0) {
			if (debug) {
				System.out.println("No new request received");
			}
		} 
		// if the ElevatorListener receives a door fault (stuck), elevator should fix itself (transient fault)
		else if (receive[0] == (byte)9 && receive[1] == (byte)1){
			//fault function in elevatorsubsystem needed
		}
		// if the ElevatorListener receives a slow fault, shut down elevator (hard fault)
		else if (receive[0] == (byte)9 && receive[1] == (byte)2){
			elevSys.emergencyStop();
		}
		else {
			addRequestsFromBytes(receive);
		}

	}

	/**
	 * Adds a request to the elevator based on packet bytes received from the
	 * scheduler.
	 * 
	 * @param requestData byte[], the request info received from the scheduler.
	 */
	private void addRequestsFromBytes(byte[] requestData) {
		List<Request> requests = Request.fromByteArray(requestData);

		elevSys.addRequests(requests);
	}

	/**
	 * Public function to send and receive a UDP packet to and from the socket
	 * 
	 * @param data
	 * @return byte[]
	 * @author Farhan Mahamud
	 */
	public byte[] sendElevatorRequestPacket(byte[] data) {

		byte[] receiveData = new byte[PacketUtils.BUFFER_SIZE];

		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),
					PacketUtils.SCHEDULER_ELEVATOR_PORT); // Initialize packet
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (debug) {
			System.out.println("Elevator: Sending packet");
			printInfo(data);
		}

		try {
			this.socket.send(sendPacket); // Sends packet to host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		receivePacket = new DatagramPacket(receiveData, receiveData.length); // Initialize receive packet

		try {
			// Block until a datagram is received via sendReceiveSocket.
			if (debug) {
				System.out.println("Elevator: Waiting to receive message from Scheduler");
			}

			socket.receive(receivePacket); // Waiting to receive packet from host
		} catch (SocketTimeoutException e) {
			return new byte[] { 0, 0 };
		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}

		if (debug) {
			// Process the received datagram.
			System.out.println("Elevator: Received data");
			printInfo(receivePacket.getData());
		}

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
	 * 
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

	@Override
	public void run() {
		while (true) {
			this.updateFloorQueue();
		}

	}

}
