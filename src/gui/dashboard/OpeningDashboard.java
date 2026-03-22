package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.team.Team;
import data.team.finance.financialpolicy.AmbitiousPolicy;
import data.team.finance.financialpolicy.BalancedPolicy;
import data.team.finance.financialpolicy.FinancialPolicy;
import data.team.finance.financialpolicy.ThriftyPolicy;
import gui.panel.common.ButtonStyleUtil;
import gui.panel.common.BuildBox;
import gui.panel.common.DashboardPanelUtil;
import gui.panel.common.SectionTitle;
import gui.panel.common.TeamMapPanel;
import gui.panel.openningPanel.OpeningPolicyDetailPanel;
import gui.panel.openningPanel.OpeningTeamSelectionPanel;
import process.manager.LeagueManager;
import process.utilitary.TeamStatUtil;

public class OpeningDashboard extends JPanel {

	private static final int IDEAL_DASHBOARD_SPACING = 16;
	private static final int IDEAL_DASHBOARD_HEADER_HEIGHT = 50;
	private static final int IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH = 420;
	private static final int IDEAL_DASHBOARD_TOP_CARD_HEIGHT = 220;
	private static final Color IDEAL_DASHBOARD_BACKGROUND_COLOR = new Color(247, 248, 250);

	private LeagueManager leagueManager;
	private ArrayList<Team> teams;
	private Team selectedTeam;
	private JButton continueButton;
	private JButton randomPoliciesButton;
	private TeamMapPanel openingMapPanel;
	private OpeningTeamSelectionPanel teamSelectionPanel;
	private OpeningPolicyDetailPanel policyDetailPanel;

	public OpeningDashboard() {
		this(new LeagueManager());
	}

	public OpeningDashboard(LeagueManager leagueManager) {
		this.leagueManager = leagueManager;
		this.leagueManager.prepareOpeningData();
		create();
		organize();
		actions();
		selectDefaultTeam();
	}

	private void create() {
		teams = new ArrayList<Team>(leagueManager.getLeague().getAllTeam());
		continueButton = new JButton("Continuer");
		randomPoliciesButton = new JButton();
		openingMapPanel = new TeamMapPanel();
		teamSelectionPanel = new OpeningTeamSelectionPanel();
		policyDetailPanel = new OpeningPolicyDetailPanel();

		randomPoliciesButton.setFocusPainted(false);
		ButtonStyleUtil.styleToggleButton(randomPoliciesButton);
		ButtonStyleUtil.setToggleButtonSelected(randomPoliciesButton, true);
		randomPoliciesButton.setIcon(createRandomIcon());
		randomPoliciesButton.setText("");
		randomPoliciesButton.setPreferredSize(new Dimension(44, 44));
	}

	private ImageIcon createRandomIcon() {
		ImageIcon icon = new ImageIcon("src/test/randomIcon.png");
		Image scaledImage = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(IDEAL_DASHBOARD_BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		content.add(buildFooter(), BorderLayout.SOUTH);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		return DashboardPanelUtil.createContentPanel(IDEAL_DASHBOARD_SPACING);
	}

	private JPanel buildHeader() {
		JPanel header = new SectionTitle(
			"Creation de la ligue",
			"Definissez les politiques financieres des equipes"
		);
		header.setPreferredSize(new Dimension(360, IDEAL_DASHBOARD_HEADER_HEIGHT));
		return header;
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new BorderLayout(IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING));
		body.setOpaque(false);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildCenterColumn() {
		JPanel mapContent = new JPanel(new BorderLayout(0, 8));
		mapContent.setOpaque(false);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		topPanel.setOpaque(false);
		topPanel.setBorder(BorderFactory.createEmptyBorder(-8, 0, 0, 0));
		topPanel.add(randomPoliciesButton);

		mapContent.add(topPanel, BorderLayout.NORTH);
		mapContent.add(openingMapPanel, BorderLayout.CENTER);

		return new BuildBox(
			"LOCALISATION DES FRANCHISES",
			"Cliquez sur une ville",
			mapContent
		);
	}

	private JPanel buildRightColumn() {
		JPanel column = DashboardPanelUtil.createRightColumn(IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH, 12);

		JPanel topCard = new BuildBox(
			"EQUIPE SELECTIONNEE",
			"Equipe courante",
			teamSelectionPanel
		);
		topCard.setPreferredSize(new Dimension(IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH, IDEAL_DASHBOARD_TOP_CARD_HEIGHT));

		JPanel bottomCard = new BuildBox(
			"POLITIQUE FINANCIERE",
			"Informations generales",
			policyDetailPanel
		);

		column.add(topCard, BorderLayout.NORTH);
		column.add(bottomCard, BorderLayout.CENTER);

		return column;
	}

	private JPanel buildFooter() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		footer.setOpaque(false);

		footer.add(continueButton);

		return footer;
	}

	private void actions() {
		openingMapPanel.setTeamSelectionAction(new MapSelectionAction());
		randomPoliciesButton.addActionListener(new RandomPoliciesListener());
		teamSelectionPanel.getAmbitiousButton().addActionListener(new AmbitiousPolicyListener());
		teamSelectionPanel.getBalancedButton().addActionListener(new BalancedPolicyListener());
		teamSelectionPanel.getThriftyButton().addActionListener(new ThriftyPolicyListener());
	}

	private void selectDefaultTeam() {
		if (teams.isEmpty()) {
			setSelectedTeam(null);
			return;
		}
		setSelectedTeam(teams.get(0));
	}

	private void setSelectedTeam(Team selectedTeam) {
		this.selectedTeam = selectedTeam;
		refreshSelectedTeamPanels();
	}

	private void refreshSelectedTeamPanels() {
		teamSelectionPanel.updateTeam(selectedTeam);
		if (selectedTeam != null) {
			teamSelectionPanel.setSelectedPolicy(selectedTeam.getTeamFinance().getFinancialProfil());
		}
		policyDetailPanel.updateTeam(selectedTeam, leagueManager.getLeague());
		if (selectedTeam == null) {
			openingMapPanel.setSelectedTeamName(null);
			return;
		}
		openingMapPanel.setSelectedTeamName(selectedTeam.getName());
	}

	private void applyPolicy(FinancialPolicy policy) {
		if (selectedTeam == null) {
			return;
		}
		leagueManager.chooseFinancialPolicy(selectedTeam, policy);
		leagueManager.prepareOpeningData();
		refreshSelectedTeamPanels();
	}

	private class MapSelectionAction implements Runnable {
		@Override
		public void run() {
			setSelectedTeam(TeamStatUtil.findTeamByName(openingMapPanel.getSelectedTeamName()));
		}
	}

	private class AmbitiousPolicyListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			applyPolicy(new AmbitiousPolicy());
		}
	}

	private class BalancedPolicyListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			applyPolicy(new BalancedPolicy());
		}
	}

	private class ThriftyPolicyListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			applyPolicy(new ThriftyPolicy());
		}
	}

	private class RandomPoliciesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			leagueManager.randomFinancialPolicy();
			leagueManager.prepareOpeningData();
			refreshSelectedTeamPanels();
		}
	}

	public JButton getContinueButton() {
		return continueButton;
	}

	public boolean hasSelectedProfil() {
		return selectedTeam != null;
	}

	public void showSelectionWarning() {
		JOptionPane.showMessageDialog(
			this,
			"Selectionnez une equipe sur la carte avant de continuer.",
			"Selection requise",
			JOptionPane.WARNING_MESSAGE
		);
	}
}
