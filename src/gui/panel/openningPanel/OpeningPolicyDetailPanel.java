package gui.panel.openningPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.league.League;
import data.team.Team;
import data.team.finance.financialpolicy.AmbitiousPolicy;
import data.team.finance.financialpolicy.BalancedPolicy;
import data.team.finance.financialpolicy.FinancialPolicy;
import data.team.finance.marketsize.LargeSize;
import data.team.finance.marketsize.MediumSize;
import data.team.finance.marketsize.MarketSize;
import data.team.finance.marketsize.SmallSize;
import gui.panel.common.PlayerDisplayUtil;
import gui.panel.common.TeamDisplayUtil;
import process.utilitary.TeamStatUtil;

public class OpeningPolicyDetailPanel extends JPanel {

	private JLabel policyLabel;
	private JLabel teamValueLabel;
	private JLabel cityValueLabel;
	private JLabel conferenceValueLabel;
	private JLabel divisionValueLabel;
	private JLabel marketSizeValueLabel;
	private JLabel budgetValueLabel;
	private JLabel capacityValueLabel;
	private JLabel noteValueLabel;

	public OpeningPolicyDetailPanel() {
		setLayout(new BorderLayout(0, 14));
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

		policyLabel = new JLabel("-", JLabel.CENTER);
		policyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		policyLabel.setForeground(new Color(90, 90, 90));

		JPanel policyPanel = new JPanel(new BorderLayout());
		policyPanel.setOpaque(false);
		policyPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(210, 214, 220)),
			BorderFactory.createEmptyBorder(12, 16, 12, 16)
		));
		policyPanel.add(policyLabel, BorderLayout.CENTER);

		JPanel infoPanel = new JPanel(new GridLayout(8, 1, 0, 0));
		infoPanel.setOpaque(false);

		teamValueLabel = createValueLabel();
		cityValueLabel = createValueLabel();
		conferenceValueLabel = createValueLabel();
		divisionValueLabel = createValueLabel();
		marketSizeValueLabel = createValueLabel();
		budgetValueLabel = createValueLabel();
		capacityValueLabel = createValueLabel();
		noteValueLabel = createValueLabel();

		infoPanel.add(createRow("Equipe", teamValueLabel));
		infoPanel.add(createRow("Ville", cityValueLabel));
		infoPanel.add(createRow("Conference", conferenceValueLabel));
		infoPanel.add(createRow("Division", divisionValueLabel));
		infoPanel.add(createRow("Market size", marketSizeValueLabel));
		infoPanel.add(createRow("Budget annuel", budgetValueLabel));
		infoPanel.add(createRow("Capacite Stade", capacityValueLabel));
		infoPanel.add(createRow("Note globale", noteValueLabel));

		add(policyPanel, BorderLayout.NORTH);
		add(infoPanel, BorderLayout.CENTER);

		updateTeam(null, null);
	}

	private JLabel createValueLabel() {
		JLabel valueLabel = new JLabel("-");
		valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		valueLabel.setForeground(new Color(0x17, 0x31, 0x74));
		return valueLabel;
	}

	private JPanel createRow(String title, JLabel valueLabel) {
		JPanel row = new JPanel(new GridLayout(2, 1, 0, 2));
		row.setOpaque(false);
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 238, 242)));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		titleLabel.setForeground(new Color(110, 117, 131));

		row.add(titleLabel);
		row.add(valueLabel);
		return row;
	}

	public void updateTeam(Team team, League league) {
		if (team == null || league == null) {
			showEmptyState();
			return;
		}
		showTeamState(team, league);
	}

	private void showEmptyState() {
		policyLabel.setText("-");
		teamValueLabel.setText("-");
		cityValueLabel.setText("-");
		conferenceValueLabel.setText("-");
		divisionValueLabel.setText("-");
		marketSizeValueLabel.setText("-");
		budgetValueLabel.setText("-");
		capacityValueLabel.setText("-");
		noteValueLabel.setText("-");
	}

	private void showTeamState(Team team, League league) {
		policyLabel.setText(getPolicyName(team.getTeamFinance().getFinancialProfil()));
		teamValueLabel.setText(TeamDisplayUtil.getShortName(team));
		cityValueLabel.setText(TeamDisplayUtil.getCityName(team));
		conferenceValueLabel.setText(TeamDisplayUtil.getConferenceLabel(TeamStatUtil.getConferenceName(team, league)));
		divisionValueLabel.setText(TeamStatUtil.getDivisionName(team, league));
		marketSizeValueLabel.setText(getMarketSizeName(team.getTeamFinance().getMarketSize()));
		budgetValueLabel.setText(PlayerDisplayUtil.formatSalary(team.getTeamFinance().getBudget().getRemainingAmount()));
		capacityValueLabel.setText(team.getStadium().getCapacity() + " places");

		int note = (int) Math.round(TeamStatUtil.getAverageNote(team));
		noteValueLabel.setText(note + "/100");
	}

	private String getPolicyName(FinancialPolicy policy) {
		if (policy instanceof AmbitiousPolicy) {
			return "Ambitieux";
		}
		if (policy instanceof BalancedPolicy) {
			return "Equilibre";
		}
		return "Economique";
	}

	private String getMarketSizeName(MarketSize marketSize) {
		if (marketSize instanceof LargeSize) {
			return "Grand";
		}
		if (marketSize instanceof MediumSize) {
			return "Moyen";
		}
		if (marketSize instanceof SmallSize) {
			return "Petit";
		}
		return "-";
	}

}
