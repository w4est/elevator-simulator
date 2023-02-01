package scheduler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import elevator.*;
import elevator.ElevatorSubsystem.Direction;
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
	//HashMap chosen to store requests with the key being time; this will be the main way of scheduling requests to be picked up,
	//but in future iterations there will also be logic to schedule based on other factors (request going same direction as moving elevator, etc.)
	private HashMap<LocalTime, Request> requests;
	private ElevatorSubsystem elevatorSubsys;
	private FloorSubsystem floorSubsystem;
	
	public Scheduler() {
		requests = new HashMap<LocalTime, Request>();
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
		LocalTime priorityRequest = null;
		//first checks if the elevator is moving past the request in the same direction to pick it up.
		//this will need to be updated when there is more than 1 elevator.
		if(!elevatorSubsys.getElevator().getCurrentDirection().equals("Idle")) {
			for (LocalTime t: requests.keySet()) {
				if(requests.get(t).getFloorButton() == elevatorSubsys.getElevator().getCurrentDirection()) {
					//checks if the direction matches and the elevator will move past the correct floor on its current path
					if((elevatorSubsys.getElevator().getCurrentFloor() < requests.get(t).getFloorNumber()) && (requests.get(t).getFloorButton() == "Up") || 
							(elevatorSubsys.getElevator().getCurrentFloor() > requests.get(t).getFloorNumber()) && (requests.get(t).getFloorButton() == "Down")) {
						priorityRequest = t;
					}
				}	        
			}
		}
		//then if not, tells the elevator to service the oldest job
		else {
			for (LocalTime t: requests.keySet()) {
				if(priorityRequest == null || t.isBefore(priorityRequest)) {
					priorityRequest = t;
				}
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
}
