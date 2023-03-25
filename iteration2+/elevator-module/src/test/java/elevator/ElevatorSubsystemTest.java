package elevator;

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

/**
 * Tests the most important functions of the ElevatorSubsystem class
 * @author Farhan Mahamud
 *
 */
public class ElevatorSubsystemTest {

	/**
	 * Tests if the ElevatorSubsystem can properly send an update message to 
	 * receive a new request from the Scheduler and add it to floorQueues
	 * @author Farhan Mahamud
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveRequest() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		ElevatorSubsystem elevSub = new ElevatorSubsystem(1, s);
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
					byte[] data = new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4).toByteArray();
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(s).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Thread elevatorThread = new Thread(elevSub);

//		elevSub.updateFloorQueue();
		assertEquals(elevSub.getFloorQueues().size(), 1);
	}
	
	/**
	 * Tests if the ElevatorSubsystem can properly send an update message to 
	 * receive a new request from the Scheduler and receive no new request 
	 * and continue on
	 * @author Farhan Mahamud
	 */
	/**
	 * Tests
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveConfimation() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		ElevatorSubsystem elevSub = new ElevatorSubsystem(1, s);
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
					data[0] = (byte) 0;
					data[1] = (byte) 0;
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(s).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

//		elevSub.updateFloorQueue();
		assertEquals(elevSub.getFloorQueues().size(), 0);
	}
	
	/**
	 * Test the ability of the elevator to pick up and drop off
	 * requests and the correct floors
	 * @author Farhan Mahamud
	 */
	@Test
	void testAddRequest() {
		ElevatorSubsystem elevSus = new ElevatorSubsystem(1);
		Elevator elev = elevSus.getElevator();
		Request r1 = new Request(LocalTime.now(), 2, Direction.UP, 5);
		Request r2 = new Request(LocalTime.now(), 2, Direction.UP, 3);
		Request r3 = new Request(LocalTime.now(), 3, Direction.UP, 4);
		elevSus.getFloorQueues().add(r1);
		elevSus.getFloorQueues().add(r2);
		elevSus.getFloorQueues().add(r3);

		assertEquals(elevSus.getFloorQueues().size(), 3);
		
		elevSus.movePeopleOnElevator(elev.getCurrentFloor());
		
		assertEquals(elevSus.getFloorQueues().size(), 3);
		assertEquals(elev.getElevatorQueue().size(), 0);
		
		elev.setCurrentFloor(elev.getCurrentFloor()+1);
		elevSus.movePeopleOnElevator(elev.getCurrentFloor());
		
		assertEquals(elevSus.getFloorQueues().size(), 1);
		assertEquals(elev.getElevatorQueue().size(), 2);

		elevSus.stopElevator();
		
		elevSus.getElevator().setCurrentFloor(elev.getCurrentFloor()+1);

		elev.clearFloor();
		
		assertEquals(elev.getElevatorQueue().size(), 1);
		
		elevSus.movePeopleOnElevator(elev.getCurrentFloor());
		elevSus.stopElevator();
		
		assertEquals(elev.getElevatorQueue().size(), 2);
		
	}

}
