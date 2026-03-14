package gui.panel.matchPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.sport.setup.Game;
import data.sport.setup.GameResult;
import gui.panel.teamPanel.TeamLogoPanel;

public class MatchResultPanel extends JPanel {
	private static final Color TITLE_COLOR = new Color(0x17, 0x31, 0x74);
	private static final Color SUBTITLE_COLOR = new Color(0x6D, 0x75, 0x83);
	private static final Color PRIMARY_BAR_COLOR = new Color(0x2F, 0x80, 0xA9);

	private JLabel titleLabel;
	private JLabel matchStatusLabel;
	private TeamLogoPanel homeLogoPanel;
	private TeamLogoPanel awayLogoPanel;
	private JLabel homeNameLabel;
	private JLabel awayNameLabel;
	private JLabel homeCityLabel;
	private JLabel awayCityLabel;
	private JLabel mainScoreLabel;
	private JLabel quarterTitleLabel;
	private JLabel homeQuarterTeamLabel;
	private JLabel awayQuarterTeamLabel;
	private JLabel[] homeQuarterLabels;
	private JLabel[] awayQuarterLabels;
	private JLabel homeQuarterTotalLabel;
	private JLabel awayQuarterTotalLabel;

	public MatchResultPanel() {
		super(new BorderLayout(0, 16));
		setOpaque(false);
		add(buildScoreHeaderPanel(), BorderLayout.NORTH);
		add(buildQuarterPanel(), BorderLayout.CENTER);
	}

	public void showHiddenState(Game game, String dayLabel) {
		String homeName = game.getGameContext().getHomeTeam().getName();
		String awayName = game.getGameContext().getAwayTeam().getName();
		updateTeamLabels(homeName, awayName, dayLabel);
		matchStatusLabel.setText("À venir");
		mainScoreLabel.setText("--");
		quarterTitleLabel.setText("Résultats masqués");
		homeQuarterTeamLabel.setText(extractShortName(homeName));
		awayQuarterTeamLabel.setText(extractShortName(awayName));
		resetQuarterTable();
	}

	public void showGame(Game game, String dayLabel) {
		String homeName = game.getGameContext().getHomeTeam().getName();
		String awayName = game.getGameContext().getAwayTeam().getName();
		updateTeamLabels(homeName, awayName, dayLabel);
		matchStatusLabel.setText("Terminé");
		mainScoreLabel.setText(game.getHomeFinalScore() + " - " + game.getAwayFinalScore());
		quarterTitleLabel.setText("Match terminé");
		updateQuarterTable(game.getQuarterResults(), homeName, awayName);
	}

	public void showEmptyState() {
		titleLabel.setText("SAISON RÉGULIÈRE");
		matchStatusLabel.setText("À venir");
		homeLogoPanel.setTeamName("");
		awayLogoPanel.setTeamName("");
		homeNameLabel.setText("Home");
		awayNameLabel.setText("Away");
		homeCityLabel.setText("-");
		awayCityLabel.setText("-");
		mainScoreLabel.setText("--");
		quarterTitleLabel.setText("Résultats masqués");
		homeQuarterTeamLabel.setText("Home");
		awayQuarterTeamLabel.setText("Away");
		resetQuarterTable();
	}

