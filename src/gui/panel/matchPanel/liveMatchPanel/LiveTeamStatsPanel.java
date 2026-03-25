package gui.panel.matchPanel.liveMatchPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.panel.common.SectionTitle;
import gui.panel.mapPanel.effectifPanel.playerPanel.PlayerSummaryPanel;
import process.manager.PlayerLiveSummary;

public class LiveTeamStatsPanel extends JPanel {
	private JLabel pointsLabel;
	private JLabel reboundsLabel;
	private JLabel assistsLabel;
	private JLabel turnoversLabel;
	private JLabel fgLabel;
	private JLabel threeLabel;
	private PlayerSummaryPanel firstBestPlayerPanel;
	private PlayerSummaryPanel secondBestPlayerPanel;

	public LiveTeamStatsPanel() {
		super(new BorderLayout(0, 12));
		create();
		organize();
	}

	private void create() {
		setOpaque(false);

		JPanel statsPanel = new JPanel(new GridLayout(6, 1, 0, 6));
		statsPanel.setOpaque(false);

		pointsLabel = new JLabel("Points : 0");
		reboundsLabel = new JLabel("Rebonds : 0");
		assistsLabel = new JLabel("Passes : 0");
		turnoversLabel = new JLabel("Turnovers : 0");
		fgLabel = new JLabel("FG% : 0%");
		threeLabel = new JLabel("3PT% : 0%");
		firstBestPlayerPanel = new PlayerSummaryPanel();
		secondBestPlayerPanel = new PlayerSummaryPanel();

		statsPanel.add(pointsLabel);
		statsPanel.add(reboundsLabel);
		statsPanel.add(assistsLabel);
		statsPanel.add(turnoversLabel);
		statsPanel.add(fgLabel);
		statsPanel.add(threeLabel);
		add(statsPanel, BorderLayout.NORTH);
		add(buildBestPlayersPanel(), BorderLayout.CENTER);
	}

	private void organize() {
	}

	private JPanel buildBestPlayersPanel() {
		JPanel bestPlayersPanel = new JPanel(new BorderLayout());
		bestPlayersPanel.setOpaque(false);
		bestPlayersPanel.add(new SectionTitle("MEILLEURS JOUEURS", ""), BorderLayout.NORTH);

		JPanel bestPlayersContent = new JPanel();
		bestPlayersContent.setOpaque(false);
		bestPlayersContent.setLayout(new BoxLayout(bestPlayersContent, BoxLayout.Y_AXIS));
		bestPlayersContent.add(Box.createVerticalStrut(4));
		bestPlayersContent.add(firstBestPlayerPanel);
		bestPlayersContent.add(Box.createVerticalStrut(8));
		bestPlayersContent.add(secondBestPlayerPanel);
		bestPlayersContent.setPreferredSize(new Dimension(10, 220));

		bestPlayersPanel.add(bestPlayersContent, BorderLayout.CENTER);
		return bestPlayersPanel;
	}

	public void updateStats(int points, int rebounds, int assists, int turnovers, String fg, String three,
			PlayerLiveSummary[] bestPlayers) {
		pointsLabel.setText("Points : " + points);
		reboundsLabel.setText("Rebonds : " + rebounds);
		assistsLabel.setText("Passes : " + assists);
		turnoversLabel.setText("Turnovers : " + turnovers);
		fgLabel.setText("FG% : " + fg);
		threeLabel.setText("3PT% : " + three);
		updatePlayerPanel(firstBestPlayerPanel, bestPlayers, 0);
		updatePlayerPanel(secondBestPlayerPanel, bestPlayers, 1);
	}

	private void updatePlayerPanel(PlayerSummaryPanel playerPanel, PlayerLiveSummary[] bestPlayers, int index) {
		if (bestPlayers == null || index >= bestPlayers.length || bestPlayers[index] == null) {
			playerPanel.updateSummary(null, 0);
			return;
		}
		playerPanel.updateSummary(bestPlayers[index].getPlayer(), bestPlayers[index].getPoints());
	}
}
