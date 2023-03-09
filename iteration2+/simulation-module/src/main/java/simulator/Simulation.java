package simulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
// import elevator.ElevatorSubsystem;
// import floor.FloorSubsystem;
// import scheduler.Scheduler;

import common.Direction;
import common.Request;

/**
 * Simulation class is responsible for program initialization & runs the simulation from the console
 * @author Will Forest
 */
public class Simulation {
	
	
	public static void main(String[] args) throws IOException {
		
		String testFile = "src/test/resources/request_test.txt"; // directory of test file for FloorSubsystem
		
		// Gather an offset, so we can read the first entry from the file effectively as 0:00 or right now.
		LocalTime initialTime = LocalTime.now();
		long startTime = System.nanoTime();
		
		int numFloors = 7;
		int numElevators = 4;
		
		Map<Integer, List<User>> floorUsers = new HashMap<>();
		Map<Integer, List<User>> elevatorUsers = new HashMap<>();
		
		for (int i = 0; i < numFloors; i++) {
			floorUsers.put(i, new ArrayList<>());
		}
		
		for (int i = 0; i < numElevators; i++) {
			elevatorUsers.put(i, new ArrayList<>());
		}
		
		try (InputFileReader iReader = new InputFileReader(testFile)) {
			Optional<SimulationEntry> entry = iReader.getNextEntry();
			
			
			while (entry.isPresent() || usersUsingElevators(floorUsers, elevatorUsers)) {
				if (entry.isPresent() && isItRequestTime(initialTime, startTime, entry.get())) {
					SimulationEntry currentEntry = entry.get();
					Optional<User> newUser = sendRequestAtFloor(currentEntry);
					
					// Success, the new user should be simulated, read next entry
					if (newUser.isPresent()) {
						floorUsers.get(currentEntry.getSourceFloor()).add(newUser.get());
						entry = iReader.getNextEntry();
					}
				}		
			}
		}
	}
	
	/**
	 * Checks all the floors and elevators for any users still using the elevator service
	 * @param floorUsers
	 * @param elevatorUsers
	 * @return whether or not there is at least 1 user using the elevators
	 */
	private static boolean usersUsingElevators(Map<Integer, List<User>> floorUsers, Map<Integer, List<User>> elevatorUsers) {
		
		for (Entry<Integer, List<User>> floor: floorUsers.entrySet()) {
			if (floor.getValue().size() > 0) {
				return true;
			}
		}
		
		for (Entry<Integer, List<User>> elevator: elevatorUsers.entrySet()) {
			if (elevator.getValue().size() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if a request is ready to be run.
	 * 
	 * If the first request's start time + the delta time of the currently running system
	 * is > the next entry's time, then the next request is ready to be sent.
	 * 
	 * @param offsetTime The LocalTime of the first request
	 * @param startTime The start of recording time in nanoseconds
	 * @param entry The simulationEntry to evaluate.
	 * @return whether it is appropriate to send the next request
	 */
	private static boolean isItRequestTime(LocalTime offsetTime, long startTime, SimulationEntry entry) {
		long deltaTimeSinceStart = System.nanoTime() - startTime;
		return offsetTime.plusNanos(deltaTimeSinceStart).isAfter(entry.getTimestamp());
	}
	
	/**
	 * Opens a socket and pushes a request button on the floor Subsystem
	 * 
	 * @param entry
	 */
	private static Optional<User> sendRequestAtFloor(SimulationEntry entry) {
		
		try (DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(20);
			
			Direction direction = entry.isUp() ? Direction.UP : Direction.DOWN;
			Request request = new Request(entry.getTimestamp(), entry.getSourceFloor(), direction,
					entry.getDestinationFloor());

			byte[] buffer = request.toByteArray();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.send(packet);
			
			
		} catch (SocketException e) {
			// If we timeout, we'll try again later
			e.printStackTrace();
			return Optional.empty();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
				
		return Optional.of(new User(entry.getDestinationFloor()));
	}
}
