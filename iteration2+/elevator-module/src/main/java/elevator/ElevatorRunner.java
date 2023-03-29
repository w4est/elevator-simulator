package elevator;

import java.util.Optional;

public class ElevatorRunner {

	public final static int DEFAULT_MAX_FLOOR = 7; // The default max floor
	public final static int DEFAULT_MIN_FLOOR = 1; // The default min floor
	public final static long DEFAULT_FLOOR_MOVEMENT_TIME = 4000L;
	public final static long DEFAULT_DOOR_MOVEMENT_TIME = 2500L;
	public final static long DEFAULT_LOAD_TIME_PER_PERSON = 1500L;
	public final static int DEFAULT_NUM_ELEVATORS = 2;

	public static void main(String[] args) {

		Optional<Long> loadingTime = getLoadingSpeedInArgs(args);
		Optional<Long> doorTime = getElevatorDoorSpeedInArgs(args);
		Optional<Long> floorMovementTime = getElevatorSpeedInArgs(args);
		Optional<Integer> minFloor = getElevatorMinFloorInArgs(args);
		Optional<Integer> maxFloor = getElevatorMaxFloorInArgs(args);
		Optional<Integer> numElevators = getElevatorNumberInArgs(args);

		
		for (int i = 0; i < numElevators.orElse(DEFAULT_NUM_ELEVATORS); i++) {
			
			ElevatorSubsystem e = new ElevatorSubsystem(i + 1, floorMovementTime.orElse(DEFAULT_FLOOR_MOVEMENT_TIME),
					doorTime.orElse(DEFAULT_DOOR_MOVEMENT_TIME), loadingTime.orElse(DEFAULT_LOAD_TIME_PER_PERSON),
					maxFloor.orElse(DEFAULT_MAX_FLOOR), minFloor.orElse(DEFAULT_MIN_FLOOR));
			Thread eThread = new Thread(e);
			
			ElevatorListener listen = new ElevatorListener(e);
			Thread listenThread = new Thread(listen);
			
			ElevatorFaultListener faultListen = new ElevatorFaultListener(e, eThread);
			Thread faultListenThread = new Thread(faultListen);	
			
			eThread.start();
			listenThread.start();
			faultListenThread.start();
		}
	}

	private static Optional<Long> getLoadingSpeedInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--loading_speed") && i + 1 < max) {
				return Optional.ofNullable(Long.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}

	private static Optional<Long> getElevatorDoorSpeedInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--door_speed") && i + 1 < max) {
				return Optional.ofNullable(Long.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}

	private static Optional<Long> getElevatorSpeedInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--elevator_speed") && i + 1 < max) {
				return Optional.ofNullable(Long.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
	}

	private static Optional<Integer> getElevatorMinFloorInArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--min_floor") && i + 1 < max) {
				return Optional.ofNullable(Integer.valueOf(args[i + 1]));
			}
		}

		return Optional.empty();
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
