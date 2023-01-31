package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * This class loads the simulation's file and creates an object
 * 
 * 
 * @author William Forrest
 *
 */
public class SimulatorReader implements AutoCloseable {

	File file;
	BufferedReader bufferedReader;

	SimulatorReader(String filePath) {

		File fileToRead = new File(filePath);
		FileReader fs;
		try {
			System.out.println(fileToRead.getAbsolutePath());
			fs = new FileReader(fileToRead);
			bufferedReader = new BufferedReader(fs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
