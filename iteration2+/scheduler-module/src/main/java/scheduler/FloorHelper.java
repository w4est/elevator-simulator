package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

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
			receiveSocket = new DatagramSocket(5003);
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

		System.out.println("Scheduler received packet from Floor:\nBytes: " + Arrays.toString(receivePacket.getData())
				+ "\nString: " + new String(receivePacket.getData()));

		Request newRequest = Request.fromByteArray(receiveData);

		scheduler.organizeRequest(newRequest.getLocalTime(), newRequest);

		System.out.println("Scheduler added request to the queue.");
	}

	public void sendPacket(byte[] sendData) {
		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), 5001);
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
