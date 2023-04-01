package floor;

import java.util.ArrayList;

/**
 * Floor Class used in FloorSubsystem to set up a floor in a building.
 * @author Subear Jama
 */
public class Floor{
	private final int floorNumber; // the floor's number
	private int numberOfPeople;    // number of people currently on the floor
	private boolean upButton;      // floor's up button (pressed = on, not pressed = off)
	private boolean downButton;   
	private ArrayList<Integer> floorLamps; // the elevator(s) current position (uses index for elevator #)
	
	/**
	 * Floor Constructor sets up the initial state of a Floor.
	 * @param floorNum int, the floor's number
	 * @param numOfElevators int, the elevators per floor
	 */
	public Floor (int floorNum, int numOfElevators) {
		this.floorNumber = floorNum;
		this.numberOfPeople = 0;
		this.upButton = false;
		this.downButton = false;
		this.floorLamps = new ArrayList<Integer>(numOfElevators);
		// later the elevator position will update according to the elevator num being the index of floorLamps
		for (int i = 1; i < numOfElevators + 1; i++) {
			this.floorLamps.add(1);
		}
	}
	
	/**
	 * This method adds the people on the floor. If people exist on the floor then it increments the count.
	 * @param peopleOnFloor int, the people to add to this floor.
	 */
	public void addNumberOfPeople (int peopleOnFloor) {
		this.numberOfPeople += peopleOnFloor;
	}
	
	/**
	 * This method turns the Floor up button on or off.
	 * If up button is false, the down button also turns off if it was true before.
	 * @param up boolean, true = up is on, false = both up and down are off
	 */
	public void setUpButton(boolean up) {
		if (up == true) {
			this.upButton = true;
		}
		else {
			this.upButton = false;
			this.downButton = false;
		}
	}
	
	/**
	 * This method turns the Floor down button on or off.
	 * If down button is false, the up button also turns off if it was true before.
	 * @param down boolean, true = down is on, false = both up and down are off.
	 */
	public void setDownButton(boolean down) {
		if (down == true) {
			this.downButton = true;
		}
		else {
			this.downButton = false;
			this.upButton = false;
		}
	}
	
	/**
	 * This method removes people from this floor.
	 * It only allows you to remove up to the number of people that currently are on the floor.
	 * @param peopleLeavingFloor int, the number of people leaving the floor.
	 */
	public void removePeople(int peopleLeavingFloor) {
		if (peopleLeavingFloor < numberOfPeople) {
			this.numberOfPeople -= peopleLeavingFloor;
			System.out.println("*Floor "+ this.floorNumber + ": "+ peopleLeavingFloor + " people got off floor");
		}
	}
	
	/**
	 * This method gets and returns the number of people on the floor.
	 * @return int, the number of people on this floor.
	 */
	public int getNumPeople() {
		return this.numberOfPeople;
	}
	
	/**
	 * This method gets and returns the floor number.
	 * @return int, the floor number.
	 */
	public int getFloorNumber() {
		return this.floorNumber;
	}
	
	/**
	 * This method gets the status of the floor's up button.
	 * @return boolean, true = up is on, false = up is off.
	 */
	public boolean getUpButton() {
		return this.upButton;
	}
	
	/**
	 * This method gets the status of the floor's down button.
	 * @return boolean, true = down is on, false = down is off.
	 */
	public boolean getDownButton() {
		return this.downButton;
	}
	
	/**
	 * Method sets the FloorLamp for that particular elevator
	 * @param elevatorNumber int, the index to put the eleavtor's position
	 * @param elevatorPosition int, the elevator number's current floor
	 */
	public void setFloorLamp(int elevatorNumber, int elevatorPosition) {
		this.floorLamps.set(elevatorNumber, elevatorPosition);
	}
	
	public ArrayList<Integer> getFloorLamp() {
		return this.floorLamps;
	}
}