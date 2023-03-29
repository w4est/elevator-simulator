package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

import common.PacketHeaders;
import common.PacketUtils;
import common.Request;

/**
 * @author Jacob Hovey
 *
 *         The FloorHelper is a thread of the Scheduler module that helps with
 *         receiving Request packets from the Floor subsystem and organizing
 *         them in the scheduler.
 */
public class FloorHelper implements Runnable {
	private DatagramSocket receiveSocket, sendSocket;
	private DatagramPacket receivePacket, sendPacket;
	private Scheduler scheduler;

	/**
	 * The constructor for the FloorHelper.
	 * 
	 * @param scheduler Scheduler, the main scheduler class that this thread
	 *                  references.
	 */
	public FloorHelper(Scheduler scheduler) {
		try {
			receiveSocket = new DatagramSocket(PacketUtils.SCHEDULER_FLOOR_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.scheduler = scheduler;
	}

	/**
	 * The receivePacket method checks for packets from the Floor Subsystem that
	 * include Request information. When a packet is received, the Request
	 * information is sent to the main Scheduler class and organized into the list
	 * of requests that need to be sent to the elevators.
	 */
	public void receivePacket() {
		byte[] receiveData = new byte[PacketUtils.BUFFER_SIZE];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Scheduler received request packet from Floor.");

		byte[] packetHeader = Arrays.copyOf(receivePacket.getData(), 2);
		if (Arrays.equals(PacketHeaders.Request.getHeaderBytes(), packetHeader)) {
			List<Request> newRequests = Request.fromByteArray(receivePacket.getData());
			scheduler.organizeRequest(newRequests.get(0).getLocalTime(), newRequests.get(0));
			System.out.println("Scheduler added request to the queue.");
		}
	}

	/**
	 * This sends a packet to the floor with any update information received from
	 * the elevator, for the purpose of controlling lamps and displays.
	 * 
	 * @param sendData byte[], the relevant data to be passed along.
	 */
	public void sendPacket(byte[] sendData) {
		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), PacketUtils.FLOOR_PORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * The run method of the FloorHelper thread. This consistently loops through the
	 * receivePacket method.
	 */
	public void run() {
		while (true) {
			receivePacket();
		}
	}

	/**
	 * setter method for the sendSocket; used for testing.
	 * 
	 * @param sendSocket DatagramSocket, the socket to use to replace the sender.
	 */
	public void setSendSocket(DatagramSocket sendSocket) {
		this.sendSocket.close();
		this.sendSocket = sendSocket;
	}

	/**
	 * setter method for the receiveSocket; used for testing.
	 * 
	 * @param receiveSocket DatagramSocket, the socket to use to replace the
	 *                      receiver.
	 */
	public void setReceiveSocket(DatagramSocket receiveSocket) {
		this.receiveSocket.close();
		this.receiveSocket = receiveSocket;
	}
}