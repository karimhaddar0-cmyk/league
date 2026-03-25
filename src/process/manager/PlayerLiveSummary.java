package process.manager;

import data.player.Player;

public class PlayerLiveSummary {
	private Player player;
	private int points;

	public PlayerLiveSummary(Player player, int points) {
		this.player = player;
		this.points = points;
	}

	public Player getPlayer() {
		return player;
	}

	public int getPoints() {
		return points;
	}
}
