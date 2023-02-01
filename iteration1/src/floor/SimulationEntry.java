package floor;

import java.io.IOException;
import java.time.LocalTime;

/**
 * Class that defines a simulation record
 * 
 * 
 * @author WilliamForrest
 *
 */
public class SimulationEntry {

	private final LocalTime timestamp;
	private final int sourceFloor;
	private final boolean up;
	private final int destinationFloor;

	public SimulationEntry(LocalTime timestamp, int sourceFloor, boolean up, int destinationFloor) {
		this.timestamp = timestamp;
		this.sourceFloor = sourceFloor;
		this.up = up;
		this.destinationFloor = destinationFloor;
	}

	/**
	 * Creates a simulation entry from a line from the simulation file.
	 * 
	 * @param line
	 * @return
	 * @throws IOException
	 */
	public static SimulationEntry fromString(String line) throws IOException {

		// Split on whitespace
		String[] tokens = line.split("\s");

		if (tokens.length != 4) {
			throw new IOException("Simulation entry is invalid");
		}

		LocalTime time = LocalTime.parse(tokens[0]);
		int sourceFloor = Integer.parseInt(tokens[1]);
		boolean directionIsUp = tokens[2].equalsIgnoreCase("up");
		int destinationFloor = Integer.parseInt(tokens[3]);

		return new SimulationEntry(time, sourceFloor, directionIsUp, destinationFloor);
	}
	
	@Override
	public String toString() {
		String direction = up ? "going up" : "going down";
		return "[" + String.join(",", timestamp.toString(), Integer.valueOf(destinationFloor).toString(), direction,
				Integer.valueOf(destinationFloor).toString()) + "]";
	}

	public LocalTime getTimestamp() {
		return timestamp;
	}

	public int getSourceFloor() {
		return sourceFloor;
	}

	public boolean isUp() {
		return up;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

}
