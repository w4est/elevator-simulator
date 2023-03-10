package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import common.PacketUtils;
import common.Request;

public class SimulationTest {

	private class TestThread implements Runnable {

		private List<Request> sentRequests = new ArrayList<>();
		private boolean runningThread = true;

		// Create a separate thread so we can test socket data transfer
		@Override
		public void run() {
			while (runningThread) {
				try (DatagramSocket socket = new DatagramSocket(PacketUtils.FLOOR_PORT)) {
					socket.setSoTimeout(50);
					byte[] buffer = new byte[PacketUtils.BUFFER_SIZE];
					DatagramPacket packet = new DatagramPacket(buffer, PacketUtils.BUFFER_SIZE);
					socket.receive(packet);
					addToSentRequests(Request.fromByteArray(packet.getData()));
					runningThread = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		public synchronized List<Request> getSentRequests() {
			return sentRequests;
		}
		
		public synchronized void addToSentRequests(Request request) {
			this.sentRequests.add(request);
		}		

	}

	@Test
	@Timeout(15)
	void shouldSendRequest() {
		TestThread testThread = new TestThread();
		Thread thread = new Thread(testThread);

		thread.start();
		Simulation sim = new Simulation();
		try {
			sim.runSimulation(new String[] { "--realtime", "--file", "src/test/resources/reader_test1.txt" });
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		
		// Wait a brief moment to make sure the testThread has finished packing it's bytes.
		try {
			thread.join();
			
			assertEquals(1, testThread.getSentRequests().size());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
