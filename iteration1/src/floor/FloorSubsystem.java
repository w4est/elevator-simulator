/**
 * 
 */
package floor;

import java.util.ArrayList;

import scheduler.Scheduler;

/**
 * FloorSybsystem Class communicates with Scheduler (Scheduler shared with the Elevator)
 * Gets call from person, sends info to Scheduler.
 * Receives elevator status from Scheduler, shows person (light indicator, #)
 * @author Subear Jama
 */
public class FloorSubsystem implements Runnable{	
	
	ArrayList<Floor> sevenFloors;
	private Scheduler scheduler;
	private int totalPeopleOnAllFloors;
	
	public FloorSubsystem(Scheduler schedule) {
		this.sevenFloors = new ArrayList<Floor>();
		this.scheduler = schedule;
		this.totalPeopleOnAllFloors = 0;
	}
	
	public void addFloor(int floorNumber, int numPeople, boolean light) {
		Floor floor = new Floor(floorNumber, numPeople);
		floor.setButtonAndLight(light);
	}
	
	/**
	 * Private method used in run() to keep track of the # of people on all floors wanting to use an elevator
	 */
	private void getNumberOfPeopleOnAllFloors() {
		for (Floor f: sevenFloors) {
			totalPeopleOnAllFloors += f.getNumPeople();
		}
	}

	/**
	 * Communicate with scheduler until there are no more people to move on all floors
	 */
	@Override
	public void run() {
		getNumberOfPeopleOnAllFloors();
		while (totalPeopleOnAllFloors != 0) {
			for (Floor f: sevenFloors) {
				//my suggestion: could just pass the floor object to the scheduler?
				if (f.getUpButton() == true) {
					System.out.println("Sending to Scheduler: Floor " + f.getFloorNumber() + " going UP");
					scheduler.requestElevator(f.getFloorNumber(), f.getUpButton());
				}
				else {
					System.out.println("Sending to Scheduler: Floor " + f.getFloorNumber() + " going DOWN");
					scheduler.requestElevator(f.getFloorNumber(), f.getDownButton());
				}
			}
			//TO DO:
			//need to receive messages from scheduler
			//need to remove people from floors (already in Floor object)
			try {
				Thread.sleep(120); // slow down for correct order of print statements
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		System.out.println("People on all floors have successfully reached their destination!\n");
	}
}
