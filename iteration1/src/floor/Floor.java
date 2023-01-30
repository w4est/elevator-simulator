/**
 * 
 */
package floor;

/**
 * Floor Class used in FloorSubsystem
 * @author Subear Jama
 */
public class Floor{
	private int floorNumber;
	private int numberOfPeople;
	private boolean light;
	//separated buttons for on/off states
	private boolean upButton;
	private boolean downButton;
	
	/**
	 * Floor Constructor sets up the initial state of a Floor.
	 * @param floorNum int, the floor's number
	 * @param peopleOnFloor int, the people on that flooor
	 */
	public Floor (int floorNum, int peopleOnFloor) {
		this.floorNumber = floorNum;
		this.numberOfPeople = peopleOnFloor;
		this.light = false;
		this.upButton = false;
		this.downButton = false;
	}
	
	/**
	 * This method turns the Floor button and light on or off
	 * @param onOff boolean, true = on and false = off
	 */
	public void setButtonAndLight(boolean onOff) {
		if (onOff == true) {
			this.upButton = true;
			this.light = true;
		}
		else {
			this.downButton = false;
			this.light = false;
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
	
	public void removePeople(int peopleLeavingFloor) {
		if (peopleLeavingFloor > numberOfPeople) {
			System.out.println("Cannot remove people that don't exist. People on Floor #" + floorNumber +
					": " + numberOfPeople);
		} else {
			numberOfPeople -= peopleLeavingFloor;
			System.out.println("Successfylly Removed " + peopleLeavingFloor + " Out of " + numberOfPeople +
					" People from Floor #" + floorNumber);
		}
	}
}
