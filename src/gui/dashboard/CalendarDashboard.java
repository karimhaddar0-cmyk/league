package gui.dashboard;
import config.CalendarConfiguration;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import data.calendar.GameDay;
import gui.panel.calendarPanel.HeaderPanel;
import gui.panel.calendarPanel.MonthViewPanel;
import gui.panel.calendarPanel.WeekViewPanel;
import process.manager.SimulationManager;

public class CalendarDashboard extends JPanel {

	private static final int DASHBOARD_SPACING = 16;
	private static final String MONTH_VIEW = "MONTH_VIEW";
	private static final String WEEK_VIEW = "WEEK_VIEW";
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);
	private final SimulationManager simulationManager;
	private HeaderPanel headerPanel;
	private WeekViewPanel weekViewPanel;
	private MonthViewPanel monthViewPanel;
	private JPanel contentPanel;
	private CardLayout contentLayout;
	private YearMonth displayedMonth;
	private boolean monthViewSelected;
	private LocalDate currentCalendarDate;
	private MatchDashboard matchDashboard;
	private Runnable showMatchDashboardAction;

	public CalendarDashboard(SimulationManager simulationManager, MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
		this.simulationManager = simulationManager;
		this.matchDashboard = matchDashboard;
		this.showMatchDashboardAction = showMatchDashboardAction;
		create();
		organize();
		actions();
		updateDashboardState();
	}

	private void create() {
		headerPanel = new HeaderPanel();
		weekViewPanel = new WeekViewPanel(simulationManager);
		OpenMatchDayAction openMatchDayAction = new OpenMatchDayAction(matchDashboard, showMatchDashboardAction);
		weekViewPanel.setOpenMatchDayAction(openMatchDayAction);
		monthViewPanel = new MonthViewPanel();
		contentLayout = new CardLayout();
		contentPanel = new JPanel(contentLayout);
		contentPanel.setOpaque(false);
		contentPanel.add(monthViewPanel, MONTH_VIEW);
		contentPanel.add(weekViewPanel, WEEK_VIEW);
		currentCalendarDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE;
		displayedMonth = buildDisplayedMonth(currentCalendarDate);
		monthViewSelected = true;
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(headerPanel, BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		JPanel content = new JPanel(new BorderLayout(DASHBOARD_SPACING, DASHBOARD_SPACING));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(0, DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING));
		return content;
	}

	public void startSeason() {
		simulationManager.randomFinance();
		simulationManager.getLeagueManager().startSeason();
		weekViewPanel.loadSeasonState();
		currentCalendarDate = weekViewPanel.getCurrentDate();
		updateDisplayedMonth(currentCalendarDate);
		updateDashboardState();
	}

	public void refreshSeasonState() {
		if (weekViewPanel.getCurrentDate() == null) {
			weekViewPanel.loadSeasonState();
		}
		if (weekViewPanel.getCurrentDate() != null) {
			currentCalendarDate = weekViewPanel.getCurrentDate();
		}
		updateDisplayedMonth(currentCalendarDate);
		updateDashboardState();
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new BorderLayout(0, 0));
		body.setOpaque(false);
		body.add(contentPanel, BorderLayout.CENTER);
		return body;
	}

	private void updateDashboardState() {
		LocalDate currentDate = currentCalendarDate;
		checkDisplayedMonth();
		updateHeaderState();
		updateCalendarPanels(currentDate);
		updateCurrentCard();
	}

	private void updateHeaderState() {
		headerPanel.setMonthText(MonthViewPanel.buildMonthText(displayedMonth));
		headerPanel.setMonthViewSelected(monthViewSelected);
		updateProgress();
	}

	private void updateCalendarPanels(LocalDate currentDate) {
		updateMonthView(currentDate);
	}

	private void updateProgress() {
		if (simulationManager.getLeague() == null || simulationManager.getLeague().getReagularSeason() == null
				|| simulationManager.getLeague().getReagularSeason().getCalendar() == null) {
			headerPanel.setProgress(0, 0);
			return;
		}

		HashMap<LocalDate, GameDay> seasonCalendar =
				new HashMap<LocalDate, GameDay>(simulationManager.getLeague().getReagularSeason().getCalendar().getCalendar());
		int totalGameDays = seasonCalendar.size();
		int displayedGameDays = 0;
		for (GameDay gameDay : seasonCalendar.values()) {
			if (gameDay.isDisplayed()) {
				displayedGameDays++;
			}
		}
		headerPanel.setProgress(displayedGameDays, totalGameDays);
	}

	private void updateMonthView(LocalDate currentDate) {
		if (simulationManager.getLeague() == null || simulationManager.getLeague().getReagularSeason() == null
				|| simulationManager.getLeague().getReagularSeason().getCalendar() == null) {
			monthViewPanel.showMonth(displayedMonth, currentDate, null);
			return;
		}

		HashMap<LocalDate, GameDay> seasonCalendar =
				new HashMap<LocalDate, GameDay>(simulationManager.getLeague().getReagularSeason().getCalendar().getCalendar());
		monthViewPanel.showMonth(displayedMonth, currentDate, seasonCalendar);
	}

	private void updateCurrentCard() {
		if (monthViewSelected) {
			contentLayout.show(contentPanel, MONTH_VIEW);
		} else {
			contentLayout.show(contentPanel, WEEK_VIEW);
		}
	}

	private void actions() {
		headerPanel.setSimulateDayAction(new SimulateDayAction());
		headerPanel.setSimulateWeekAction(new SimulateWeekAction());
		headerPanel.setSimulateSeasonAction(new SimulateSeasonAction());
		headerPanel.setPreviousMonthAction(new PreviousMonthAction());
		headerPanel.setNextMonthAction(new NextMonthAction());
		headerPanel.setMonthToggleAction(new ShowMonthViewAction());
		headerPanel.setWeekToggleAction(new ShowWeekViewAction());
		monthViewPanel.setMatchDashboard(matchDashboard, showMatchDashboardAction);
	}

	private class OpenMatchDayAction extends WeekViewPanel.OpenMatchDayAction {
		private MatchDashboard matchDashboard;
		private Runnable showMatchDashboardAction;

		private OpenMatchDayAction(MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
			this.matchDashboard = matchDashboard;
			this.showMatchDashboardAction = showMatchDashboardAction;
		}

		@Override
		public void open(GameDay gameDay, LocalDate date) {
			matchDashboard.showGameDay(gameDay, date);
			showMatchDashboardAction.run();
		}
	}

	private class SimulateDayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			weekViewPanel.advanceDay();
			currentCalendarDate = weekViewPanel.getCurrentDate();
			updateDisplayedMonth(currentCalendarDate);
			updateDashboardState();
		}
	}

	private class SimulateWeekAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			weekViewPanel.advanceWeek();
			if (weekViewPanel.getCurrentDate() != null) {
				currentCalendarDate = weekViewPanel.getCurrentDate();
			}
			updateDisplayedMonth(currentCalendarDate);
			updateDashboardState();
		}
	}

	private class SimulateSeasonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			weekViewPanel.advanceSeason();
			currentCalendarDate = weekViewPanel.getSimulationDate();
			updateDisplayedMonth(currentCalendarDate);
			updateDashboardState();
		}
	}

	private class PreviousMonthAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isSeasonStartMonth(displayedMonth)) {
				displayedMonth = displayedMonth.minusMonths(1);
			}
			updateDashboardState();
		}
	}

	private class NextMonthAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isSeasonEndMonth(displayedMonth)) {
				displayedMonth = displayedMonth.plusMonths(1);
			}
			updateDashboardState();
		}
	}

	private class ShowMonthViewAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			monthViewSelected = true;
			updateDashboardState();
		}
	}

	private class ShowWeekViewAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			monthViewSelected = false;
			updateDashboardState();
		}
	}

	private void checkDisplayedMonth() {
		if (isBeforeSeasonStartMonth(displayedMonth)) {
			displayedMonth = buildDisplayedMonth(CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE);
		}
		if (isAfterSeasonEndMonth(displayedMonth)) {
			displayedMonth = buildDisplayedMonth(CalendarConfiguration.REGULAR_SEASON_END_DATE);
		}
	}

	private void updateDisplayedMonth(LocalDate date) {
		displayedMonth = buildDisplayedMonth(date);
	}

	private YearMonth buildDisplayedMonth(LocalDate date) {
		YearMonth month = YearMonth.now();
		month = month.withYear(date.getYear());
		month = month.withMonth(date.getMonthValue());
		return month;
	}

	private boolean isSeasonStartMonth(YearMonth month) {
		return month.getYear() == CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getYear()
				&& month.getMonthValue() == CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonthValue();
	}

	private boolean isSeasonEndMonth(YearMonth month) {
		return month.getYear() == CalendarConfiguration.REGULAR_SEASON_END_DATE.getYear()
				&& month.getMonthValue() == CalendarConfiguration.REGULAR_SEASON_END_DATE.getMonthValue();
	}

	private boolean isBeforeSeasonStartMonth(YearMonth month) {
		if (month.getYear() < CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getYear()) {
			return true;
		}
		if (month.getYear() > CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getYear()) {
			return false;
		}
		return month.getMonthValue() < CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE.getMonthValue();
	}

	private boolean isAfterSeasonEndMonth(YearMonth month) {
		if (month.getYear() > CalendarConfiguration.REGULAR_SEASON_END_DATE.getYear()) {
			return true;
		}
		if (month.getYear() < CalendarConfiguration.REGULAR_SEASON_END_DATE.getYear()) {
			return false;
		}
		return month.getMonthValue() > CalendarConfiguration.REGULAR_SEASON_END_DATE.getMonthValue();
	}
}
