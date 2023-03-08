package scheduler;

import java.time.LocalTime;
import java.util.*;

import common.Direction;
import common.Request;

/**
 * @author Jacob Hovey
 * 
 *         The Scheduler for the elevator system. Thread synchronization will
 *         occur in this class. Communicates with the floor and elevator
 *         subsystems to schedule the best elevator route.
 *
 */
public class Scheduler {
	// List of requests received from the Floor Subsystem
	private TreeMap<LocalTime, Request> requests;

	public Scheduler() {
		requests = new TreeMap<LocalTime, Request>();
	};

	/**
	 * This is called by the FloorHelper thread when a new request packet is
	 * received. This organizes the new request into the requests list in order of
	 * priority.
	 * 
	 * @param time    LocalTime, the specific time that the request was made.
	 * @param request Request, contains all necessary information about the elevator
	 *                request.
	 */
	public synchronized void organizeRequest(LocalTime time, Request request) {
		requests.put(time, request);
		notifyAll();
	}

	/**
	 * This is called by the ElevatorHelper thread when a request is ready to be
	 * serviced. This iterates through the requests list in order of priority, and
	 * searches for any requests that best fit the current elevator path.
	 */
	public synchronized Request sendPriorityRequest(Direction elevatorDirection, int elevatorFloor) {
		LocalTime priorityRequest = null;

		switch (elevatorDirection) {
		case IDLE:
			for (LocalTime t : requests.keySet()) {
				if (priorityRequest == null || t.isBefore(priorityRequest)) {
					priorityRequest = t;
				}
			}
			requests.remove(priorityRequest);
		case UP:
			for (LocalTime t : requests.keySet()) {
				if ((requests.get(t).getFloorButton() == Direction.UP)
						&& (requests.get(t).getFloorNumber() > elevatorFloor) && (priorityRequest == null
								|| requests.get(priorityRequest).getFloorNumber() < requests.get(t).getFloorNumber())) {
					priorityRequest = t;
				}
			}
		case DOWN:
			for (LocalTime t : requests.keySet()) {
				if ((requests.get(t).getFloorButton() == Direction.DOWN)
						&& (requests.get(t).getFloorNumber() < elevatorFloor) && (priorityRequest == null
								|| requests.get(priorityRequest).getFloorNumber() > requests.get(t).getFloorNumber())) {
					priorityRequest = t;
				}
			}
		}

		notifyAll();
		return requests.get(priorityRequest);
	}
	
	public static void main(String args[]) {
		Scheduler sched = new Scheduler();
		FloorHelper fh = new FloorHelper(sched);
		ElevatorHelper eh = new ElevatorHelper(sched, fh);
		
		Thread fhThread = new Thread(fh);
		Thread ehThread = new Thread(eh);
		
		fhThread.run();
		ehThread.run();
	}
}
