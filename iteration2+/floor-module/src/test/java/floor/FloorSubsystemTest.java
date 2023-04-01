package floor;

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
 * FloorSubsystemTest uses Junit and Mockito to test FloorSubsystem Class on reading input files.
 * @author Subear Jama and William Forrest
 */
public class FloorSubsystemTest {
	
	/*
	 * This tests method receiveInfo() and should receive and add a request to FLoorSubsystem 
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveRequest() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		DatagramSocket r = Mockito.mock(DatagramSocket.class);
		
		FloorSubsystem fs = new FloorSubsystem(8,2,s,r); // num of floors = 8
		assertEquals(fs.getFloorRequests().size(), 0);
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
			}).when(r).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fs.receiveInfo(); //receiving data
		//fs.operate(); //sending data
		assertEquals(fs.getFloorRequests().size(), 1);
	}
	
	/*
	 * This tests method receiveInfo() and should not add any requests to FLoorSubsystem 
	 */
	@Test
	@SuppressWarnings("rawtypes")
	void receiveConfimation() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		DatagramSocket r = Mockito.mock(DatagramSocket.class);
		
		FloorSubsystem fs = new FloorSubsystem(8,2,s,r);
		assertEquals(fs.getFloorRequests().size(), 0);
		
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
			}).when(r).receive(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fs.receiveInfo(); //receiving data
		//fs.operate(); //sending data
		assertEquals(fs.getFloorRequests().size(), 0);
	}
	
	
	
	@Test
	@SuppressWarnings("rawtypes")
	void sendRequest() {
		DatagramSocket s = Mockito.mock(DatagramSocket.class);
		DatagramSocket r = Mockito.mock(DatagramSocket.class);
		
		FloorSubsystem fs = new FloorSubsystem(8,2,s,r);
		assertEquals(fs.getFloorRequests().size(), 0);
		try {
			Mockito.doAnswer(new Answer() {
				@Override
				public Object answer(InvocationOnMock invocation) throws Throwable {
					System.out.print("Sending functionality works");
					return null;
				}
			}).when(s).send(Mockito.any(DatagramPacket.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fs.addFloorRequests(new Request(LocalTime.of(2, 1, 1, 1), 1, Direction.UP, 4));
		assertEquals(fs.getFloorRequests().size(), 1);
		fs.operate(); //sending data
		assertEquals(fs.getFloorRequests().get(0).getRequestStatus(), true);
	}
	
	
	
	
	
}
