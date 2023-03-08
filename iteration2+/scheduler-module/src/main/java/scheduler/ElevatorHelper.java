package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * @author jacob
 *
 */
public class ElevatorHelper implements Runnable {
	private DatagramSocket receiveSocket, sendSocket;
	private DatagramPacket receivePacket, sendPacket;
	private Scheduler scheduler;
	private FloorHelper floorHelper;

	public ElevatorHelper(Scheduler scheduler, FloorHelper floorHelper) {
		try {
			receiveSocket = new DatagramSocket(5004);
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
		this.floorHelper = floorHelper;
	}

	public void receiveSendPacket() {
		byte[] receiveData = new byte[128];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(
				"Scheduler received packet from Elevator:\nBytes: " + Arrays.toString(receivePacket.getData()));

		// TODO convert receivePacket to direction and floor (format: byte of current floor, byte of direction (to byte method), byte of state)
		// scheduler.sendPriorityRequest(direction, floor);
		// TODO convert priority request to datagram sendPacket
		// TODO logic to send floor relevant info 
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		while (true) {
			receiveSendPacket();
		}
	}
}
