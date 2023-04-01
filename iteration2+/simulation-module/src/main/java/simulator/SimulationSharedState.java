package simulator;

public class SimulationSharedState {

	private boolean allRequestsSent = false;

	public synchronized boolean areAllRequestsSent() {
		return allRequestsSent;
	}

	public synchronized void setAllRequestsSent(boolean allRequestsSent) {
		this.allRequestsSent = allRequestsSent;
	}

}
