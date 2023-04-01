package simulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import common.ElevatorStatusRequest;
import common.PacketHeaders;
import common.PacketUtils;

/**
 * Implements a thread that listens for status updates from the elevator and floors
 * @author William Forrest
 *
 */
public class StatusUpdater implements Runnable {

	private SimulationGUI gui;
	private boolean running = true;
	
	public StatusUpdater(SimulationGUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void run() {

		try (DatagramSocket listenSocket = new DatagramSocket(PacketUtils.SIMULATION_PORT)) {
			listenSocket.setSoTimeout(500);

			while (running) {

				checkForUpdates(listenSocket);
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}
	
	private void checkForUpdates(DatagramSocket listenSocket) {
		try {

			byte[] buffer = new byte[PacketUtils.BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			listenSocket.receive(packet);
			
			byte[] header = Arrays.copyOf(packet.getData(), 2);
			if (Arrays.equals(header, PacketHeaders.ElevatorStatus.getHeaderBytes())) {
				
				// update the status
				ElevatorStatusRequest elevatorStatus = ElevatorStatusRequest.fromByteArray(packet.getData());
				gui.updateState(elevatorStatus.getElevatorNumber(), elevatorStatus.getFloorNumber(),
						elevatorStatus.getState());
			}

		} catch (IOException e) {
			// Keep going
		}
	}
}
