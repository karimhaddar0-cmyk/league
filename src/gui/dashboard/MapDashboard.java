package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import data.team.Team;
import gui.panel.common.BuildBox;
import gui.panel.common.DashboardPanelUtil;
import gui.panel.common.SectionTitle;
import gui.panel.common.TeamMapPanel;
import gui.panel.mapPanel.effectifPanel.MapTeamPlayersPanel;
import gui.panel.mapPanel.effectifPanel.MapTeamSummaryPanel;
import process.manager.SimulationManager;
import process.repositery.TeamRepositery;
import process.utilitary.TeamStatUtil;

/**
 * Dashboard dédié à la page Carte.
 */
public class MapDashboard extends JPanel {
	private static final int IDEAL_DASHBOARD_SPACING = 16;
	private static final int IDEAL_DASHBOARD_HEADER_HEIGHT = 50;
	private static final int IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH = 270;
	private static final int IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH = 340;
	private static final Color IDEAL_DASHBOARD_BACKGROUND_COLOR = new Color(247, 248, 250);

	private SimulationManager simulationManager;
	private ArrayList<Team> teams;
	private Team selectedTeam;
	private TeamMapPanel mapPanel;
	private MapTeamSummaryPanel teamSummaryPanel;
	private MapTeamPlayersPanel teamPlayersPanel;
	private Runnable openRosterAction;
	private TeamRepositery teamRepositery = TeamRepositery.getInstance();
	private boolean currentSeasonSelected = true;

	public MapDashboard(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
		create();
		organize();
		actions();
		selectDefaultTeam();
	}

	private void create() {
		teams = new ArrayList<Team>(teamRepositery.getAllTeams());
		mapPanel = new TeamMapPanel();
		teamSummaryPanel = new MapTeamSummaryPanel();
		teamPlayersPanel = new MapTeamPlayersPanel();
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(IDEAL_DASHBOARD_BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		return DashboardPanelUtil.createContentPanel(IDEAL_DASHBOARD_SPACING);
	}

	private JPanel buildHeader() {
		JPanel header = new SectionTitle("Carte des equipes", "Distribution geographique");
		header.setPreferredSize(new Dimension(IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH, IDEAL_DASHBOARD_HEADER_HEIGHT));
		return header;
	}

	private JPanel buildBody() {
		JPanel body = DashboardPanelUtil.createBodyPanel(IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildCenterColumn() {
		return new BuildBox("LOCALISATION DES FRANCHISES", "", mapPanel);
	}

	private JPanel buildRightColumn() {
		JPanel column = DashboardPanelUtil.createGridColumn(2, 1, 0, 12, IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH);

		column.add(
				new BuildBox("Détails de l'équipe", "Informations détaillées sur l'équipe sélectionnée", teamSummaryPanel));
		column.add(new BuildBox("Joueurs de l'équipe", "", teamPlayersPanel));

		return column;
	}

	private void actions() {
		teamSummaryPanel.getOpenRosterButton().addActionListener(new OpenRosterListener());
		mapPanel.setTeamSelectionAction(new MapSelectionAction());
	}

	private void selectDefaultTeam() {
		if (teams.isEmpty()) {
			setSelectedTeam(null);
			return;
		}
		setSelectedTeam(teams.get(0));
	}

	public void setSelectedTeam(Team selectedTeam) {
		this.selectedTeam = selectedTeam;
		teamSummaryPanel.updateTeam(selectedTeam, currentSeasonSelected);
		teamPlayersPanel.updateTeam(selectedTeam, currentSeasonSelected);
		if (selectedTeam == null) {
			mapPanel.setSelectedTeamName(null);
		} else {
			mapPanel.setSelectedTeamName(selectedTeam.getName());
		}
	}

	public Team getSelectedTeam() {
		return selectedTeam;
	}

	public void setOpenRosterAction(Runnable openRosterAction) {
		this.openRosterAction = openRosterAction;
	}

	public void refreshSelectedTeam() {
		setSelectedTeam(selectedTeam);
	}

	private class OpenRosterListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (openRosterAction != null) {
				openRosterAction.run();
			}
		}
	}

	private class MapSelectionAction implements Runnable {
		@Override
		public void run() {
			setSelectedTeam(TeamStatUtil.findTeamByName(mapPanel.getSelectedTeamName()));
		}
	}
}
