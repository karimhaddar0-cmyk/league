package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import data.team.Team;
import gui.panel.common.BuildBox;
import gui.panel.common.DashboardCard;
import gui.panel.common.PlayerDisplayUtil;
import gui.panel.mapPanel.effectifPanel.teamPanel.TeamLogoPanel;
import gui.panel.mapPanel.effectifPanel.teamPanel.TeamRosterPanel;
import process.utilitary.FinanceUtilitary;
import process.utilitary.TeamStatUtil;

public class RosterDashboard extends JPanel {
	private static final int DASHBOARD_SPACING = 16;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	private Team selectedTeam;
	private Runnable backToMapAction;
	private boolean currentSeasonSelected;

	private JButton backButton;
	private JButton currentSeasonButton;
	private JButton previousSeasonButton;
	private JLabel teamNameLabel;
	private JLabel subtitleLabel;
	private TeamLogoPanel teamLogoPanel;
	private JLabel playersCountValueLabel;
	private JLabel payrollValueLabel;
	private JLabel averagePointsValueLabel;
	private TeamRosterPanel rosterPanel;

	public RosterDashboard() {
		create();
		organize();
		actions();
		updateDashboard();
	}

	private void create() {
		currentSeasonSelected = true;
		backButton = new JButton("Retour à la carte");
		currentSeasonButton = new JButton("Saison actuelle");
		previousSeasonButton = new JButton("Saison passée");
		teamNameLabel = new JLabel("Effectif");
		subtitleLabel = new JLabel("-");
		teamLogoPanel = new TeamLogoPanel("", 56);
		playersCountValueLabel = new JLabel("-");
		payrollValueLabel = new JLabel("-");
		averagePointsValueLabel = new JLabel("-");
		rosterPanel = new TeamRosterPanel();

		backButton.setFocusPainted(false);
		currentSeasonButton.setFocusPainted(false);
		previousSeasonButton.setFocusPainted(false);
		backButton.setAlignmentX(LEFT_ALIGNMENT);
		teamNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
		teamNameLabel.setForeground(new Color(0x17, 0x31, 0x74));
		subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		subtitleLabel.setForeground(new Color(110, 117, 131));
		subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);

