package scheduler;

import java.time.LocalTime;
import java.util.*;

import elevator.*;
import floor.*;

/**
 * @author Jacob Hovey
 * 
 * The Scheduler for the elevator system. Thread synchronization
 * will occur in this class. Communicates with the floor and elevator
 * subsystems to schedule the best elevator route.
 *
 */
public class Scheduler {	
	//this will be set to true when there are people in the queue who have not been serviced
	private boolean elevatorNeeded = false;
	//List of requests received from the Floor Subsystem
	private TreeMap<LocalTime, Request> requests;
	private ElevatorSubsystem elevatorSubsys;
	private FloorSubsystem floorSubsystem;
	
	public Scheduler() {
		requests = new TreeMap<LocalTime, Request>();
	};
	
    /**
    * For iteration 1, we need to have references to the elevator subsystem, this will be replaced by network communication in the future.
    **/
	public void addElevatorSubsys(ElevatorSubsystem e) {
		elevatorSubsys = e;
	}
	
	/**
	* For iteration 1, we need to have references to the floor subsystem, this will be replaced by network communication in the future.
	**/
	public void addFloorSubsys(FloorSubsystem f) {
		floorSubsystem = f;
	}
	
	/**
	 * This is the command that runs when a user presses a call elevator button on a specific floor.
	 * It adds the request information to the requests queue, and then notifies the elevator threads to check for a job.
	 * 
	 * @param time		LocalTime, the specific time that the request was made.
	 * @param request	Request, contains all necessary information about the elevator request.	
	 */
	public synchronized void requestElevator(LocalTime time, Request request) {
		requests.put(time, request);
		elevatorNeeded = true;
		notifyAll();
	}
	
	/**
	 * This is constantly called by waiting elevator threads
	 * to see if there is a new job for them.
	 */
	public synchronized void elevatorNeeded() {
		//this will need to be updated when there are multiple elevator threads to ensure that they don't all attempt to fulfill the request.
		while(!elevatorNeeded) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Scheduler ran into an error: ");
				e.printStackTrace();
			}
		}
		//Tells the elevator to service the oldest job. This will be updated in later iterations to prioritize
		//based on other factors as well (direction of moving elevator, etc.). Currently requests are instantaneous so 
		//it just works from old to new.
		LocalTime priorityRequest = null;
		for (LocalTime t: requests.keySet()) {
			if(priorityRequest == null || t.isBefore(priorityRequest)) {
					priorityRequest = t;
			}
		}
		//send the request information to the elevator.	
		elevatorSubsys.updateFloorQueue(requests.get(priorityRequest));
		//remove the sent request from the queue. 
		requests.remove(priorityRequest);
		//stops calling elevators if there are no more requests in the queue.
		if (requests.isEmpty()) {
			elevatorNeeded = false;
		}
		notifyAll();
	}
	
	/**
	 * This is the command called by the elevator subsystem when it has received a request
	 * and is dispatching an elevator. The information is then shared with the floor subsystem.
	 * 
	 * @param elevatorNumber			int, the number of the elevator dispatched for the job.
	 * @param departureFloorNumber		int, the number of the floor the elevator is leaving from.
	 * @param targetFloorNumber			int, the number of the floor the elevator is going towards.
	 */
	public synchronized void requestReceived(int elevatorNumber, int departureFloorNumber, int targetFloorNumber) {
		floorSubsystem.getElevatorInfoFromScheduler(elevatorNumber, departureFloorNumber, targetFloorNumber);
		notifyAll();
	}
	
	/**
	 * Method to get the boolean value of elevatorNeeded.
	 * 
	 * @return	boolean, value of elevatorNeeded.
	 */
	public boolean getElevatorNeeded() {
		return elevatorNeeded;
	}
}
