package scheduler;

/**
 * @author Jacob Hovey
 *
 */
public enum SchedulerStateMachine {
	NoRequests {
		public SchedulerStateMachine nextState() {
			return CheckForRequests;
		}
	},
	CheckForRequests {
		public SchedulerStateMachine nextState() {
			return IncompleteRequests;
		}
	},
	IncompleteRequests {
		public SchedulerStateMachine nextState() {
			return CheckForResponses;
		}
	},
	CheckForResponses {
		public SchedulerStateMachine nextState() {
			return AllRequestsComplete;
		}
	},
	AllRequestsComplete {
		public SchedulerStateMachine nextState() {
			return this;
		}
	};
	
	public abstract SchedulerStateMachine nextState();
}
