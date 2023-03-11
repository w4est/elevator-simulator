package elevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
// FIXME
// import scheduler.Scheduler;

public class ElevatorSubsystemTest {

	// FIXME
	/*
	 * @Test void shouldPollSchedulerWhenIdle() {
	 * 
	 * Scheduler schedulerMock = Mockito.mock(Scheduler.class);
	 * 
	 * // On the first attempt, say that the scheduler is not done // On the second,
	 * inform the elevatorThread to shutdown
	 * when(schedulerMock.isDone()).thenReturn(false, true);
	 * 
	 * Thread elevatorThread = new Thread(new ElevatorSubsystem(schedulerMock, 1));
	 * elevatorThread.start();
	 * 
	 * assertTrue(elevatorThread.isAlive()); // Give the thread some time to
	 * interact try { Thread.sleep(100); } catch (InterruptedException e) {
	 * e.printStackTrace(); } // Verify that the elevator subsystem checked with the
	 * scheduler verify(schedulerMock, atLeastOnce()).elevatorNeeded();
	 * 
	 * // Wait for the elevator to finish try { elevatorThread.join(500); } catch
	 * (InterruptedException e) { e.printStackTrace(); }
	 * 
	 * assertFalse(elevatorThread.isAlive()); }
	 */

	/**
	 * This test case goes through the states of an elevator from the perspective of
	 * the ElevatorSubsystem.
	 * 
	 * It tests how the ElevatorSubsystem handles multiple requests at the same time
	 * 
	 * NOTE: another way to test states is to run simulation and look at the console
	 * 
	 * @author Subear Jama
	 */
	// FIXME
	/*
	 * @Test void shouldTestElevatorStates() { //Initialization Scheduler
	 * schedulerMock = Mockito.mock(Scheduler.class); ElevatorSubsystem
	 * elevatorSubsystem = new ElevatorSubsystem(schedulerMock, 1); // Before
	 * starting thread, add scheduler requests manually into ElevatorSubsystem
	 * Request r1 = new Request(2, Direction.UP, 5); Request r2 = new Request(4,
	 * Direction.UP, 5); Request r3 = new Request(3, Direction.UP, 7);
	 * elevatorSubsystem.updateFloorQueue(r1);
	 * elevatorSubsystem.updateFloorQueue(r2);
	 * elevatorSubsystem.updateFloorQueue(r3); Thread elevatorThread = new
	 * Thread(elevatorSubsystem); elevatorThread.start();
	 * 
	 * // check print statements in console (added into ElevatorSubsystem to see
	 * current floor & state) /*If you see this in the console before the
	 * ElevatorSubsystem finishes, Ignore. Doesn't happen in actual simulation:
	 * 
	 * ElevatorSubsystem: Start Operate Cycle 1. Elevator Current Floor & State:
	 * Floor 1, State: STOP_OPENED 2. Elevator Current Floor & State: Floor 1,
	 * State: STOP_CLOSED
	 */
	// }

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Thread elevatorThread = new Thread(elevSub);

		elevSub.updateFloorQueue();
		assertEquals(elevSub.getFloorQueues().size(), 1);
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		elevSub.updateFloorQueue();
		assertEquals(elevSub.getFloorQueues().size(), 0);
	}
	
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

		assertEquals(elevSus.getFloorQueues().size(), 5);
		
		elevSus.movePeopleOnElevator(elev.getCurrentFloor());
		
		assertEquals(elevSus.getFloorQueues().size(), 5);
		assertEquals(elev.getElevatorQueue().size(), 0);
		
		elev.setCurrentFloor(elev.getCurrentFloor()+1);
		elevSus.movePeopleOnElevator(elev.getCurrentFloor());
		
		assertEquals(elevSus.getFloorQueues().size(), 3);
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
