package gui.panel.mapPanel.effectifPanel.playerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.player.Player;
import data.player.Asset;
import gui.panel.common.DashboardCard;
import gui.panel.common.PlayerDisplayUtil;
import process.utilitary.PlayerStatUtil;

public class PlayerRosterEntryPanel extends DashboardCard {
	private PlayerPortraitPanel portraitPanel;
	private JLabel nameLabel;
	private JLabel positionLabel;
	private JLabel statsLabel;
	private JLabel salaryLabel;

	public PlayerRosterEntryPanel() {
		create();
		organize();
	}

	private void create() {
		portraitPanel = new PlayerPortraitPanel(null, 48, 34);
		nameLabel = new JLabel("-");
		positionLabel = new JLabel("-");
		statsLabel = new JLabel("-");
		salaryLabel = new JLabel("-");

		nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		nameLabel.setForeground(new Color(0x17, 0x31, 0x74));
		positionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		positionLabel.setForeground(new Color(0x37, 0x84, 0xB3));
		statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
		statsLabel.setForeground(new Color(90, 90, 90));
		salaryLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		salaryLabel.setForeground(new Color(0x17, 0x31, 0x74));
	}

	private void organize() {
		setLayout(new BorderLayout(8, 0));
		setPreferredSize(new Dimension(10, 48));

		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JPanel topLine = new JPanel(new BorderLayout(6, 0));
		topLine.setOpaque(false);
		topLine.add(nameLabel, BorderLayout.CENTER);
		topLine.add(positionLabel, BorderLayout.EAST);

		JPanel bottomLine = new JPanel(new BorderLayout(4, 0));
		bottomLine.setOpaque(false);
		bottomLine.add(statsLabel, BorderLayout.CENTER);
		bottomLine.add(salaryLabel, BorderLayout.EAST);

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(topLine);
		centerPanel.add(Box.createVerticalStrut(3));
		centerPanel.add(bottomLine);
		centerPanel.add(Box.createVerticalGlue());

		add(portraitPanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);
	}

	public void updatePlayer(Player player, boolean currentSeasonSelected) {
		if (player == null) {
			portraitPanel.setPlayer(null);
			nameLabel.setText("-");
			positionLabel.setText("-");
			statsLabel.setText("-");
			salaryLabel.setText("-");
			return;
		}

		portraitPanel.setPlayer(player);
		nameLabel.setText(player.getName());
		positionLabel.setText(player.getPosition());
		statsLabel.setText(buildStatsText(player, currentSeasonSelected));
		salaryLabel.setText(PlayerDisplayUtil.formatSalary(player.getSalary()));
	}

	private String buildStatsText(Player player, boolean currentSeasonSelected) {
		Asset assets = PlayerStatUtil.getDisplayedAssets(player, currentSeasonSelected);
		double points = assets.getPointPerMatch();
		double assists = assets.getAssistPerMatch();
		double rebounds = assets.getReboundPerMatch();
		return PlayerDisplayUtil.formatOneDecimal(points) + " PPG  "
				+ PlayerDisplayUtil.formatOneDecimal(assists) + " APG  "
				+ PlayerDisplayUtil.formatOneDecimal(rebounds) + " RPG";
	}
}
