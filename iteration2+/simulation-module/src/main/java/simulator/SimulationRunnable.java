package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Wrapper the runs the simulation in a runnable, so it can be threaded with a gui nicely.
 * @author William Forrest
 */
public class SimulationRunnable implements Runnable {

	private Simulation sim;
	private final DatagramSocket socket;
	
	public SimulationRunnable(String[] args) throws SocketException {
		this.socket = new DatagramSocket();
		this.sim = new Simulation(args, socket);
	}
	
	@Override
	public void run() {
		try (socket) {
			// Run headless
			System.out.println("Starting simulation");
			sim.runSimulation();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Simulation getSimulation() {
		return sim;
	}

}
