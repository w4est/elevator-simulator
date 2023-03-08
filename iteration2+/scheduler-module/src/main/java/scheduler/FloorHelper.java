package scheduler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * @author Jacob Hovey
 *
 */
public class FloorHelper implements Runnable {
	private DatagramSocket receiveSocket, sendSocket;
	private DatagramPacket receivePacket, sendPacket;
	private Scheduler scheduler;

	public FloorHelper() {
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
	}

	public void receivePacket() {
		byte[] receiveData = new byte[128]; //how many bytes?
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Scheduler received packet from Floor:\nBytes: " + Arrays.toString(receivePacket.getData()));
		
		//TODO convert data to request information

		scheduler.organizeRequest(null, null);
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

	public void run() {
		receivePacket();
	}
}