	private JPanel buildScoreHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout(12, 0));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		topPanel.setOpaque(false);
		titleLabel = new JLabel("SAISON RÉGULIÈRE");
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		titleLabel.setForeground(SUBTITLE_COLOR);
		matchStatusLabel = new JLabel("À venir");
		matchStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		matchStatusLabel.setForeground(PRIMARY_BAR_COLOR);
		topPanel.add(titleLabel);
		topPanel.add(matchStatusLabel);

		JPanel centerPanel = new JPanel(new GridLayout(1, 3, 12, 0));
		centerPanel.setOpaque(false);
		centerPanel.add(buildTeamPanel(true));
		centerPanel.add(buildScorePanel());
		centerPanel.add(buildTeamPanel(false));

		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(centerPanel, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildTeamPanel(boolean home) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		TeamLogoPanel logoPanel = new TeamLogoPanel("", 70);
		logoPanel.setAlignmentX(CENTER_ALIGNMENT);

		JLabel nameLabel = new JLabel(home ? "Home" : "Away");
		nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		nameLabel.setForeground(TITLE_COLOR);
		nameLabel.setAlignmentX(CENTER_ALIGNMENT);

		JLabel cityLabel = new JLabel("-");
		cityLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		cityLabel.setForeground(SUBTITLE_COLOR);
		cityLabel.setAlignmentX(CENTER_ALIGNMENT);

		panel.add(logoPanel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(nameLabel);
		panel.add(Box.createVerticalStrut(4));
		panel.add(cityLabel);

		if (home) {
			homeLogoPanel = logoPanel;
			homeNameLabel = nameLabel;
			homeCityLabel = cityLabel;
		} else {
			awayLogoPanel = logoPanel;
			awayNameLabel = nameLabel;
			awayCityLabel = cityLabel;
		}
		return panel;
	}

	private JPanel buildScorePanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		mainScoreLabel = new JLabel("0 - 0", JLabel.CENTER);
		mainScoreLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
		mainScoreLabel.setForeground(TITLE_COLOR);
		mainScoreLabel.setAlignmentX(CENTER_ALIGNMENT);
		mainScoreLabel.setHorizontalAlignment(JLabel.CENTER);

		quarterTitleLabel = new JLabel("Résultat masqué");
		quarterTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		quarterTitleLabel.setForeground(SUBTITLE_COLOR);
		quarterTitleLabel.setAlignmentX(CENTER_ALIGNMENT);

		panel.add(Box.createVerticalGlue());
		panel.add(mainScoreLabel);
		panel.add(Box.createVerticalStrut(4));
		panel.add(quarterTitleLabel);
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JPanel buildQuarterPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

		JLabel title = new JLabel("SCORE PAR QUART-TEMPS");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		title.setForeground(TITLE_COLOR);
		panel.add(title, BorderLayout.NORTH);

		JPanel table = new JPanel(new GridLayout(3, 6, 12, 8));
		table.setOpaque(false);
		table.add(new JLabel("ÉQUIPE"));
		table.add(new JLabel("Q1", JLabel.CENTER));
		table.add(new JLabel("Q2", JLabel.CENTER));
		table.add(new JLabel("Q3", JLabel.CENTER));
		table.add(new JLabel("Q4", JLabel.CENTER));
		table.add(new JLabel("TOTAL", JLabel.CENTER));

		homeQuarterTeamLabel = new JLabel("Home");
		awayQuarterTeamLabel = new JLabel("Away");
		homeQuarterLabels = createQuarterLabels();
		awayQuarterLabels = createQuarterLabels();
		homeQuarterTotalLabel = new JLabel("-", JLabel.CENTER);
		awayQuarterTotalLabel = new JLabel("-", JLabel.CENTER);

		table.add(homeQuarterTeamLabel);
		addQuarterRow(table, homeQuarterLabels, homeQuarterTotalLabel);
		table.add(awayQuarterTeamLabel);
		addQuarterRow(table, awayQuarterLabels, awayQuarterTotalLabel);

		panel.add(table, BorderLayout.CENTER);
		return panel;
	}

	private JLabel[] createQuarterLabels() {
		return new JLabel[] {
				new JLabel("-", JLabel.CENTER),
				new JLabel("-", JLabel.CENTER),
				new JLabel("-", JLabel.CENTER),
				new JLabel("-", JLabel.CENTER)
		};
	}

	private void addQuarterRow(JPanel table, JLabel[] quarterLabels, JLabel totalLabel) {
		for (int i = 0; i < quarterLabels.length; i++) {
			table.add(quarterLabels[i]);
		}
		table.add(totalLabel);
	}

	private void updateTeamLabels(String homeName, String awayName, String dayLabel) {
		titleLabel.setText("SAISON RÉGULIÈRE - " + dayLabel.toUpperCase());
		homeLogoPanel.setTeamName(homeName);
		awayLogoPanel.setTeamName(awayName);
		homeNameLabel.setText(extractShortName(homeName));
		awayNameLabel.setText(extractShortName(awayName));
		homeCityLabel.setText(extractCity(homeName));
		awayCityLabel.setText(extractCity(awayName));
	}

	private void updateQuarterTable(GameResult[] quarterResults, String homeName, String awayName) {
		homeQuarterTeamLabel.setText(extractShortName(homeName));
		awayQuarterTeamLabel.setText(extractShortName(awayName));
		int homeTotal = 0;
		int awayTotal = 0;
		for (int i = 0; i < 4; i++) {
			int homeScore = 0;
			int awayScore = 0;
			if (quarterResults != null && quarterResults.length > i && quarterResults[i] != null) {
				homeScore = quarterResults[i].getScorehomeTeam();
				awayScore = quarterResults[i].getScoreAwayTeam();
			}
			homeQuarterLabels[i].setText(String.valueOf(homeScore));
			awayQuarterLabels[i].setText(String.valueOf(awayScore));
			homeTotal += homeScore;
			awayTotal += awayScore;
		}
		homeQuarterTotalLabel.setText(String.valueOf(homeTotal));
		awayQuarterTotalLabel.setText(String.valueOf(awayTotal));
	}

	private void resetQuarterTable() {
		for (int i = 0; i < 4; i++) {
			homeQuarterLabels[i].setText("-");
			awayQuarterLabels[i].setText("-");
		}
		homeQuarterTotalLabel.setText("-");
		awayQuarterTotalLabel.setText("-");
	}

	private String extractShortName(String teamName) {
		String[] words = teamName.split(" ");
		if (words.length == 0) {
			return teamName;
		}
		return words[words.length - 1];
	}

	private String extractCity(String teamName) {
		int index = teamName.lastIndexOf(' ');
		if (index <= 0) {
			return teamName;
		}
		return teamName.substring(0, index);
	}
}
