package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import common.ElevatorStatusRequest;
import common.PacketUtils;

/**
 * Class used to communicate with simulation for status updates
 * @author Farhan Mahamud
 *
 */
public class SimulationListener implements Runnable {

	private ElevatorSubsystem elevSys;
	private DatagramPacket sendPacket; // Packets for sending and receiveing
	protected DatagramSocket socket; // Socket used for sending and receiving UDP packets
	private static boolean debug = false;

	public SimulationListener(ElevatorSubsystem e) {
		elevSys = e;
		setupSocket();

	}

	public SimulationListener(ElevatorSubsystem e, DatagramSocket s) {
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
	 * Public function to send and receive a UDP packet to and from the socket
	 * 
	 * @param data
	 * @return byte[]
	 * @author Farhan Mahamud
	 */
	public void sendElevatorUpdatePacket() {

		byte[] sendData = new byte[PacketUtils.BUFFER_SIZE];

		Elevator elevator = elevSys.getElevator();
		sendData = new ElevatorStatusRequest(elevator.getCarNumber(), elevator.getCurrentFloor(),
				elevator.getCurrentDirection(), elevator.getCurrentElevatorState()).toByteArray();

		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(),
					PacketUtils.SIMULATION_PORT); // Initialize packet
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (debug) {
			System.out.println("Elevator: Sending packet");
//			printInfo(data);
		}

		try {
			this.socket.send(sendPacket); // Sends packet to host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendElevatorUpdatePacket();
		}
	}

}
