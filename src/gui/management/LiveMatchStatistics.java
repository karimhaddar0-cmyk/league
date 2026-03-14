package gui.management;

import java.util.HashMap;

import config.SimulationConfiguration;
import data.player.Player;
import data.sport.play.action.ActionResult;
import data.sport.play.action.Block;
import data.sport.play.action.EndOfTime;
import data.sport.play.action.PointScored;
import data.sport.play.action.Rebound;
import data.sport.play.action.Turnover;
import process.visitor.actionresult.ActionResultVisitor;

public class LiveMatchStatistics {

	public interface HomePlayerChecker {
		boolean isHomePlayer(Player player);
	}

	private int homePoints;
	private int awayPoints;
	private int homeRebounds;
	private int awayRebounds;
	private int homeAssists;
	private int awayAssists;
	private int homeTurnovers;
	private int awayTurnovers;
	private int homeTwoMade;
	private int awayTwoMade;
	private int homeThreeMade;
	private int awayThreeMade;
	private int homeFgAttempts;
	private int awayFgAttempts;
	private int homeThreeAttempts;
	private int awayThreeAttempts;

	private HashMap<String, Integer> homePlayerPoints;
	private HashMap<String, Integer> awayPlayerPoints;

	public LiveMatchStatistics() {
		homePlayerPoints = new HashMap<String, Integer>();
		awayPlayerPoints = new HashMap<String, Integer>();
		reset();
	}

	public void reset() {
		homePoints = 0;
		awayPoints = 0;
		homeRebounds = 0;
		awayRebounds = 0;
		homeAssists = 0;
		awayAssists = 0;
		homeTurnovers = 0;
		awayTurnovers = 0;
		homeTwoMade = 0;
		awayTwoMade = 0;
		homeThreeMade = 0;
		awayThreeMade = 0;
		homeFgAttempts = 0;
		awayFgAttempts = 0;
		homeThreeAttempts = 0;
		awayThreeAttempts = 0;
		homePlayerPoints.clear();
		awayPlayerPoints.clear();
	}

	public void applyAction(ActionResult action, HomePlayerChecker homePlayerChecker) {
		action.accept(new StatsVisitor(homePlayerChecker));
	}

	public SavedLiveState toSavedState(int liveActionIndex) {
		SavedLiveState state = new SavedLiveState();
		state.liveActionIndex = liveActionIndex;
		state.homePoints = homePoints;
		state.awayPoints = awayPoints;
		state.homeRebounds = homeRebounds;
		state.awayRebounds = awayRebounds;
		state.homeAssists = homeAssists;
		state.awayAssists = awayAssists;
		state.homeTurnovers = homeTurnovers;
		state.awayTurnovers = awayTurnovers;
		state.homeTwoMade = homeTwoMade;
		state.awayTwoMade = awayTwoMade;
		state.homeThreeMade = homeThreeMade;
		state.awayThreeMade = awayThreeMade;
		state.homeFgAttempts = homeFgAttempts;
		state.awayFgAttempts = awayFgAttempts;
		state.homeThreeAttempts = homeThreeAttempts;
		state.awayThreeAttempts = awayThreeAttempts;
		state.homePlayerPoints = new HashMap<String, Integer>(homePlayerPoints);
		state.awayPlayerPoints = new HashMap<String, Integer>(awayPlayerPoints);
		return state;
	}

	public void loadFromState(SavedLiveState state) {
		homePoints = state.homePoints;
		awayPoints = state.awayPoints;
		homeRebounds = state.homeRebounds;
		awayRebounds = state.awayRebounds;
		homeAssists = state.homeAssists;
		awayAssists = state.awayAssists;
		homeTurnovers = state.homeTurnovers;
		awayTurnovers = state.awayTurnovers;
		homeTwoMade = state.homeTwoMade;
		awayTwoMade = state.awayTwoMade;
		homeThreeMade = state.homeThreeMade;
		awayThreeMade = state.awayThreeMade;
		homeFgAttempts = state.homeFgAttempts;
		awayFgAttempts = state.awayFgAttempts;
		homeThreeAttempts = state.homeThreeAttempts;
		awayThreeAttempts = state.awayThreeAttempts;
		homePlayerPoints.clear();
		awayPlayerPoints.clear();
		homePlayerPoints.putAll(state.homePlayerPoints);
		awayPlayerPoints.putAll(state.awayPlayerPoints);
	}

	public int getHomePoints() {
		return homePoints;
	}

	public int getAwayPoints() {
		return awayPoints;
	}

	public int getHomeRebounds() {
		return homeRebounds;
	}

	public int getAwayRebounds() {
		return awayRebounds;
	}

	public int getHomeAssists() {
		return homeAssists;
	}

	public int getAwayAssists() {
		return awayAssists;
	}

	public int getHomeTurnovers() {
		return homeTurnovers;
	}

	public int getAwayTurnovers() {
		return awayTurnovers;
	}

	public String getHomeFgPercent() {
		return formatPercent(homeTwoMade + homeThreeMade, homeFgAttempts);
	}

	public String getAwayFgPercent() {
		return formatPercent(awayTwoMade + awayThreeMade, awayFgAttempts);
	}

	public String getHomeThreePercent() {
		return formatPercent(homeThreeMade, homeThreeAttempts);
	}

	public String getAwayThreePercent() {
		return formatPercent(awayThreeMade, awayThreeAttempts);
	}

