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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import common.Request;

public class SimulationTest {

	@Test
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

		Simulation sim = new Simulation(new String[] { "--realtime", "--file", "src/test/resources/reader_test1.txt" }, mockSocket);
		try {
			sim.runSimulation();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(1, sentRequests.size());
		
	}
}
