package elevator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import common.PacketUtils;

/**
 * A test class for testing the ElevatorFaultListener class
 * @author Farhan Mahamud
 *
 */
public class ElevatorFaultTest {
	
	/**
	 * Tests if elevator handles Door Fault Interrupt
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveDoorFault() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		ElevatorSubsystem elevSub = new ElevatorSubsystem(1, 100, 100, 100, 7, 1);
		Thread elevSubThread = Mockito.mock(Thread.class);
		ElevatorFaultListener fault = new ElevatorFaultListener(elevSub, elevSubThread, s);
		
		try {
			Mockito.doAnswer(new Answer() {
				@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			}).when(s).send(Mockito.any(DatagramPacket.class));

			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = new byte[PacketUtils.BUFFER_SIZE];
					data[0] = (byte) 9;
					data[1] = (byte) 1;
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(s).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		fault.checkForFaults();
		Mockito.verify(elevSubThread, times(1)).interrupt();
	}

	/**
	 * Tests if elevator handles Slow Fault Interrupt
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveSlowFault() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		ElevatorSubsystem elevSub = Mockito.mock(ElevatorSubsystem.class);
		Thread elevSubThread = Mockito.mock(Thread.class);
		ElevatorFaultListener fault = new ElevatorFaultListener(elevSub, elevSubThread, s);
		
		try {
			Mockito.doAnswer(new Answer() {
				@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			}).when(s).send(Mockito.any(DatagramPacket.class));

			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = new byte[PacketUtils.BUFFER_SIZE];
					data[0] = (byte) 9;
					data[1] = (byte) 2;
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(s).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		when(elevSub.getElevator()).thenReturn(new Elevator(0));
		fault.checkForFaults();
		Mockito.verify(elevSub, times(1)).activateSlowFault();
	}
}
