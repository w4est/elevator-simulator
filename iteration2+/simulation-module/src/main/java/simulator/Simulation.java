package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.Optional;

import common.Direction;
import common.PacketUtils;
import common.Request;

/**
 * Simulation class is responsible for program initialization & runs the
 * simulation from the console
 * 
 * @author Will Forest
 */
public class Simulation {

	public static void main(String[] args) throws IOException {
		Simulation sim = new Simulation();
		try (DatagramSocket socket = new DatagramSocket()) {
			sim.runSimulation(args, socket);
		}
	}

	public void runSimulation(String[] args, DatagramSocket datagramSocket) throws FileNotFoundException, IOException {
		// Realtime mode is much slower to test, must be enabled upon request
		boolean realTimeMode = isRealtimeFlagInStringArgs(args);

		String testFile = readInputFileFromStringArgs(args); // directory of test file for FloorSubsystem

		// Gather an offset, so we can read the first entry from the file effectively as
		// 0:00 or right now.
		LocalTime initialTime = LocalTime.now();
		long startTime = System.nanoTime();

		try (InputFileReader iReader = new InputFileReader(testFile)) {
			Optional<SimulationEntry> entry = iReader.getNextEntry();

			while (entry.isPresent()) {
				// Only wait for timestamps if it's realtimeMode.
				if (entry.isPresent() && (!realTimeMode || isItRequestTime(initialTime, startTime, entry.get()))) {
					SimulationEntry currentEntry = entry.get();
					sendRequestAtFloor(currentEntry, datagramSocket);

					// Success, the new user should be simulated, read next entry
					entry = iReader.getNextEntry();
				}
			}
		}
	}

	private static String readInputFileFromStringArgs(String args[]) {
		String file = "src/test/resources/request_test.txt"; // directory of test file for FloorSubsystem

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--file") && (i + 1 < max)) {
				return args[i + 1];
			}
		}

		return file;
	}

	private static boolean isRealtimeFlagInStringArgs(String args[]) {

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--realtime")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if a request is ready to be run.
	 * 
	 * If the first request's start time + the delta time of the currently running
	 * system is > the next entry's time, then the next request is ready to be sent.
	 * 
	 * @param offsetTime The LocalTime of the first request
	 * @param startTime  The start of recording time in nanoseconds
	 * @param entry      The simulationEntry to evaluate.
	 * @return whether it is appropriate to send the next request
	 */
	private static boolean isItRequestTime(LocalTime offsetTime, long startTime, SimulationEntry entry) {
		long deltaTimeSinceStart = System.nanoTime() - startTime;
		return offsetTime.plusNanos(deltaTimeSinceStart).isAfter(entry.getTimestamp());
	}

	/**
	 * Opens a socket and pushes a request button on the floor Subsystem
	 * 
	 * @param entry
	 * @return true if the message was successfully sent
	 */
	private static boolean sendRequestAtFloor(SimulationEntry entry, DatagramSocket socket) {
		try {
			socket.setSoTimeout(20);

			Direction direction = entry.isUp() ? Direction.UP : Direction.DOWN;
			Request request = new Request(entry.getTimestamp(), entry.getSourceFloor(), direction,
					entry.getDestinationFloor());

			byte[] buffer = request.toByteArray();
			DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getLocalHost(),
					PacketUtils.FLOOR_PORT);
			socket.send(packet);

		} catch (SocketException e) {
			// If we timeout, we'll try again later
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
