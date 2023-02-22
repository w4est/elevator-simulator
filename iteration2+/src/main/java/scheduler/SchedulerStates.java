package scheduler;

/**
 * @author Jacob Hovey
 *
 */
public enum SchedulerStates {
	CheckForRequests {
		public SchedulerStates nextState() {
			return IncompleteRequests;
		}
	},
	IncompleteRequests {
		public SchedulerStates nextState() {
			return CheckForResponses;
		}
	},
	CheckForResponses {
		public SchedulerStates nextState() {
			return CheckForRequests;
		}
	},
	AllRequestsComplete {
		public SchedulerStates nextState() {
			return this;
		}
	};
	
	public abstract SchedulerStates nextState();
}
