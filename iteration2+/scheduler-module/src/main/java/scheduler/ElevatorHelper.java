package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import common.Direction;

/**
 * @author jacob
 *
 */
public class ElevatorHelper implements Runnable {
	private DatagramSocket receiveSocket, sendSocket;
	private DatagramPacket receivePacket, sendPacket;
	private Scheduler scheduler;
	private FloorHelper floorHelper;

	public ElevatorHelper() {
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

		if (receivePacket.getData() == null) {
			scheduler.sendPriorityRequest(Direction.IDLE, 0);
			// TODO convert priority request to datagram sendPacket
			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if (receivePacket.getData().length == 2) {
			// TODO this statement should determine somehow if its data for the floor, maybe
			// length?
			floorHelper.sendPacket(receiveData);
		} else {
			// TODO convert receivePacket to direction and floor
			// scheduler.sendPriorityRequest(direction, floor);
			// TODO convert priority request to datagram sendPacket
			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void run() {
		receiveSendPacket();
	}
}
