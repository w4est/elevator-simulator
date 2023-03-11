package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import common.ElevatorInfoRequest;
import common.PacketUtils;
import common.Request;

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

		System.out.println("Scheduler received packet from Elevator:\nBytes: "
				+ Arrays.toString(receivePacket.getData()) + "\nString: " + new String(receivePacket.getData()));

		ElevatorInfoRequest elevatorStatus = ElevatorInfoRequest.fromByteArray(receiveData);

		Request priorityRequest = scheduler.sendPriorityRequest(elevatorStatus.getDirection(),
				elevatorStatus.getFloorNumber());

		byte[] sendData = new byte[PacketUtils.BUFFER_SIZE];

		if (priorityRequest == null) {
			sendData = new byte[2];
			sendData[0] = (byte) 0;
			sendData[1] = (byte) 0;
		} else {
			sendData = priorityRequest.toByteArray();
		}

		sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		floorHelper.sendPacket(receivePacket.getData());
	}

	public void run() {
		while (true) {
			receiveSendPacket();
		}
	}
}
