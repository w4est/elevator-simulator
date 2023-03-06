package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

/**
 * This class loads the simulation's file and creates an {@link SimulationEntry}
 * for each row read.
 * 
 * 
 * @author William Forrest and Subear Jama
 *
 */
public class InputFileReader implements AutoCloseable {

	private BufferedReader bufferedReader;

	InputFileReader(String filePath) throws FileNotFoundException {
			File fileToRead = new File(filePath);
			FileReader fs;
			fs = new FileReader(fileToRead);
			bufferedReader = new BufferedReader(fs);
		}

	/**
	 * Reads a single line from the file, and returns a {@link SimulationEntry}
	 * 
	 * @return the next SimulationEntry, or an empty Optional if we have reached end of file
	 * @throws IOException
	 */
	public Optional<SimulationEntry> getNextEntry() throws IOException {
		String line = bufferedReader.readLine();
		if (line == null) {
			return Optional.empty();
		}
		return Optional.of(SimulationEntry.fromString(line));
	}

	/**
	 * Closes the underlying buffered reader, used in try-with-resources statements
	 */
	@Override
	public void close() throws IOException {
		this.bufferedReader.close();
	}

}
