package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import simulator.InputFileReader;
import simulator.SimulationEntry;

public class InputFileReaderTest {

	private static final String TEST_FILE1 = "src/test/resources/reader_test1.txt";
	private static final String TEST_FILE2 = "src/test/resources/reader_test2.txt";

	@Test
	void shouldReadEntry() throws Exception {
		// Let the read autoclose when done
		try (InputFileReader reader = new InputFileReader(TEST_FILE1)) {
			Optional<SimulationEntry> entry = reader.getNextEntry();

			assertTrue(entry.isPresent());
			assertEquals(LocalTime.of(14, 5, 33, 123000000), entry.get().getTimestamp());
			assertEquals(2, entry.get().getSourceFloor());
			assertEquals(4, entry.get().getDestinationFloor());
			assertEquals(true, entry.get().isUp());
		}
	}

	@Test
	void shouldReadAllEntries() throws Exception {
		// Let the read autoclose when done
		try (InputFileReader reader = new InputFileReader(TEST_FILE2)) {

			List<SimulationEntry> results = new ArrayList<>();

			Optional<SimulationEntry> entry;
			do {
				entry = reader.getNextEntry();
				if (entry.isPresent()) {
					results.add(entry.get());
				}
			} while (entry.isPresent());

			// 10 lines should have been read
			assertTrue(results.size() == 10);

			// Let's check the first and last entries for correctness
			SimulationEntry firstEntry = results.get(0);
			assertEquals(LocalTime.of(14, 5, 33, 123000000), firstEntry.getTimestamp());
			assertEquals(2, firstEntry.getSourceFloor());
			assertEquals(4, firstEntry.getDestinationFloor());
			assertEquals(true, firstEntry.isUp());

			SimulationEntry lastEntry = results.get(9);
			assertEquals(LocalTime.of(14, 55, 33, 123000000), lastEntry.getTimestamp());
			assertEquals(3, lastEntry.getSourceFloor());
			assertEquals(6, lastEntry.getDestinationFloor());
			assertEquals(true, lastEntry.isUp());
		}
	}

}
