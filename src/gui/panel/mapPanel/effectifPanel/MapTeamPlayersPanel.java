package gui.panel.mapPanel.effectifPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.player.Player;
import data.team.Team;
import process.utilitary.PlayerStatUtil;

public class MapTeamPlayersPanel extends JPanel {
	private JLabel[] playerLabels;

	public MapTeamPlayersPanel() {
		create();
		organize();
		updateTeam(null);
	}

	private void create() {
		playerLabels = new JLabel[10];
		for (int i = 0; i < playerLabels.length; i++) {
			playerLabels[i] = new JLabel("-");
			playerLabels[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
			playerLabels[i].setForeground(new Color(40, 40, 40));
		}
	}

	private void organize() {
		setOpaque(false);
		setLayout(new GridLayout(5, 2, 10, 10));
		for (int i = 0; i < playerLabels.length; i++) {
			add(playerLabels[i]);
		}
	}

	public void updateTeam(Team team) {
		if (team == null) {
			for (int i = 0; i < playerLabels.length; i++) {
				playerLabels[i].setText("-");
			}
			return;
		}

		ArrayList<Player> players = new ArrayList<Player>(team.getPlayers().values());
		PlayerStatUtil.sortPlayersByDisplayedNote(players);

		for (int i = 0; i < playerLabels.length; i++) {
			if (i < players.size()) {
				Player player = players.get(i);
				playerLabels[i].setText((int) Math.round(PlayerStatUtil.getDisplayedNote(player)) + "  " + player.getName());
			} else {
				playerLabels[i].setText("-");
			}
		}
	}

}
