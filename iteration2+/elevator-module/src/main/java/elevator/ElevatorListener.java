package elevator;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import common.ElevatorInfoRequest;
import common.PacketUtils;

public class ElevatorListener implements Runnable{
	
	private ElevatorSubsystem elevatorSubsys;
	private DatagramSocket socket;
	
	public ElevatorListener(ElevatorSubsystem e) {
		this.elevatorSubsys = e;
		this.socket = e.socket;
	}

	public void elevatorCommunication() {
		while (true) {
			byte[] send = new ElevatorInfoRequest(elevatorSubsys.getElevator().getCarNumber(), elevatorSubsys.getElevator().getCurrentFloor(), 
					elevatorSubsys.getElevator().getCurrentDirection(), elevatorSubsys.getElevator().getCurrentElevatorState()).toByteArray();
			
			DatagramPacket sendPacket;

			
			try {
				sendPacket = new DatagramPacket(send, send.length, InetAddress.getLocalHost(),
						PacketUtils.SCHEDULER_ELEVATOR_PORT);
				socket.send(sendPacket); // Send packet to client
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Elevator Listener: packet sent to client");
			
			byte receive[] = new byte[150];
			DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
			System.out.println("Elevator Listener: Waiting for Packet from Client.\n");

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
			System.out.println(received);
		}
	}

	@Override
	public void run() {
		this.elevatorCommunication();
		
	}
}
