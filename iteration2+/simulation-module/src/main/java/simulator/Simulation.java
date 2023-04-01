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

	private final String[] args;
	private final DatagramSocket datagramSocket;

	public Simulation(String[] args, DatagramSocket datagramSocket) {
		this.args = args;
		this.datagramSocket = datagramSocket;
	}

	public void runSimulation() throws FileNotFoundException, IOException {

		String testFile = readInputFileFromStringArgs(args); // directory of test file for FloorSubsystem

		LocalTime initialTime;
		long startTime = System.nanoTime();

		try (InputFileReader iReader = new InputFileReader(testFile)) {
			Optional<SimulationEntry> entry = iReader.getNextEntry();
			// Gather an offset, so we can read the first entry from the file effectively as
			// 0:00 or right now.
			initialTime = entry.isPresent() ? entry.get().getTimestamp() : null;
			while (entry.isPresent()) {
				// Only wait for timestamps if it's realtimeMode.
				long deltaTime = System.nanoTime() - startTime;
				if (entry.isPresent() && (isItRequestTime(initialTime, deltaTime, entry.get()))) {
					SimulationEntry currentEntry = entry.get();
					System.out.println(String.format("Sending Entry, entry is %s, simulation time is: %s",
							entry.get().toString(), deltaTime));
					sendRequestAtFloor(currentEntry, datagramSocket);

					// Success, the new user should be simulated, read next entry
					entry = iReader.getNextEntry();
				}
			}
		}
	}

	private static String readInputFileFromStringArgs(String args[]) {
		String file = "src/test/resources/request_test2.txt"; // directory of test file for FloorSubsystem

		for (int i = 0, max = args.length; i < max; i++) {
			if (args[i].equalsIgnoreCase("--file") && (i + 1 < max)) {
				return args[i + 1];
			}
		}

		return file;
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
	private static boolean isItRequestTime(LocalTime offsetTime, long deltaTime, SimulationEntry entry) {
		return offsetTime.plusNanos(deltaTime).isAfter(entry.getTimestamp());
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
