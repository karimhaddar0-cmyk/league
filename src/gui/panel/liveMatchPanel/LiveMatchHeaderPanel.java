package gui.panel.liveMatchPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.panel.teamPanel.TeamLogoPanel;

public class LiveMatchHeaderPanel extends JPanel {
	private static final Color TITLE_COLOR = new Color(0x17, 0x31, 0x74);
	private static final Color SUBTITLE_COLOR = new Color(0x6D, 0x75, 0x83);
	private static final Color PRIMARY_COLOR = new Color(0x2F, 0x80, 0xA9);
	private static final Color DANGER_COLOR = new Color(0xE0, 0x00, 0x00);
	private static final Color TEXT_COLOR = new Color(90, 90, 90);

	private JButton backButton;
	private JButton playButton;
	private JButton nextQuarterButton;
	private JButton pauseButton;
	private TeamLogoPanel homeLogoPanel;
	private TeamLogoPanel awayLogoPanel;
	private JLabel homeNameLabel;
	private JLabel awayNameLabel;
	private JLabel homeRoleLabel;
	private JLabel awayRoleLabel;
	private JLabel homeScoreLabel;
	private JLabel awayScoreLabel;
	private JLabel quarterLabel;
	private JLabel quarterTimeLabel;

	public LiveMatchHeaderPanel() {
		super(new BorderLayout(16, 0));
		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 225, 225)),
				BorderFactory.createEmptyBorder(12, 16, 12, 16)));

		backButton = new JButton("Retour");
		playButton = new JButton("Play");
		nextQuarterButton = new JButton("Quart");
		pauseButton = new JButton("Pause");
		homeLogoPanel = new TeamLogoPanel("", 56);
		awayLogoPanel = new TeamLogoPanel("", 56);
		homeNameLabel = createNameLabel();
		awayNameLabel = createNameLabel();
		homeRoleLabel = createRoleLabel("Domicile");
		awayRoleLabel = createRoleLabel("Extérieur");
		homeScoreLabel = createScoreLabel(PRIMARY_COLOR);
		awayScoreLabel = createScoreLabel(TEXT_COLOR);
		quarterLabel = new JLabel("Q1", JLabel.CENTER);
		quarterTimeLabel = new JLabel("12:00", JLabel.CENTER);

		styleButtons();
		styleQuarterLabels();

		add(buildLeftPanel(), BorderLayout.WEST);
		add(buildCenterPanel(), BorderLayout.CENTER);
		add(buildRightPanel(), BorderLayout.EAST);
	}

	public JButton getBackButton() {
		return backButton;
	}

	public JButton getPlayButton() {
		return playButton;
	}

	public JButton getNextQuarterButton() {
		return nextQuarterButton;
	}

	public JButton getPauseButton() {
		return pauseButton;
	}

	public void updateHeader(String homeTeamName, String awayTeamName, int homeScore, int awayScore,
			String quarterLabelText, String quarterTimeText) {
		homeLogoPanel.setTeamName(homeTeamName);
		awayLogoPanel.setTeamName(awayTeamName);
		homeNameLabel.setText(extractShortName(homeTeamName));
		awayNameLabel.setText(extractShortName(awayTeamName));
		homeScoreLabel.setText(String.valueOf(homeScore));
		awayScoreLabel.setText(String.valueOf(awayScore));
		quarterLabel.setText(quarterLabelText);
		quarterTimeLabel.setText(quarterTimeText);
	}

	private JPanel buildLeftPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.setOpaque(false);
		panel.add(backButton);
		return panel;
	}

	private JPanel buildCenterPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
		panel.setOpaque(false);
		panel.add(homeLogoPanel);
		panel.add(buildTeamPanel(homeNameLabel, homeRoleLabel));
		panel.add(homeScoreLabel);
		panel.add(buildDashLabel());
		panel.add(awayScoreLabel);
		panel.add(buildTeamPanel(awayNameLabel, awayRoleLabel));
		panel.add(awayLogoPanel);
		return panel;
	}

	private JPanel buildRightPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		panel.setOpaque(false);
		panel.add(buildQuarterPanel());
		panel.add(playButton);
		panel.add(nextQuarterButton);
		panel.add(pauseButton);
		return panel;
	}

	private JPanel buildTeamPanel(JLabel nameLabel, JLabel roleLabel) {
		JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
		panel.setOpaque(false);
		panel.add(nameLabel);
		panel.add(roleLabel);
		return panel;
	}

	private JPanel buildQuarterPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
		panel.setOpaque(false);
		panel.add(quarterLabel);
		panel.add(quarterTimeLabel);
		return panel;
	}

	private JLabel buildDashLabel() {
		JLabel label = new JLabel("-");
		label.setForeground(TEXT_COLOR);
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
		return label;
	}

	private JLabel createNameLabel() {
		JLabel label = new JLabel("Equipe");
		label.setForeground(TITLE_COLOR);
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		return label;
	}

	private JLabel createRoleLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(SUBTITLE_COLOR);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		return label;
	}

	private JLabel createScoreLabel(Color color) {
		JLabel label = new JLabel("0");
		label.setForeground(color);
		label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
		return label;
	}

	private void styleQuarterLabels() {
		quarterLabel.setForeground(TITLE_COLOR);
		quarterLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		quarterTimeLabel.setForeground(SUBTITLE_COLOR);
		quarterTimeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
	}

	private void styleButtons() {
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setForeground(TEXT_COLOR);
		backButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

		styleActionButton(playButton, PRIMARY_COLOR);
		styleActionButton(nextQuarterButton, new Color(90, 90, 90));
		styleActionButton(pauseButton, DANGER_COLOR);
	}

	private void styleActionButton(JButton button, Color background) {
		button.setFocusPainted(false);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
		button.setBackground(background);
		button.setForeground(Color.WHITE);
		button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		button.setPreferredSize(new Dimension(84, 34));
	}

	private String extractShortName(String teamName) {
		String[] words = teamName.split(" ");
		if (words.length == 0) {
			return teamName;
		}
		return words[words.length - 1];
	}
}
