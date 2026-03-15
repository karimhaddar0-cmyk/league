package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import config.SimulationConfiguration;
import gui.panel.calendarPanel.CalendarQuickActionsPanel;
import gui.panel.calendarPanel.CalendarSimulationPanel;
import gui.panel.calendarPanel.SeasonProgressBarPanel;
import gui.panel.common.BuildBox;
import gui.panel.common.SectionTitle;
import process.manager.SimulationManager;

public class CalendarDashboard extends JPanel {

	private static final int IDEAL_DASHBOARD_SPACING = 16;
	private static final int IDEAL_DASHBOARD_HEADER_HEIGHT = 50;
	private static final int IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH = 270;
	private static final int IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH = 340;
	private static final Color IDEAL_DASHBOARD_BACKGROUND_COLOR = new Color(247, 248, 250);
	private static final Color SECONDARY_TEXT_COLOR = new Color(0x6D, 0x75, 0x83);
	private final SimulationManager simulationManager;
	private CalendarSimulationPanel calendarSimulationPanel;
	private SeasonProgressBarPanel seasonProgressBarPanel;


	public CalendarDashboard(SimulationManager simulationManager, MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
		this.simulationManager = simulationManager;
		create(matchDashboard, showMatchDashboardAction);
		organize();
	}

	private void create(MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
		calendarSimulationPanel = new CalendarSimulationPanel(simulationManager);
		calendarSimulationPanel.setMatchDaySelectionListener(new OpenMatchDayListener(matchDashboard, showMatchDashboardAction));
		seasonProgressBarPanel = new SeasonProgressBarPanel(
				SimulationConfiguration.REGULAR_SEASON_DEBUT_DATE,
				SimulationConfiguration.REGULAR_SEASON_END_DATE,
				SimulationConfiguration.REGULAR_SEASON_DEBUT_DATE);
		calendarSimulationPanel.setSeasonProgressBarPanel(seasonProgressBarPanel);
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
		JPanel content = new JPanel(new BorderLayout(IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(0, IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING));
		return content;
	}

	public void startSeason() {
		simulationManager.randomFinance();
		simulationManager.startSeason();
		calendarSimulationPanel.loadSeasonState();
	}

	public void refreshSeasonState() {
		calendarSimulationPanel.loadSeasonState();
	}

	

	private JPanel buildHeader() {
		JPanel header = new SectionTitle("CALENDRIER DE LA SAISON", "Saison régulière");
		header.setPreferredSize(new Dimension(IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH, IDEAL_DASHBOARD_HEADER_HEIGHT));
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
		JPanel centerColumn = new JPanel(new BorderLayout(0, 12));
		centerColumn.setOpaque(false);

		JPanel progressCard = new BuildBox("PROGRESSION DE LA SAISON", "", buildSeasonProgressPanel());
		progressCard.setPreferredSize(new Dimension(10, 110));

		JPanel matchDaysCard = new BuildBox("JOURS DE MATCH", "", buildMatchDaysPanel());

		centerColumn.add(progressCard, BorderLayout.NORTH);
		centerColumn.add(matchDaysCard, BorderLayout.CENTER);
		return centerColumn;
	}

	private JPanel buildSeasonProgressPanel() {
		return seasonProgressBarPanel;
	}

	private JPanel buildRightColumn() {
		JPanel column = new JPanel(new GridLayout(2, 1, 0, 12));
		column.setOpaque(false);
		column.setPreferredSize(new Dimension(IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH, 10));

		JPanel actionsCard = new BuildBox("ACTIONS RAPIDES", "", new CalendarQuickActionsPanel(
				new SimulateDayAction(),
				new SimulateWeekAction(),
				new SimulateSeasonAction()));
		JPanel infoCard = new BuildBox("INFORMATIONS SAISON", "", buildSeasonInfoPanel());

		column.add(actionsCard);
		column.add(infoCard);
		return column;
	}

	private JPanel buildMatchDaysPanel() {
		return calendarSimulationPanel;
	}

	private class OpenMatchDayListener implements CalendarSimulationPanel.MatchDaySelectionListener {
		private final MatchDashboard matchDashboard;
		private final Runnable showMatchDashboardAction;

		private OpenMatchDayListener(MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
			this.matchDashboard = matchDashboard;
			this.showMatchDashboardAction = showMatchDashboardAction;
		}

		@Override
		public void openMatchDay(data.calendar.GameDay gameDay, java.time.LocalDate date) {
			matchDashboard.showGameDay(gameDay, date);
			showMatchDashboardAction.run();
		}
	}

	private class SimulateDayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			calendarSimulationPanel.advanceDay();
		}
	}

	private class SimulateWeekAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			calendarSimulationPanel.advanceWeek();
		}
	}

	private class SimulateSeasonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			simulationManager.displayCurrentSeason();
			calendarSimulationPanel.loadSeasonState();
		}
	}

	private JPanel buildSeasonInfoPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

		JLabel gamesLabel = new JLabel("Matchs joués : 0 (placeholder)");
		JLabel teamsLabel = new JLabel("Équipes : 30 (placeholder)");

		gamesLabel.setForeground(SECONDARY_TEXT_COLOR);
		teamsLabel.setForeground(SECONDARY_TEXT_COLOR);

		panel.add(gamesLabel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(teamsLabel);

		return panel;
	}
}
