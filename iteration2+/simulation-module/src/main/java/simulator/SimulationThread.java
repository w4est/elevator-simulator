package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Wrapper the runs the simulation in a runnable, so it can be threaded with a gui nicely.
 * @author William Forrest
 */
public class SimulationThread implements Runnable {

	String[] args;
	
	public SimulationThread(String[] args) {
		this.args = args;
	}
	
	@Override
	public void run() {
		try (DatagramSocket socket = new DatagramSocket()) {
			// Run headless
			System.out.println("Starting simulation");
			Simulation sim = new Simulation(args, socket);
			sim.runSimulation();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
