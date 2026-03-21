package gui.panel.mapPanel.effectifPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.team.Team;
import gui.panel.common.PlayerDisplayUtil;
import gui.panel.mapPanel.effectifPanel.teamPanel.TeamLogoPanel;

public class MapTeamSummaryPanel extends JPanel {
	private JLabel teamNameLabel;
	private JLabel payrollLabel;
	private JLabel stadiumNameLabel;
	private JLabel capacityLabel;
	private JButton openRosterButton;
	private TeamLogoPanel teamLogoPanel;

	public MapTeamSummaryPanel() {
		create();
		organize();
		updateTeam(null);
	}

	private void create() {
		teamNameLabel = new JLabel();
		payrollLabel = new JLabel();
		stadiumNameLabel = new JLabel();
		capacityLabel = new JLabel();
		openRosterButton = new JButton("Voir l'effectif complet");
		teamLogoPanel = new TeamLogoPanel("", 56);

		teamNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		teamNameLabel.setForeground(new Color(0x17, 0x31, 0x74));
		openRosterButton.setFocusPainted(false);
	}

	private void organize() {
		setLayout(new BorderLayout(0, 12));
		setOpaque(false);

		JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
		headerPanel.setOpaque(false);
		headerPanel.add(teamLogoPanel, BorderLayout.WEST);
		headerPanel.add(teamNameLabel, BorderLayout.CENTER);

		JPanel infoPanel = new JPanel();
		infoPanel.setOpaque(false);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(buildInfoLabel("Budget annuel", payrollLabel));
		infoPanel.add(Box.createVerticalStrut(8));
		infoPanel.add(buildInfoLabel("Arène", stadiumNameLabel));
		infoPanel.add(Box.createVerticalStrut(8));
		infoPanel.add(buildInfoLabel("Capacité de l'arène", capacityLabel));

		add(headerPanel, BorderLayout.NORTH);
		add(infoPanel, BorderLayout.CENTER);
		add(openRosterButton, BorderLayout.SOUTH);
	}

	private JPanel buildInfoLabel(String title, JLabel valueLabel) {
		JPanel row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		titleLabel.setForeground(new Color(110, 117, 131));
		valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		valueLabel.setForeground(new Color(0x17, 0x31, 0x74));

		row.add(titleLabel);
		row.add(Box.createVerticalStrut(2));
		row.add(valueLabel);
		row.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		return row;
	}

	public void updateTeam(Team team) {
		if (team == null) {
			teamLogoPanel.setTeamName("");
			teamNameLabel.setText("Aucune équipe");
			payrollLabel.setText("-");
			stadiumNameLabel.setText("-");
			capacityLabel.setText("-");
			openRosterButton.setEnabled(false);
			return;
		}

		teamLogoPanel.setTeamName(team.getName());
		teamNameLabel.setText(team.getName());
		payrollLabel.setText(PlayerDisplayUtil.formatSalary(team.getTeamFinance().getBudget().getInitialAmount()));
		stadiumNameLabel.setText(team.getStadium().getName());
		capacityLabel.setText(String.valueOf(team.getStadium().getCapacity()));
		openRosterButton.setEnabled(true);
	}

	public JButton getOpenRosterButton() {
		return openRosterButton;
	}
}
