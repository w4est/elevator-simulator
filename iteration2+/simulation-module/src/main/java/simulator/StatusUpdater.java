package simulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import common.ElevatorStatusRequest;
import common.FloorStatusRequest;
import common.PacketHeaders;
import common.PacketUtils;

/**
 * Implements a thread that listens for status updates from the elevator and floors
 * @author William Forrest
 *
 */
public class StatusUpdater implements Runnable {

	private SimulationGUI gui;
	private Simulation simulation;
	private boolean running = true;
	
	private ElevatorStatusRequest[] lastUpdate;
	private long[] lastUpdateTime;
	private long startTime = 0L;
	// To determine when the simulation is done, we need to have an all idle elevator list,
	// but, we want to do it a few times to ensure we are stable
	private boolean[] elevatorStable;
	private static final long stabilityDelta = 500;
	
	public StatusUpdater(SimulationGUI gui, int elevatorCount, Simulation simulation) {
		this.gui = gui;
		this.simulation = simulation;
		this.startTime = System.currentTimeMillis();
		this.lastUpdate = new ElevatorStatusRequest[elevatorCount];
		this.lastUpdateTime = new long[elevatorCount];
		this.elevatorStable = new boolean[elevatorCount];
		
		for (int i = 0; i < elevatorCount; i++) {
			elevatorStable[i] = false;
			lastUpdateTime[i] = Long.MAX_VALUE; // Make sure we can't have a delta too big on the first evaluation.
		}
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
				gui.updateState(elevatorStatus);
				
				if (checkIfFinished(elevatorStatus)) {
					running = false;
					// Send our result
					gui.simulationComplete(System.currentTimeMillis() - startTime - stabilityDelta);
				}
			} 
			// if the packet is from the FloorSubsystem, update the GUI
			else if (Arrays.equals(header, PacketHeaders.FloorStatus.getHeaderBytes())) {
				FloorStatusRequest floorStatus = FloorStatusRequest.fromByteArray(packet.getData());
				gui.updateFloor(floorStatus);
			}

		} catch (IOException e) {
			// Keep going
		}
	}
	
	/*
	* We define a simulation finished if:
	*   - Every elevator has no requests pending (Or, is broken!)
	*   - Every elevator has had no changes in it's status for 500+ms
	*/
	private boolean checkIfFinished(ElevatorStatusRequest request) {
		int arrayPosition = request.getElevatorNumber() - 1;
		ElevatorStatusRequest lastStatusUpdate = lastUpdate[arrayPosition];
		long lastStatusUpdateTime = lastUpdateTime[arrayPosition];
		
		if (simulation.allRequestsComplete() && lastStatusUpdate != null && (request.getPendingRequests() == 0 || request.isBroken())
				&& lastStatusUpdate.equals(request)) {
			if (System.currentTimeMillis() - lastStatusUpdateTime > stabilityDelta) {
				elevatorStable[arrayPosition] = true;
			} 
			
			// Else we aren't stable yet, but we don't need to update our time and request.
		} else {
			elevatorStable[arrayPosition] = false;
			lastUpdateTime[arrayPosition] = System.currentTimeMillis();
			lastUpdate[arrayPosition] = request;
		}
		
		// Check that every elevator is marked stable
		for (boolean stable : elevatorStable) {
			if (!stable) {
				return false;
			}
		}
		
		return true;
	}
}
