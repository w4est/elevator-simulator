package floor;

/**
 * Floor Class used in FloorSubsystem to set up a floor in a building.
 * @author Subear Jama
 */
public class Floor{
	private final int floorNumber;
	private int numberOfPeople;
	private boolean upButton;
	private boolean downButton;
	
	/**
	 * Floor Constructor sets up the initial state of a Floor.
	 * @param floorNum int, the floor's number
	 */
	public Floor (int floorNum) {
		this.floorNumber = floorNum;
		this.numberOfPeople = 0;
		this.upButton = false;
		this.downButton = false;
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
		if (peopleLeavingFloor > numberOfPeople) {
			System.out.println("Cannot remove people that don't exist. People on Floor #" + floorNumber +
					": " + numberOfPeople);
		} else {
			numberOfPeople -= peopleLeavingFloor;
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
}