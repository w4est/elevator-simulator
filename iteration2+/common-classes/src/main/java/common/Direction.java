package common;

/**
 * An enum class to symbolize a direction for the elevator
 * 
 * @author Farhan Mahamud
 *
 */
public enum Direction {
	UP, DOWN, IDLE;
	
	public int toInt() {
		return this.ordinal();
	}

	public static Direction fromInt(int value) {
		return Direction.values()[value];
	}
}