	public String getHomeBestPlayersText() {
		return buildTopPlayersText(homePlayerPoints);
	}

	public String getAwayBestPlayersText() {
		return buildTopPlayersText(awayPlayerPoints);
	}

	private int getPlayerPoints(HashMap<String, Integer> map, String playerName) {
		Integer current = map.get(playerName);
		if (current == null) {
			return 0;
		}
		return current.intValue();
	}

	private String formatPercent(int made, int attempts) {
		if (attempts <= 0) {
			return "0%";
		}
		return (int) Math.round((made * 100.0) / attempts) + "%";
	}

	private String buildTopPlayersText(HashMap<String, Integer> players) {
		String bestName = "-";
		int bestPoints = 0;
		String secondName = "-";
		int secondPoints = 0;

		for (String name : players.keySet()) {
			int points = players.get(name).intValue();
			if (points > bestPoints) {
				secondName = bestName;
				secondPoints = bestPoints;
				bestName = name;
				bestPoints = points;
			} else if (points > secondPoints) {
				secondName = name;
				secondPoints = points;
			}
		}

		if ("-".equals(bestName)) {
			return "-";
		}
		if ("-".equals(secondName)) {
			return "<html>" + bestName + " (" + bestPoints + " pts)</html>";
		}
		return "<html>" + bestName + " (" + bestPoints + " pts)<br>" + secondName + " (" + secondPoints
				+ " pts)</html>";
	}

	private class StatsVisitor implements ActionResultVisitor<Void> {
		private HomePlayerChecker homePlayerChecker;

		public StatsVisitor(HomePlayerChecker homePlayerChecker) {
			this.homePlayerChecker = homePlayerChecker;
		}

		@Override
		public Void visit(PointScored pointScored) {
			Player scorer = pointScored.getScorerPlayer();
			boolean homeScorer = homePlayerChecker.isHomePlayer(scorer);
			String shotType = "";
			if (pointScored.getOffensiveAction() != null) {
				shotType = pointScored.getOffensiveAction().getName();
			}

			int points;
			if (SimulationConfiguration.THREEPOINT.equals(shotType)) {
				points = 3;
			} else if (SimulationConfiguration.TWOPOINT.equals(shotType)) {
				points = 2;
			} else {
				points = 1;
			}

			if (homeScorer) {
				homePoints += points;
				homePlayerPoints.put(scorer.getName(), getPlayerPoints(homePlayerPoints, scorer.getName()) + points);
			} else {
				awayPoints += points;
				awayPlayerPoints.put(scorer.getName(), getPlayerPoints(awayPlayerPoints, scorer.getName()) + points);
			}

			if (SimulationConfiguration.THREEPOINT.equals(shotType)) {
				if (homeScorer) {
					homeThreeMade++;
					homeThreeAttempts++;
					homeFgAttempts++;
				} else {
					awayThreeMade++;
					awayThreeAttempts++;
					awayFgAttempts++;
				}
			} else if (SimulationConfiguration.TWOPOINT.equals(shotType)) {
				if (homeScorer) {
					homeTwoMade++;
					homeFgAttempts++;
				} else {
					awayTwoMade++;
					awayFgAttempts++;
				}
			}

			Player assist = pointScored.getAssistPlayer();
			if (assist != null) {
				if (homePlayerChecker.isHomePlayer(assist)) {
					homeAssists++;
				} else {
					awayAssists++;
				}
			}
			return null;
		}

		@Override
		public Void visit(Turnover turnover) {
			if (homePlayerChecker.isHomePlayer(turnover.getDefensePlayer())) {
				homeTurnovers++;
			} else {
				awayTurnovers++;
			}
			return null;
		}

		@Override
		public Void visit(Block block) {
			if (homePlayerChecker.isHomePlayer(block.getBlockingPlayer())) {
				awayFgAttempts++;
			} else {
				homeFgAttempts++;
			}
			return null;
		}

		@Override
		public Void visit(Rebound rebound) {
			if (homePlayerChecker.isHomePlayer(rebound.getReboundPlayer())) {
				homeRebounds++;
			} else {
				awayRebounds++;
			}
			return null;
		}

		@Override
		public Void visit(EndOfTime endOfTime) {
			return null;
		}
	}

	public static class LiveAction {
		private int quarter;
		private ActionResult action;

		public LiveAction(int quarter, ActionResult action) {
			this.quarter = quarter;
			this.action = action;
		}

		public int getQuarter() {
			return quarter;
		}

		public ActionResult getAction() {
			return action;
		}
	}

	public static class SavedLiveState {
		int liveActionIndex;
		int homePoints;
		int awayPoints;
		int homeRebounds;
		int awayRebounds;
		int homeAssists;
		int awayAssists;
		int homeTurnovers;
		int awayTurnovers;
		int homeTwoMade;
		int awayTwoMade;
		int homeThreeMade;
		int awayThreeMade;
		int homeFgAttempts;
		int awayFgAttempts;
		int homeThreeAttempts;
		int awayThreeAttempts;
		HashMap<String, Integer> homePlayerPoints;
		HashMap<String, Integer> awayPlayerPoints;

		public int getLiveActionIndex() {
			return liveActionIndex;
		}

		public void setLiveActionIndex(int liveActionIndex) {
			this.liveActionIndex = liveActionIndex;
		}
	}
}
