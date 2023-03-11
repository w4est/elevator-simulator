package simulator;

import java.io.IOException;
import java.net.DatagramSocket;

public class SimulationRunner {
	public static void main(String[] args) throws IOException {
		try (DatagramSocket socket = new DatagramSocket()) {
			System.out.println("Starting simulation");
			Simulation sim = new Simulation(args, socket);
			sim.runSimulation();
		}
	}
}
