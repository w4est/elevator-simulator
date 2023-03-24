package simulator;

import java.io.IOException;

public class SimulationRunner {
	public static void main(String[] args) throws IOException {

		if (isGuiFlagInStringArgs(args)) {
			SimulationGUI simulationGUI = new SimulationGUI();
			simulationGUI.openScreen();
		} else {
			Thread simulationThread = new Thread(new SimulationThread(args));
			simulationThread.start();
		}
	}

	private static boolean isGuiFlagInStringArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--gui")) {
				return true;
			}
		}

		return false;
	}
}
