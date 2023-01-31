package simulator;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Class that defines a simulation record
 * 
 * 
 * @author WilliamForrest
 *
 */
public class SimulationEntry {

	final LocalDate timestamp;
	final int sourceFloor;
	final boolean up;
	final int destinationFloor;
	
	
	public SimulationEntry(LocalDate timestamp, int sourceFloor, boolean up, int destinationFloor) {
		this.timestamp = timestamp;
		this.sourceFloor = sourceFloor;
		this.up = up;
		this.destinationFloor = destinationFloor;
	}
	
	/**
	 * Creates a simulation entry from a line from the simulation file.
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
		
		LocalDate date = LocalDate.parse(tokens[0]);
		int sourceFloor = Integer.parseInt(tokens[1]);
		boolean directionIsUp = Boolean.parseBoolean(tokens[2]);
		int destinationFloor = Integer.parseInt(tokens[3]);
		
		return new SimulationEntry(date, sourceFloor, directionIsUp, destinationFloor);
	}
	
}
