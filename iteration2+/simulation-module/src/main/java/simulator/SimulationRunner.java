package simulator;

import java.io.IOException;
import java.util.Optional;

public class SimulationRunner {
	
	public final static int DEFAULT_MAX_FLOOR = 22;
	public final static int DEFAULT_NUM_ELEVATORS = 4;
	
	public static void main(String[] args) throws IOException {

		if (isGuiFlagInStringArgs(args)) {
			Optional<Integer> maxFloor = getElevatorMaxFloorInArgs(args);
			Optional<Integer> elevatorNumber = getElevatorNumberInArgs(args);
			
			SimulationGUI simulationGUI = new SimulationGUI(maxFloor.orElse(DEFAULT_MAX_FLOOR),
					elevatorNumber.orElse(DEFAULT_NUM_ELEVATORS));
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
	
	private static Optional<Integer> getElevatorMaxFloorInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--max_floor") && i + 1 < max) {
				return Optional.ofNullable(Integer.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}

	private static Optional<Integer> getElevatorNumberInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--elevator_count") && i + 1 < max) {
				return Optional.ofNullable(Integer.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}
}
