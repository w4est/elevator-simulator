package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import common.PacketUtils;

/**
 * Thread constantly checks for faults received from the SimulationGUI calls.
 * A Door Fault will send an interrupt and elevator should fix itself (transient fault)
 * A Slow Fault will slow down the elevator until its forced to shut down.
 * 
 * Note: A new port number is the ELEVATOR_PORT + the elevator number.
 * @author Subear Jama
 */
public class ElevatorFaultListener implements Runnable{
	
	private ElevatorSubsystem elevSys;
	private DatagramPacket receivePacket; // Packet for receiving
	private DatagramSocket receiveSocket;
	private int portNum;
	private Thread elevatorThread;
	
	/**
	 * Constructor sets up a different port number for every elevator
	 * @param e               ElevatorSubsystem, the elevator
	 * @param elevatorThread  Thread, the elevator's thread
	 */
	public ElevatorFaultListener(ElevatorSubsystem e, Thread elevatorThread) {
		this.portNum = PacketUtils.ELEVATOR_PORT + e.getElevator().getCarNumber();
		
		this.elevSys = e;
		this.elevatorThread = elevatorThread;
		try {
			receiveSocket = new DatagramSocket(portNum);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Method constantly called in run() to receive faults
	 */
	private void checkForFaults() {
		
		byte[] receiveData = new byte[PacketUtils.BUFFER_SIZE];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

		// if the ElevatorFaultListener receives a door fault (stuck), elevator should fix itself (transient fault)
		if (receivePacket.getData()[0] == (byte)9 && receivePacket.getData()[1] == (byte)1) {
			//fault function in elevatorsubsystem needed
			System.out.println("DOOR FAULT RECEIVED");
			this.elevatorThread.interrupt();
		} 
		// if the ElevatorListener receives a slow fault, shut down elevator (hard fault)
		else if (receivePacket.getData()[0] == (byte)9 && receivePacket.getData()[1] == (byte)2) {
			System.out.println("SLOW FAULT RECEIVED");
			this.elevSys.emergencyStop();
		}
	}
			
	@Override
	public void run() {
		while (true) {
			this.checkForFaults();
		}

	}

}
