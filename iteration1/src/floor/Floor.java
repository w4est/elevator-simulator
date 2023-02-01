package floor;

/**
 * Floor Class used in FloorSubsystem
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
	 * @param peopleOnFloor int, the people on that floor
	 */
	public Floor (int floorNum) {
		this.floorNumber = floorNum;
		this.numberOfPeople = 0;
		this.upButton = false;
		this.downButton = false;
	}
	
	public void addNumberOfPeople (int peopleOnFloor) {
		this.numberOfPeople += peopleOnFloor;
	}
	
	/**
	 * This method turns the Floor up button on or off.
	 * If up button is false, the down button also turns off if it was true before.
	 * @param up boolean, true = up is on, false = off
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
	 * @param down boolean, true = down is on, false = off
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
	 * This method removes people from this floor
	 * @param peopleLeavingFloor int, the number of people leaving the floor
	 */
	public void removePeople(int peopleLeavingFloor) {
		if (peopleLeavingFloor > numberOfPeople) {
			System.out.println("Cannot remove people that don't exist. People on Floor #" + floorNumber +
					": " + numberOfPeople);
		} else {
			numberOfPeople -= peopleLeavingFloor;
			System.out.println("Successfully Removed " + peopleLeavingFloor + " Out of " + numberOfPeople +
					" People from Floor #" + floorNumber);
		}
	}
	
	public int getNumPeople() {
		return this.numberOfPeople;
	}
	
	public int getFloorNumber() {
		return this.floorNumber;
	}
	
	public boolean getUpButton() {
		return this.upButton;
	}
	
	public boolean getDownButton() {
		return this.downButton;
	}
}