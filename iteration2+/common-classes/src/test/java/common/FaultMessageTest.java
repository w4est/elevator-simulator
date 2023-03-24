package common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FaultMessageTest {
	
		@Test
		void shouldSerializeToByteArrayDoorFault() {

			FaultMessage faultMessage = new FaultMessage(Fault.DoorFault);

			byte[] transformedData = faultMessage.toByteArray();

			assertArrayEquals(PacketHeaders.DoorFault.getHeaderBytes(), transformedData);
		}
		
		@Test
		void shouldSerializeToByteArraySlowFault() {

			FaultMessage faultMessage = new FaultMessage(Fault.SlowFault);

			byte[] transformedData = faultMessage.toByteArray();

			assertArrayEquals(PacketHeaders.SlowFault.getHeaderBytes(), transformedData);
		}
		
		@Test
		void shouldDeserializeFromByteArrayDoorFault() {

			FaultMessage faultMessage = FaultMessage.fromByteArray(PacketHeaders.DoorFault.getHeaderBytes());

			assertEquals(Fault.DoorFault, faultMessage.getFault());
		}
		
		@Test
		void shouldDeserializeFromByteArraySlowFault() {

			FaultMessage faultMessage = FaultMessage.fromByteArray(PacketHeaders.SlowFault.getHeaderBytes());

			assertEquals(Fault.SlowFault, faultMessage.getFault());
		}
}
