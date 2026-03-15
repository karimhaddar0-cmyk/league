package gui.panel.playerPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.player.Player;
import gui.panel.common.DashboardCard;

public class PlayerSummaryPanel extends DashboardCard {
	private PlayerPortraitPanel portraitPanel;
	private JLabel nameLabel;
	private JLabel infoLabel;

	public PlayerSummaryPanel() {
		create();
		organize();
		updateSummary(null, 0);
	}

	private void create() {
		portraitPanel = new PlayerPortraitPanel();
		nameLabel = new JLabel();
		infoLabel = new JLabel();
		nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
	}

	private void organize() {
		setLayout(new BorderLayout(14, 0));

		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(Box.createVerticalGlue());
		textPanel.add(nameLabel);
		textPanel.add(Box.createVerticalStrut(6));
		textPanel.add(infoLabel);
		textPanel.add(Box.createVerticalGlue());

		add(portraitPanel, BorderLayout.WEST);
		add(textPanel, BorderLayout.CENTER);

		setPreferredSize(new Dimension(10, 84));
	}

	public void updateSummary(Player player, int points) {
		portraitPanel.setPlayer(player);
		if (player == null) {
			nameLabel.setText("Aucun joueur");
			infoLabel.setText("Points : 0");
			return;
		}

		nameLabel.setText(player.getName());
		infoLabel.setText("Points : " + points);
	}
}
