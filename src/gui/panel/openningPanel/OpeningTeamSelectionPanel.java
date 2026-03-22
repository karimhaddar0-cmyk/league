package gui.panel.openningPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.team.Team;
import data.team.finance.financialpolicy.AmbitiousPolicy;
import data.team.finance.financialpolicy.BalancedPolicy;
import data.team.finance.financialpolicy.FinancialPolicy;
import data.team.finance.financialpolicy.ThriftyPolicy;
import gui.panel.common.ButtonStyleUtil;
import gui.panel.common.TeamDisplayUtil;
import gui.panel.mapPanel.effectifPanel.teamPanel.TeamLogoPanel;

public class OpeningTeamSelectionPanel extends JPanel {

	private static final Color HEADER_BACKGROUND = new Color(0x17, 0x31, 0x74);
	private static final Color TITLE_COLOR = new Color(110, 117, 131);

	private TeamLogoPanel logoPanel;
	private JLabel cityLabel;
	private JLabel teamLabel;
	private JButton ambitiousButton;
	private JButton balancedButton;
	private JButton thriftyButton;
	private FinancialPolicy selectedPolicy;

	public OpeningTeamSelectionPanel() {
		create();
		organize();
		updateTeam(null);
	}

	private void create() {
		logoPanel = new TeamLogoPanel("", 48);
		cityLabel = new JLabel("-");
		teamLabel = new JLabel("-");
		ambitiousButton = new JButton("Ambitieux");
		balancedButton = new JButton("Equilibre");
		thriftyButton = new JButton("Economique");

		cityLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		cityLabel.setForeground(Color.WHITE);
		teamLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		teamLabel.setForeground(Color.WHITE);

		ButtonStyleUtil.styleToggleButton(ambitiousButton);
		ButtonStyleUtil.styleToggleButton(balancedButton);
		ButtonStyleUtil.styleToggleButton(thriftyButton);
	}

	private void organize() {
		setLayout(new BorderLayout(0, 12));
		setOpaque(false);

		JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
		headerPanel.setOpaque(true);
		headerPanel.setBackground(HEADER_BACKGROUND);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		headerPanel.setLayout(new BorderLayout(12, 0));
		headerPanel.add(logoPanel, BorderLayout.WEST);

		JPanel namePanel = new JPanel(new GridLayout(2, 1, 0, 2));
		namePanel.setOpaque(false);
		namePanel.add(cityLabel);
		namePanel.add(teamLabel);
		headerPanel.add(namePanel, BorderLayout.CENTER);

		JPanel policyPanel = new JPanel(new BorderLayout(0, 8));
		policyPanel.setOpaque(false);

		JLabel titleLabel = new JLabel("POLITIQUE FINANCIERE");
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		titleLabel.setForeground(TITLE_COLOR);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 8, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.add(ambitiousButton);
		buttonPanel.add(balancedButton);
		buttonPanel.add(thriftyButton);

		policyPanel.add(titleLabel, BorderLayout.NORTH);
		policyPanel.add(buttonPanel, BorderLayout.CENTER);

		add(headerPanel, BorderLayout.NORTH);
		add(policyPanel, BorderLayout.CENTER);
	}

	public void updateTeam(Team team) {
		if (team == null) {
			showEmptyState();
			return;
		}
		showTeamState(team);
	}

	private void showEmptyState() {
		logoPanel.setTeamName("");
		cityLabel.setText("Aucune");
		teamLabel.setText("selection");
		setButtonsEnabled(false);
		selectedPolicy = null;
		refreshPolicyButtons();
	}

	private void showTeamState(Team team) {
		logoPanel.setTeamName(team.getName());
		cityLabel.setText(TeamDisplayUtil.getCityName(team));
		teamLabel.setText(TeamDisplayUtil.getShortName(team));
		setButtonsEnabled(true);
		selectedPolicy = team.getTeamFinance().getFinancialProfil();
		refreshPolicyButtons();
	}

	public void setSelectedPolicy(FinancialPolicy selectedPolicy) {
		this.selectedPolicy = selectedPolicy;
		refreshPolicyButtons();
	}

	private void setButtonsEnabled(boolean enabled) {
		ambitiousButton.setEnabled(enabled);
		balancedButton.setEnabled(enabled);
		thriftyButton.setEnabled(enabled);
	}

	private void refreshPolicyButtons() {
		ButtonStyleUtil.setToggleButtonSelected(ambitiousButton, selectedPolicy instanceof AmbitiousPolicy);
		ButtonStyleUtil.setToggleButtonSelected(balancedButton, selectedPolicy instanceof BalancedPolicy);
		ButtonStyleUtil.setToggleButtonSelected(thriftyButton, selectedPolicy instanceof ThriftyPolicy);
	}

	public JButton getAmbitiousButton() {
		return ambitiousButton;
	}

	public JButton getBalancedButton() {
		return balancedButton;
	}

	public JButton getThriftyButton() {
		return thriftyButton;
	}
}
