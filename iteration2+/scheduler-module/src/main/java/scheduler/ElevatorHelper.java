package scheduler;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import common.ElevatorInfoRequest;
import common.PacketHeaders;
import common.PacketUtils;
import common.Request;

/**
 * @author Jacob Hovey
 * 
 *         The ElevatorHelper is a thread of the Scheduler module that helps
 *         with receiving status update packets from the Elevator subsystem and
 *         sending back requests that fit their current status. It also passes
 *         the updates along to the floor.
 *
 */
public class ElevatorHelper implements Runnable {
	private DatagramSocket receiveSocket, sendSocket;
	private DatagramPacket receivePacket, sendPacket;
	private Scheduler scheduler;
	private FloorHelper floorHelper;

	/**
	 * The constructor for the ElevatorHelper.
	 * 
	 * @param scheduler   Scheduler, the main scheduler class that this thread
	 *                    references.
	 * @param floorHelper FloorHelper, the FloorHelper thread that this thread
	 *                    references.
	 */
	public ElevatorHelper(Scheduler scheduler, FloorHelper floorHelper) {
		try {
			receiveSocket = new DatagramSocket(PacketUtils.SCHEDULER_ELEVATOR_PORT);
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

	/**
	 * The receiveSendPacket method checks for packets from the Elevator Subsystem
	 * that include elevator status information. When a packet is received, the
	 * information is sent to the main Scheduler class and the priority request is
	 * sent back. This is sent to the elevator and the status update is also passed
	 * along to the floor, to control lamps and buttons.
	 */
	public void receiveSendPacket() {
		byte[] receiveData = new byte[128];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Scheduler received update packet from Elevator.");

		ElevatorInfoRequest elevatorStatus = ElevatorInfoRequest.fromByteArray(receiveData);

		byte[] packetHeader = Arrays.copyOf(receivePacket.getData(), 2);
		if (Arrays.equals(PacketHeaders.ElevatorInfoRequest.getHeaderBytes(), packetHeader)) {
			ArrayList<Request> sendRequests = scheduler.sendRequests(elevatorStatus.getDirection(),
					elevatorStatus.getFloorNumber());

			byte[] sendData = new byte[PacketUtils.BUFFER_SIZE];

			// "empty" packet denoting that there are no new requests for this elevator
			// thread to service.
			if (sendRequests == null) {
				sendData = new byte[2];
				sendData[0] = (byte) 0;
				sendData[1] = (byte) 0;
			} else {
				ByteBuffer byteBuffer = ByteBuffer.wrap(sendData);
				while (sendRequests.size() != 0) {
					byteBuffer.put(sendRequests.get(0).toByteArray());
					sendRequests.remove(0);
				}
			}

			sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(),
					receivePacket.getPort());

			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("Scheduler sent request packet to Elevator.");

			floorHelper.sendPacket(receivePacket.getData());

			System.out.println("Scheduler passed update packet from Elevator to Floor.");
		}
	}

	/**
	 * The run method of the ElevatorHelper thread. This consistently loops through
	 * the receiveSendPacket method.
	 */
	public void run() {
		while (true) {
			receiveSendPacket();
			// Between updates, sleeps briefly; this is temporary for while we are not using
			// realtime. If this is not done, requests come through to the elevator too fast
			// and the print statements are overwhelming, making it hard to see any relevant
			// information.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
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
