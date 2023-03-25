package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.PacketUtils;

/**
 * Thread constantly checks for faults from the FloorSubsystem (using ELEVATOR_PORT)
 * 
 * @author subea
 *
 */
public class ElevatorFaultListener implements Runnable{
	
	private ElevatorSubsystem elevSys;
	private DatagramPacket receivePacket; // Packet for receiving
	private DatagramSocket receiveSocket;
	private int portNum;
	private Thread elevatorThread;
	
	public ElevatorFaultListener(ElevatorSubsystem e, Thread elevatorThread) {
		portNum = PacketUtils.ELEVATOR_PORT + e.getElevator().getCarNumber();
		
		elevSys = e;
		this.elevatorThread = elevatorThread;
		try {
			receiveSocket = new DatagramSocket(portNum);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	public ElevatorFaultListener(ElevatorSubsystem e, Thread elevatorThread, DatagramSocket s) {		
		elevSys = e;
		this.elevatorThread = elevatorThread;
		receiveSocket = s;
	}
	
	public void checkForFaults() {
		
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
			System.out.println("DOOR FAULT RECEIVED for Elevator " + this.elevSys.getElevator().getCarNumber());
			this.elevatorThread.interrupt();
		} 
		// if the ElevatorListener receives a slow fault, shut down elevator (hard fault)
		else if (receivePacket.getData()[0] == (byte)9 && receivePacket.getData()[1] == (byte)2) {
			System.out.println("SLOW FAULT RECEIVED for Elevator " + this.elevSys.getElevator().getCarNumber());
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
