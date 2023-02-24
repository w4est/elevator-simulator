package elevator;

/**
 * Used by the Elevator and ElevatorSubsystem to handle all elevator states.
 * @author Subear Jama
 *
 */
public enum ElevatorState {
	STOP_OPENED {
		public ElevatorState nextState() {
			return STOP_CLOSED;
		}
	},
	STOP_CLOSED {
		public ElevatorState nextState() {
			//fix this for determining direction OR going back to open
			return STOP_OPENED;
		}
	},
	MOVING_UP {
		public ElevatorState nextState() {
			return STOP_CLOSED;
		}
	},
	MOVING_DOWN {
		public ElevatorState nextState() {
			return STOP_CLOSED;
		}
	};
	
	public abstract ElevatorState nextState();
}
