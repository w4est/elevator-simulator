package scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import common.Direction;
import common.PacketUtils;
import common.Request;

public class FloorHelperTest {

	/**
	 * This tests that the FloorHelper organizes the relevant request properly into
	 * the Scheduler if a proper format Request packet is received.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	void shouldReceiveProperPacket() {

		Scheduler s = new Scheduler();
		FloorHelper fh = new FloorHelper(s);

		DatagramSocket socket = Mockito.mock(DatagramSocket.class);

		fh.setSendSocket(socket);
		fh.setReceiveSocket(socket);

		try {
			Mockito.doAnswer(new Answer() {
				@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			}).when(socket).send(Mockito.any(DatagramPacket.class));

			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4).toByteArray();
					((DatagramPacket) invocation.getArguments()[0]).setData(data);
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(socket).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			System.exit(1);
			e.printStackTrace();
		}

		fh.receivePacket();
		assertEquals(s.getRequests().size(), 1);
	}

	/**
	 * This tests that the FloorHelper does not organize any request in the
	 * scheduler if an improper format packet is received.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	void shouldNotReceiveEmptyPacket() {

		Scheduler s = new Scheduler();
		FloorHelper fh = new FloorHelper(s);

		DatagramSocket socket = Mockito.mock(DatagramSocket.class);

		fh.setSendSocket(socket);
		fh.setReceiveSocket(socket);

		try {
			Mockito.doAnswer(new Answer() {
				@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			}).when(socket).send(Mockito.any(DatagramPacket.class));

			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = new byte[PacketUtils.BUFFER_SIZE];
					data[0] = (byte) 0;
					data[1] = (byte) 0;
					((DatagramPacket) invocation.getArguments()[0]).setData(data);
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(socket).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			System.exit(1);
			e.printStackTrace();
		}

		fh.receivePacket();
		assertEquals(s.getRequests().isEmpty(), true);
	}
}