		JPanel content = new JPanel(new BorderLayout(DASHBOARD_SPACING, DASHBOARD_SPACING));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(
				DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING));

		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildHeader() {
		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);

		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setAlignmentX(LEFT_ALIGNMENT);
		titlePanel.add(backButton);
		titlePanel.add(Box.createVerticalStrut(10));

		JPanel teamLine = new JPanel(new BorderLayout(12, 0));
		teamLine.setOpaque(false);
		teamLine.setAlignmentX(LEFT_ALIGNMENT);
		teamLine.add(teamLogoPanel, BorderLayout.WEST);
		teamLine.add(teamNameLabel, BorderLayout.CENTER);

		titlePanel.add(teamLine);
		titlePanel.add(Box.createVerticalStrut(4));
		titlePanel.add(subtitleLabel);

		header.add(titlePanel, BorderLayout.WEST);
		return header;
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new BorderLayout(0, DASHBOARD_SPACING));
		body.setOpaque(false);
		body.add(buildSeasonButtonsPanel(), BorderLayout.NORTH);
		body.add(buildCenterContentPanel(), BorderLayout.CENTER);
		return body;
	}

	private JPanel buildCenterContentPanel() {
		JPanel centerContentPanel = new JPanel(new BorderLayout(0, DASHBOARD_SPACING));
		centerContentPanel.setOpaque(false);
		centerContentPanel.add(buildSummaryPanel(), BorderLayout.NORTH);
		centerContentPanel.add(new BuildBox("LISTE DES JOUEURS", "Effectif complet", buildRosterContentPanel()), BorderLayout.CENTER);
		return centerContentPanel;
	}

	private JPanel buildSeasonButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(currentSeasonButton);
		buttonsPanel.add(Box.createHorizontalStrut(8));
		buttonsPanel.add(previousSeasonButton);
		updateSeasonButtonsStyle();
		return buttonsPanel;
	}

	private JPanel buildSummaryPanel() {
<<<<<<< HEAD
		JPanel summaryPanel = new JPanel(new java.awt.GridLayout(1, 3, DASHBOARD_SPACING, 0));
=======
		JPanel summaryPanel = new JPanel(new GridLayout(1, 4, DASHBOARD_SPACING, 0));
>>>>>>> main
		summaryPanel.setOpaque(false);
		summaryPanel.add(buildMetricCard("Joueurs", playersCountValueLabel));
		summaryPanel.add(buildMetricCard("Masse salariale", payrollValueLabel));
		summaryPanel.add(buildMetricCard("PPG moyen", averagePointsValueLabel));
		return summaryPanel;
	}

	private JPanel buildMetricCard(String title, JLabel valueLabel) {
		DashboardCard card = new DashboardCard();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		titleLabel.setForeground(new Color(110, 117, 131));
		valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
		valueLabel.setForeground(new Color(0x17, 0x31, 0x74));

		card.add(titleLabel);
		card.add(Box.createVerticalStrut(4));
		card.add(valueLabel);
		return card;
	}

	private JPanel buildRosterContentPanel() {
		JPanel container = new JPanel(new BorderLayout());
		container.setOpaque(false);
		container.add(rosterPanel, BorderLayout.CENTER);
		return container;
	}

	private void actions() {
		backButton.addActionListener(new BackToMapListener());
		currentSeasonButton.addActionListener(new CurrentSeasonListener());
		previousSeasonButton.addActionListener(new PreviousSeasonListener());
	}

	public void setSelectedTeam(Team team) {
		selectedTeam = team;
		updateDashboard();
	}

	public void setBackToMapAction(Runnable backToMapAction) {
		this.backToMapAction = backToMapAction;
	}

	private void updateDashboard() {
		updateSeasonButtonsStyle();
		if (selectedTeam == null) {
<<<<<<< HEAD
			teamLogoPanel.setTeamName("");
			teamNameLabel.setText("Effectif");
			subtitleLabel.setText("-");
			playersCountValueLabel.setText("-");
			payrollValueLabel.setText("-");
			averagePointsValueLabel.setText("-");
			rosterPanel.updateTeam(null, currentSeasonSelected);
=======
			showEmptyState();
>>>>>>> main
			return;
		}
		showTeamState();
	}

	private void showEmptyState() {
		teamLogoPanel.setTeamName("");
		teamNameLabel.setText("Effectif");
		subtitleLabel.setText("-");
		playersCountValueLabel.setText("-");
		payrollValueLabel.setText("-");
		averageNoteValueLabel.setText("-");
		averagePointsValueLabel.setText("-");
		rosterPanel.updateTeam(null, currentSeasonSelected);
	}

	private void showTeamState() {
		teamLogoPanel.setTeamName(selectedTeam.getName());
		teamNameLabel.setText(selectedTeam.getName());
		subtitleLabel.setText("Effectif complet");
		playersCountValueLabel.setText(String.valueOf(selectedTeam.getPlayers().size()));
		FinanceUtilitary.updateTeamPayroll(selectedTeam);
		payrollValueLabel.setText(PlayerDisplayUtil.formatSalary(selectedTeam.getTeamFinance().getPayroll()));
<<<<<<< HEAD
		averagePointsValueLabel.setText(PlayerDisplayUtil.formatOneDecimal(computeAveragePoints()));
		rosterPanel.updateTeam(selectedTeam, currentSeasonSelected);
	}

	private double computeAveragePoints() {
		ArrayList<Player> players = new ArrayList<Player>(selectedTeam.getPlayers().values());
		if (players.isEmpty()) {
			return 0;
		}

		double total = 0;
		for (Player player : players) {
			total += PlayerDisplayUtil.getDisplayedAssets(player, currentSeasonSelected).getPointPerMatch();
		}
		return total / players.size();
	}

=======
		averageNoteValueLabel
				.setText(PlayerDisplayUtil.formatOneDecimal(TeamStatUtil.getAverageNote(selectedTeam)) + "/100");
		averagePointsValueLabel.setText(
				PlayerDisplayUtil.formatOneDecimal(TeamStatUtil.getAveragePoints(selectedTeam, currentSeasonSelected)));
		rosterPanel.updateTeam(selectedTeam, currentSeasonSelected);
	}

>>>>>>> main
	private void updateSeasonButtonsStyle() {
		Color activeBackground = new Color(0x37, 0x84, 0xB3);
		Color inactiveBackground = new Color(240, 240, 240);
		Color activeForeground = Color.WHITE;
		Color inactiveForeground = new Color(60, 60, 60);

		currentSeasonButton.setOpaque(true);
		previousSeasonButton.setOpaque(true);
		currentSeasonButton.setBorderPainted(false);
		previousSeasonButton.setBorderPainted(false);

		if (currentSeasonSelected) {
			currentSeasonButton.setBackground(activeBackground);
			currentSeasonButton.setForeground(activeForeground);
			previousSeasonButton.setBackground(inactiveBackground);
			previousSeasonButton.setForeground(inactiveForeground);
		} else {
			currentSeasonButton.setBackground(inactiveBackground);
			currentSeasonButton.setForeground(inactiveForeground);
			previousSeasonButton.setBackground(activeBackground);
			previousSeasonButton.setForeground(activeForeground);
		}
	}

	private class BackToMapListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (backToMapAction != null) {
				backToMapAction.run();
			}
		}
	}

	private class CurrentSeasonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentSeasonSelected = true;
			updateDashboard();
		}
	}

	private class PreviousSeasonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentSeasonSelected = false;
			updateDashboard();
		}
	}
}
