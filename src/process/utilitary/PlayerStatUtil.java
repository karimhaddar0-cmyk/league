package process.utilitary;

import java.util.ArrayList;

import data.player.Asset;
import data.player.Player;

public class PlayerStatUtil {

	public static Asset getDisplayedAssets(Player player, boolean currentSeasonSelected) {
		if (!currentSeasonSelected) {
			return player.getPreSeasonAssets();
		}
		if (player.getCurrentSeasonAssets().getMinutesPlayedPerMatch() > 0) {
			return player.getCurrentSeasonAssets();
		}
		return player.getPreSeasonAssets();
	}

	public static double getDisplayedNote(Player player) {
		if (player.getCurrentSeasonAssets().getNote() > 0) {
			return player.getCurrentSeasonAssets().getNote();
		}
		return player.getPreSeasonAssets().getNote();
	}

	public static void sortPlayersByDisplayedNote(ArrayList<Player> players) {
		for (int i = 0; i < players.size() - 1; i++) {
			for (int j = i + 1; j < players.size(); j++) {
				double firstNote = getDisplayedNote(players.get(i));
				double secondNote = getDisplayedNote(players.get(j));
				if (secondNote > firstNote) {
					Player currentPlayer = players.get(i);
					players.set(i, players.get(j));
					players.set(j, currentPlayer);
				}
			}
		}
	}
}
