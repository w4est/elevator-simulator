package common;

/**
 * An enum class to symbolize a direction for the elevator
 * 
 * @author Farhan Mahamud
 *
 */
public enum Direction {
	UP, DOWN, IDLE;
	
	/**
	 * Converts enum to integer value
	 * @return
	 * @author Farhan Mahamud
	 */
	public int toInt() {
		return this.ordinal();
	}

	/**
	 * Converts integer into enum
	 * @param value
	 * @return
	 */
	public static Direction fromInt(int value) {
		return Direction.values()[value];
	}
}
