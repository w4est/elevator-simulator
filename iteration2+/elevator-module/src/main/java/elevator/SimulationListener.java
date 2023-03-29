package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import common.ElevatorStatusRequest;
import common.PacketHeaders;
import common.PacketUtils;

public class SimulationListener implements Runnable{

	private ElevatorSubsystem elevSys;
	private DatagramPacket sendPacket, receivePacket; // Packets for sending and receiveing
	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets
	private static boolean debug = false;

	public SimulationListener(ElevatorSubsystem e) {
		elevSys = e;
		setupSocket();
		
	}
	
	public SimulationListener(ElevatorSubsystem e, DatagramSocket s) {
		elevSys = e;
		this.socket = s;
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
	 * Public function to send and receive a UDP packet to and from the socket
	 * 
	 * @param data
	 * @return byte[]
	 * @author Farhan Mahamud
	 */
	public void sendElevatorUpdatePacket() {

		byte[] sendData = new byte[PacketUtils.BUFFER_SIZE];
		byte[] receiveData = new byte[PacketUtils.BUFFER_SIZE];
		
		Elevator elevator = elevSys.getElevator();
		receiveData = new ElevatorStatusRequest(elevator.getCurrentFloor(), elevator.getCurrentDirection(), elevator.getCurrentElevatorState()).toByteArray();

		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(),
					PacketUtils.SIMULATION_PORT); // Initialize packet
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (debug) {
			System.out.println("Elevator: Sending packet");
//			printInfo(data);
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
				System.out.println("Elevator: Waiting to receive message from Simulator");
			}

			socket.receive(receivePacket); // Waiting to receive packet from host
		} catch (SocketTimeoutException e) {
//			return new byte[] { 0, 0 };
		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}

		if (debug) {
			// Process the received datagram.
			System.out.println("Elevator: Received data");
//			printInfo(receivePacket.getData());
		}

	}
	
	@Override
	public void run() {
		sendElevatorUpdatePacket();
	}

}
