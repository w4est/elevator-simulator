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
				long totalTime = System.currentTimeMillis();
				boolean programCompleted = false;
				while (System.currentTimeMillis() < (totalTime + 30000) && !programCompleted) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					totalTime += 500;
					if(!Simulation.getElevatorSubsystemThread().isAlive()
							&& !Simulation.getFloorSubsystemThread().isAlive()) {
						programCompleted = true;
					}
				}
				assertEquals(true, programCompleted);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Make sure the threads are closed
				Simulation.getElevatorSubsystemThread().interrupt();
				Simulation.getFloorSubsystemThread().interrupt();
			}
		}
	}
}
