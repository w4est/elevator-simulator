package floor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.FloorStatusRequest;
import common.PacketUtils;

/**
 * Update the SimulationGUI with a packet having each floor's:
 *  - Floor Number
 *  - Number of people
 *  - Floor up and down button light status
 *  - Floor lamp showing elevator num and current position
 * @author Subear Jama
 */
public class FloorSimulationListener implements Runnable{
	private FloorSubsystem floorSys;
	private DatagramPacket sendPacket;
	protected DatagramSocket socket;  // sending & receiving UDP packets

	public FloorSimulationListener(FloorSubsystem f) {
		this.floorSys = f;
		try {
			socket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

	}


	public void floorSubsystemUpdatePacket() {
		
		// for every floor...
		for (Floor f: floorSys.getAllFloors()) {
			// for a floor's different elevator lamps
			int elevatorNum = 1;
			for (Integer elevFloorLamp: f.getFloorLamp()) {
				byte[] sendData = new byte[PacketUtils.BUFFER_SIZE];
				sendData = new FloorStatusRequest(f.getFloorNumber(), f.getNumPeople(), f.getUpButton(), f.getDownButton(), elevatorNum, elevFloorLamp).toByteArray();
				try {
					sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(),PacketUtils.SIMULATION_PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				//System.out.println("FloorSubsystem: Sending GUI packet");
				try {
					this.socket.send(sendPacket); // Sends packet to host
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				elevatorNum++;
			}
		}
	}

	@Override
	public void run() {
		System.out.println("FloorSubsystem's GUI Thread");
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			floorSubsystemUpdatePacket();
		}
	}

}
