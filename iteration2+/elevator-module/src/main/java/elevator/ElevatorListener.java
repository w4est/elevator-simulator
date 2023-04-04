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

	public ElevatorListener(ElevatorSubsystem e) {
		elevSys = e;
		setupSocket();
	}
	
	public ElevatorListener(ElevatorSubsystem e, DatagramSocket s) {
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
			// Nothing to do, empty packet
		} else {
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

		try {
			this.socket.send(sendPacket); // Sends packet to host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		receivePacket = new DatagramPacket(receiveData, receiveData.length); // Initialize receive packet

		try {
			// Block until a datagram is received via sendReceiveSocket.
			socket.receive(receivePacket); // Waiting to receive packet from host
		} catch (SocketTimeoutException e) {
			return new byte[] { 0, 0 };
		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		return receivePacket.getData();

	}


	@Override
	public void run() {
		while (true) {
			this.updateFloorQueue();
		}

	}

}
