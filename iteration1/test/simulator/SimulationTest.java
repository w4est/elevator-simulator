package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

// TODO, this will be replaced by an end-to-end test when we seperate programs
public class SimulationTest {

	// Stress test to make sure the program runs successfully every time
	@Test
	void stressTest() {
		for (int i = 0; i < 10; i++) {
			try {
				Simulation.main(null);
				// Give more then enough time for elevators to move etc.
				Thread.sleep(5000);
				assertEquals(false, Simulation.getElevatorSubsystemThread().isAlive());
				assertEquals(false, Simulation.getFloorSubsystemThread().isAlive());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				// Make sure the threads are closed
				Simulation.getElevatorSubsystemThread().interrupt();
				Simulation.getFloorSubsystemThread().interrupt();
			}
		}
	}
}
