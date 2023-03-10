package common;

/**
 * An enum class to symbolize a direction for the elevator
 * 
 * @author Farhan Mahamud
 *
 */
public enum Direction {
	UP, DOWN, IDLE;
	
<<<<<<< HEAD
	public short toShort() {
		return (short) this.ordinal();
	}

	public static Direction fromShort(short value) {
		return Direction.values()[value];
	}

=======
>>>>>>> main
	public int toInt() {
		return this.ordinal();
	}

	public static Direction fromInt(int value) {
		return Direction.values()[value];
	}
}
