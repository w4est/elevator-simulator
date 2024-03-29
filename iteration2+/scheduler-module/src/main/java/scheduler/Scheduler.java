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
	 * searches for any requests that best fit the current elevator path. It sends
	 * the request that best fits the criteria. If there are multiple requests on
	 * the same floor going the same direction, it can send them at the same time.
	 */
	public synchronized ArrayList<Request> sendRequests(Direction elevatorDirection, int elevatorFloor) {
		// "priorityRequest" refers to the request that best fits the current elevator
		// state and path
		LocalTime priorityRequest = null;

		switch (elevatorDirection) {
		// if the elevator is idle, it sends the oldest request and any others that are
		// starting on the same floor moving in the same direction
		case IDLE:
			for (LocalTime t : requests.keySet()) {
				if (priorityRequest == null || t.isBefore(priorityRequest)) {
					priorityRequest = t;
				}
			}
			break;
		// if the elevator is moving up, it sends the closest request above the elevator
		case UP:
			for (LocalTime t : requests.keySet()) {
				if ((requests.get(t).getFloorButton() == Direction.UP)
						&& (requests.get(t).getFloorNumber() > elevatorFloor) && (priorityRequest == null
								|| requests.get(priorityRequest).getFloorNumber() < requests.get(t).getFloorNumber())) {
					priorityRequest = t;
				}
			}
			break;
		// if the elevator is moving down, it sends the closest request below the
		// elevator
		case DOWN:
			for (LocalTime t : requests.keySet()) {
				if ((requests.get(t).getFloorButton() == Direction.DOWN)
						&& (requests.get(t).getFloorNumber() < elevatorFloor) && (priorityRequest == null
								|| requests.get(priorityRequest).getFloorNumber() > requests.get(t).getFloorNumber())) {
					priorityRequest = t;
				}
			}
			break;
		}

		ArrayList<Request> returnRequests = null;

		if (priorityRequest != null) {
			System.out.println("Requests detected, sending requests.");
			returnRequests = new ArrayList<Request>();
			// if there are multiple requests that fit the elevator path well (same
			// direction as the prioritized request and same floor or after), then all of
			// them are organized and sent at once. We limit the number of requests sent at
			// once to 3 so that one elevator does not get overloaded and multiple can
			// distribute evenly.
			for (LocalTime t : requests.keySet()) {
				int i = 0;
				if (i < 3) {
					if (requests.get(priorityRequest).getFloorButton().equals(Direction.UP)) {
						if ((requests.get(t).getFloorButton().equals(requests.get(priorityRequest).getFloorButton()))
								&& (requests.get(t).getFloorNumber() >= requests.get(priorityRequest).getFloorNumber()
										|| requests.get(t).getFloorNumber() >= elevatorFloor)) {
							returnRequests.add(requests.get(t));
							i++;
						}
					} else if (requests.get(priorityRequest).getFloorButton().equals(Direction.DOWN)) {
						if ((requests.get(t).getFloorButton().equals(requests.get(priorityRequest).getFloorButton()))
								&& (requests.get(t).getFloorNumber() <= requests.get(priorityRequest).getFloorNumber()
										|| requests.get(t).getFloorNumber() <= elevatorFloor)) {
							returnRequests.add(requests.get(t));
							i++;
						}
					}
				}
			}
			// remove all requests from the main queue that are going to be sent to the
			// elevator
			for (Request r : returnRequests) {
				requests.remove(r.getLocalTime(), r);
			}
		}
		notifyAll();
		return returnRequests;
	}

	/**
	 * The main method for the scheduler module.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Scheduler sched = new Scheduler();
		FloorHelper fh = new FloorHelper(sched);
		ElevatorHelper eh = new ElevatorHelper(sched, fh);

		Thread fhThread = new Thread(fh);
		Thread ehThread = new Thread(eh);

		fhThread.start();
		ehThread.start();
	}

	/**
	 * requests list getter method used for testing.
	 */
	public TreeMap<LocalTime, Request> getRequests() {
		return requests;
	}
}
