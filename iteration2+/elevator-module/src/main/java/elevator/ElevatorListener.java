package elevator;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ElevatorListener {
	
	private ElevatorSubsystem elevatorSubsys;
	private DatagramSocket socket;
	
	public ElevatorListener(ElevatorSubsystem e) {
		this.elevatorSubsys = e;
		this.socket = e.socket;
	}

	public void elevatorCommunication() {
		while (true) {
			byte receive[] = new byte[150];
			DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
			System.out.println("Intermediate Host: Waiting for Packet from Client.\n");

			// Block until a datagram packet is received from receiveSocket.
			try {
				System.out.println("Waiting..."); // so we know we're waiting
				socket.receive(receivePacket); // wait for client to send packet
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1);
			}

			int len = receivePacket.getLength();

			String received = new String(receive, 0, len);
			System.out.println(received + "\n");

			byte[] send;
			// Initialize new packet to send to client
			
			String message = String.format("", null);
			
			send = elevatorSubsys.getElevator().getCurrentElevatorState().toString().getBytes();

			DatagramPacket sendPacket = new DatagramPacket(send, send.length, receivePacket.getAddress(),
					receivePacket.getPort());

			len = sendPacket.getLength();
			
			
			
			try {
				socket.send(sendPacket); // Send packet to client
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Intermediate Host: packet sent to client");
		}
	}
}
