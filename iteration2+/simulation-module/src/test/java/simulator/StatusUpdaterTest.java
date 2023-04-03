package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import common.ElevatorState;
import common.ElevatorStatusRequest;
import common.PacketUtils;

public class StatusUpdaterTest {

	
	@Mock
	SimulationGUI gui;
	@Mock
	Simulation simulation;
	@Mock
	DatagramSocket socket;
	
	private AutoCloseable closeable;
	
	@BeforeEach
	public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }
	
	@AfterEach
	public void closeMocks() {
        try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@Test
	public void shouldReceiveElevatorStatus() {
		
		StatusUpdater updater = new StatusUpdater(gui, 4, simulation, socket);
		ElevatorStatusRequest request = new ElevatorStatusRequest(1, 2, 3, false, ElevatorState.MOVING_UP);
		
		try {
			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					byte[] data = request.toByteArray();
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(socket).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updater.checkForUpdates();
		
		verify(gui, times(1)).updateState(any(ElevatorStatusRequest.class));	
		
	}
	
	@Test
	public void shouldReceiveElevatorStatusAndComplete() {
		
		/*
		 * Check that in the case of 2 elevators, both are stable and unmoving
		 */
		
		StatusUpdater updater = new StatusUpdater(gui, 2, simulation, socket);
		ElevatorStatusRequest request = new ElevatorStatusRequest(1, 2, 0, false, ElevatorState.STOP_OPENED);
		ElevatorStatusRequest request2 = new ElevatorStatusRequest(2, 2, 0, false, ElevatorState.STOP_OPENED);
		
		try {
			Mockito.doAnswer(new Answer<Void>() {
				
				int recieveCount = 0;
				
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {

					byte[] data;
					// Iterate between both elevators being simulated
					if (recieveCount % 2 == 0) {
						data = request.toByteArray();
					} else {
						data = request2.toByteArray();
					}
					recieveCount++;
					((DatagramPacket) invocation.getArguments()[0]).setData(data);					
					((DatagramPacket) invocation.getArguments()[0]).setLength(data.length);
					return null;
				}
			}).when(socket).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Indicate that all the simulation requests are sent, we just need to wait for the elevator to stop
		when(simulation.allRequestsComplete()).thenReturn(true);
		
		updater.checkForUpdates();
		updater.checkForUpdates();
		
		// After 500ms, check for stability, and ensure that the simulationComplete() has been called
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updater.checkForUpdates();
		updater.checkForUpdates();
		
		verify(gui, times(4)).updateState(any(ElevatorStatusRequest.class));	
		verify(gui, times(1)).simulationComplete(anyLong());	
	}
	
	
	
	
}
