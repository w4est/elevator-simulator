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
import common.ElevatorInfoRequest;
import common.ElevatorState;
import common.PacketUtils;
import common.Request;

public class ElevatorHelperTest {

	/**
	 * This tests that the ElevatorHelper passes along the relevant request properly
	 * if an ElevatorInfoRequest packet is received.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	void shouldReceiveProperPacket() {

		Scheduler s = new Scheduler();
		FloorHelper fh = new FloorHelper(s);
		ElevatorHelper eh = new ElevatorHelper(s, fh);

		DatagramSocket socket = Mockito.mock(DatagramSocket.class);

		fh.setSendSocket(socket);
		fh.setReceiveSocket(socket);
		eh.setSendSocket(socket);
		eh.setReceiveSocket(socket);

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
					byte[] data = new ElevatorInfoRequest(1, 1, Direction.IDLE, ElevatorState.STOP_OPENED)
							.toByteArray();
					((DatagramPacket) invocation.getArguments()[0]).setData(data);
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(socket).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			System.exit(1);
			e.printStackTrace();
		}

		Request testRequest = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4);

		s.organizeRequest(testRequest.getLocalTime(), testRequest);
		assertEquals(s.getRequests().size(), 1);

		eh.receiveSendPacket();
		assertEquals(s.getRequests().size(), 0);
	}

	/**
	 * This tests that the ElevatorHelper does not pass along any request if an
	 * improper format packet is received.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	void shouldNotReceiveEmptyPacket() {

		Scheduler s = new Scheduler();
		FloorHelper fh = new FloorHelper(s);
		ElevatorHelper eh = new ElevatorHelper(s, fh);

		DatagramSocket socket = Mockito.mock(DatagramSocket.class);

		fh.setSendSocket(socket);
		fh.setReceiveSocket(socket);
		eh.setSendSocket(socket);
		eh.setReceiveSocket(socket);

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

		Request testRequest = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4);

		s.organizeRequest(testRequest.getLocalTime(), testRequest);
		assertEquals(s.getRequests().size(), 1);

		eh.receiveSendPacket();
		assertEquals(s.getRequests().size(), 1);
	}
}
