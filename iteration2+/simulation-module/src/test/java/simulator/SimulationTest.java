package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

	@Test // Disable on github because of ports
	void shouldSendRequest() {
		
		DatagramSocket mockSocket = mock(DatagramSocket.class);
		
		final List<Request> sentRequests = new ArrayList<>();
		
		try {
			doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = ((DatagramPacket) invocation.getArgument(0)).getData();
					sentRequests.add(Request.fromByteArray(data));
					return null;
				}
			}).when(mockSocket).send(any(DatagramPacket.class));
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}

		Simulation sim = new Simulation();
		try {
			sim.runSimulation(new String[] { "--realtime", "--file", "src/test/resources/reader_test1.txt" }, mockSocket);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(1, sentRequests.size());
		
	}
}
