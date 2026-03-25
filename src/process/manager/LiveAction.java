package process.manager;

import data.sport.play.action.ActionResult;

public class LiveAction {
	private int quarter;
	private ActionResult action;
	private int remainingTimeSeconds;

	public LiveAction(int quarter, ActionResult action, int remainingTimeSeconds) {
		this.quarter = quarter;
		this.action = action;
		this.remainingTimeSeconds = remainingTimeSeconds;
	}

	public int getQuarter() {
		return quarter;
	}

	public ActionResult getAction() {
		return action;
	}

	public int getRemainingTimeSeconds() {
		return remainingTimeSeconds;
	}
}
