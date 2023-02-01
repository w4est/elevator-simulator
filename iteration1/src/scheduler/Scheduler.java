package scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import elevator.*;

/**
 * @author Jacob Hovey
 * 
 * The Scheduler for the elevator system. All thread synchronization
 * will occur in this class. Communicates withe floor and elevator
 * subsystems.
 *
 */
public class Scheduler {	
	//this will be set to true when there are people in the queue who have not been serviced
	private boolean elevatorNeeded = false;
	private HashMap<Date, Request> requests;
	private final ArrayList<ElevatorSubsystem> elevatorSubsys;

	public Scheduler() {
		elevatorSubsys = new ArrayList<>();
	}
	
    /**
    * For iteration 1, we need to have references to the elevator subsystem, this will be replaced by network communication in the future.
    **/
	public void addElevatorSubsys(ElevatorSubsystem e) {
		elevatorSubsys.add(e);
	}
	
	/**
	 * This is the command that runs when a user presses a call elevator button on a specific floor.
	 * It sends the information to the elevator subsystem, which then assigns an elevator to the job.
	 * 
	 * @param time				Date, the specific time that the request was made.
	 * @param floorNumber		int, the number of the floor that the elevator request was sent from.
	 * @param floorButton		Enum, the direction of the button that was pressed to call the elevator; up or down.
	 * @param carButton			int, the number of the floor that the person wants to go to.
	 */
	public synchronized void requestElevator(Date time, int floorNumber, elevator.ElevatorSubsystem.Direction floorButton, int carButton) {
		requests.put(time, new Request(floorNumber, floorButton, carButton));
		elevatorNeeded = true;
		notifyAll();
	}
	
	/**
	 * This is constantly called by waiting elevator threads
	 * to see if there is a new job for them.
	 */
	public synchronized void elevatorNeeded() {
		while(!elevatorNeeded) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Scheduler ran into an error: ");
				e.printStackTrace();
			}
		}
		Date oldestRequest = null;
		for (Date d: requests.keySet()) {
			if( oldestRequest == null || d.before(oldestRequest))
		        oldestRequest = d;
		}
		requests.remove(oldestRequest);
		int destination = requests.get(oldestRequest).getCarButton();
		int carNumber = requests.get(oldestRequest).getFloorNumber();
		
		elevatorSubsys.get(0).updateFloorQueue(destination, 1);
		
		//send the Request info to the elevator subsystem
		if (requests.isEmpty()) {
			elevatorNeeded = false;
		}
		notifyAll();
	}
	
	/**
	 * This is the command called by the elevator subsystem when it has received a request
	 * and is dispatching an elevator.
	 * 
	 * @param elevatorNumber			int, the number of the elevator dispatched for the job.
	 * @param departureFloorNumber		int, the number of the floor the elevator is leaving from.
	 * @param targetFloorNumber			int, the number of the floor the elevator is going towards.
	 */
	public synchronized void requestReceived(int elevatorNumber, int departureFloorNumber, int targetFloorNumber) {
		//will call a function in the floor subsystem to share the info which can then be printed to the console.
	}
}
