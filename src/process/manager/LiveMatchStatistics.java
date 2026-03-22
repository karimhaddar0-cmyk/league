<<<<<<< HEAD:src/gui/management/LiveMatchStatistics.java
package gui.management;
=======
package process.manager;
>>>>>>> main:src/process/manager/LiveMatchStatistics.java

import java.util.HashMap;

import data.player.Player;
import data.sport.play.action.ActionResult;
import data.sport.setup.Game;
import process.visitor.actionresult.StatsVisitor;

public class LiveMatchStatistics {
	
	private Game game ; 
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
	private HashMap<String, Player> homePlayers;
	private HashMap<String, Player> awayPlayers;

	public LiveMatchStatistics() {
		homePlayerPoints = new HashMap<String, Integer>();
		awayPlayerPoints = new HashMap<String, Integer>();
		homePlayers = new HashMap<String, Player>();
		awayPlayers = new HashMap<String, Player>();
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
		homePlayers.clear();
		awayPlayers.clear();
	}

	public void applyAction(ActionResult action) {
		action.accept(new StatsVisitor(this));
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

	public PlayerLiveSummary[] getHomeBestPlayers() {
		return buildTopPlayers(homePlayerPoints, homePlayers);
	}

	public PlayerLiveSummary[] getAwayBestPlayers() {
		return buildTopPlayers(awayPlayerPoints, awayPlayers);
	}

	private String formatPercent(int made, int attempts) {
		if (attempts <= 0) {
			return "0%";
		}
		return (int) Math.round((made * 100.0) / attempts) + "%";
	}

	private PlayerLiveSummary[] buildTopPlayers(HashMap<String, Integer> playerPoints, HashMap<String, Player> players) {
		PlayerLiveSummary[] topPlayers = new PlayerLiveSummary[2];
		for (String playerName : playerPoints.keySet()) {
			Player player = players.get(playerName);
			if (player == null) {
				continue;
			}
			int points = playerPoints.get(playerName).intValue();
			if (topPlayers[0] == null || points > topPlayers[0].getPoints()) {
				topPlayers[1] = topPlayers[0];
				topPlayers[0] = new PlayerLiveSummary(player, points);
			} else if (topPlayers[1] == null || points > topPlayers[1].getPoints()) {
				topPlayers[1] = new PlayerLiveSummary(player, points);
			}
		}
		return topPlayers;
	}

	public int getHomeTwoMade() {
		return homeTwoMade;
	}

	public void setHomeTwoMade(int homeTwoMade) {
		this.homeTwoMade = homeTwoMade;
	}

	public int getAwayTwoMade() {
		return awayTwoMade;
	}

	public void setAwayTwoMade(int awayTwoMade) {
		this.awayTwoMade = awayTwoMade;
	}

	public int getHomeThreeMade() {
		return homeThreeMade;
	}

	public void setHomeThreeMade(int homeThreeMade) {
		this.homeThreeMade = homeThreeMade;
	}

	public int getAwayThreeMade() {
		return awayThreeMade;
	}

	public void setAwayThreeMade(int awayThreeMade) {
		this.awayThreeMade = awayThreeMade;
	}

	public int getHomeFgAttempts() {
		return homeFgAttempts;
	}

	public void setHomeFgAttempts(int homeFgAttempts) {
		this.homeFgAttempts = homeFgAttempts;
	}

	public int getAwayFgAttempts() {
		return awayFgAttempts;
	}

	public void setAwayFgAttempts(int awayFgAttempts) {
		this.awayFgAttempts = awayFgAttempts;
	}

	public int getHomeThreeAttempts() {
		return homeThreeAttempts;
	}

	public void setHomeThreeAttempts(int homeThreeAttempts) {
		this.homeThreeAttempts = homeThreeAttempts;
	}

	public int getAwayThreeAttempts() {
		return awayThreeAttempts;
	}

	public void setAwayThreeAttempts(int awayThreeAttempts) {
		this.awayThreeAttempts = awayThreeAttempts;
	}

	public HashMap<String, Integer> getHomePlayerPoints() {
		return homePlayerPoints;
	}

	public void setHomePlayerPoints(HashMap<String, Integer> homePlayerPoints) {
		this.homePlayerPoints = homePlayerPoints;
	}

	public HashMap<String, Integer> getAwayPlayerPoints() {
		return awayPlayerPoints;
	}

	public void setAwayPlayerPoints(HashMap<String, Integer> awayPlayerPoints) {
		this.awayPlayerPoints = awayPlayerPoints;
	}

	public HashMap<String, Player> getHomePlayers() {
		return homePlayers;
	}

	public void setHomePlayers(HashMap<String, Player> homePlayers) {
		this.homePlayers = homePlayers;
	}

	public HashMap<String, Player> getAwayPlayers() {
		return awayPlayers;
	}

	public void setAwayPlayers(HashMap<String, Player> awayPlayers) {
		this.awayPlayers = awayPlayers;
	}

	public void setHomePoints(int homePoints) {
		this.homePoints = homePoints;
	}

	public void setAwayPoints(int awayPoints) {
		this.awayPoints = awayPoints;
	}

	public void setHomeRebounds(int homeRebounds) {
		this.homeRebounds = homeRebounds;
	}

	public void setAwayRebounds(int awayRebounds) {
		this.awayRebounds = awayRebounds;
	}

	public void setHomeAssists(int homeAssists) {
		this.homeAssists = homeAssists;
	}

	public void setAwayAssists(int awayAssists) {
		this.awayAssists = awayAssists;
	}

	public void setHomeTurnovers(int homeTurnovers) {
		this.homeTurnovers = homeTurnovers;
	}

	public void setAwayTurnovers(int awayTurnovers) {
		this.awayTurnovers = awayTurnovers;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
