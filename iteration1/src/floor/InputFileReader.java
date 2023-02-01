package floor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

/**
 * This class loads the simulation's file and creates an object
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

	public Optional<SimulationEntry> getNextEntry() throws IOException {
		String line = bufferedReader.readLine();
		if (line == null) {
			return Optional.empty();
		}
		return Optional.of(SimulationEntry.fromString(line));
	}

	@Override
	public void close() throws Exception {
		this.bufferedReader.close();
	}

}
