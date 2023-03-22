package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import common.ElevatorInfoRequest;
import common.PacketUtils;
import common.Request;

public class ElevatorListener implements Runnable{

	private ElevatorSubsystem elevSys;
	private DatagramPacket sendPacket, receivePacket; // Packets for sending and receiveing
	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets

	public ElevatorListener(ElevatorSubsystem e) {
		elevSys = e;
		setupSocket();
	}
	
	/**
	 * Sets up the socket
	 * @author Farhan Mahamud
	 */
	private void setupSocket() {
		try {
			socket = new DatagramSocket();
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
	 * Method used by the scheduler to update the list of the requests assigned by
	 * the scheduler.
	 * 
	 * @param r Request, the highest priority request from the Scheduler
	 */
	public synchronized void updateFloorQueue() {
		
		Elevator elevator = elevSys.getElevator();
		
		byte[] data = new ElevatorInfoRequest(elevator.getCarNumber(), elevator.getCurrentFloor(), elevator.getCurrentDirection(), elevator.getCurrentElevatorState()).toByteArray();
		byte[] receive = this.sendElevatorRequestPacket(data);

		if (receive[0] == 0 && receive[1] == 0) {
			System.out.println("No new request received");
		} else {
			convertBytesToRequests(receive);
		}

	}
	
	private void convertBytesToRequests(byte[] b) {
		int index = 0;
		
		 while(index < b.length) {
			 if (b[index*36] == 0 && b[index*36 + 1] == 3) {
				 addRequestFromBytes(Arrays.copyOfRange(b, index, index + 35));
				 index += 36;
			 } else {
				 break;
			 }
		 }
	}

	private void addRequestFromBytes(byte[] requestData) {
		Request r = Request.fromByteArray(requestData);
		elevSys.getFloorQueues().add(r);
		
		elevSys.getElevator().getElevatorQueue().add(r);
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
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), PacketUtils.ELEVATOR_PORT); // Initialize packet
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
	
	@Override
	public void run() {
		while(true) {
			this.updateFloorQueue();
		}
		
	}

}
