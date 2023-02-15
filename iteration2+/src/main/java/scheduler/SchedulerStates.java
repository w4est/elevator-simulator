package scheduler;

/**
 * @author Jacob Hovey
 *
 */
public enum SchedulerStates {
	NoRequests {
		public SchedulerStates nextState() {
			return CheckForRequests;
		}
	},
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
			return AllRequestsComplete;
		}
	},
	AllRequestsComplete {
		public SchedulerStates nextState() {
			return this;
		}
	};
	
	public abstract SchedulerStates nextState();
}
