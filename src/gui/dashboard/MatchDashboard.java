package gui.dashboard;
import config.CalendarConfiguration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import data.calendar.GameDay;
import data.finance.GameStat;
import data.sport.setup.Game;
import gui.panel.common.BuildBox;
import gui.panel.common.DashboardPanelUtil;
import gui.panel.matchPanel.MatchDayListPanel;
import gui.panel.matchPanel.MatchDayListPanel.MatchSelectionListener;
import gui.panel.matchPanel.MatchDetailPanel;
import gui.panel.matchPanel.MatchFinancePanel;
import gui.panel.matchPanel.MatchHeaderPanel;
import process.manager.LeagueManager;

public class MatchDashboard extends JPanel {
	private static final int DASHBOARD_SPACING = 16;
	private static final int LEFT_COLUMN_WIDTH = 270;
	private static final int RIGHT_COLUMN_WIDTH = 300;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	private LeagueManager leagueManager;
	private LocalDate selectedDate;
	private Game selectedGame;
	private GameDay selectedGameDay;

	private MatchHeaderPanel headerPanel;
	private MatchDayListPanel matchDayListPanel;
	private MatchDetailPanel matchDetailPanel;
	private MatchFinancePanel matchFinancePanel;

	private Runnable openLiveMatchAction;

	public MatchDashboard() {
		this(null);
	}

	public MatchDashboard(LeagueManager leagueManager) {
		this.leagueManager = leagueManager == null ? new LeagueManager() : leagueManager;
		selectedDate = CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE;
		create();
		organize();
		actions();
		loadGamesOfDay(selectedDate);
	}

	private void create() {
		headerPanel = new MatchHeaderPanel();
		matchDayListPanel = new MatchDayListPanel();
		matchDetailPanel = new MatchDetailPanel();
		matchFinancePanel = new MatchFinancePanel();
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		return DashboardPanelUtil.createContentPanel(DASHBOARD_SPACING);
	}

	private JPanel buildHeader() {
		return headerPanel;
	}

	private JPanel buildBody() {
		JPanel body = DashboardPanelUtil.createBodyPanel(DASHBOARD_SPACING, 0);
		body.add(buildLeftColumn(), BorderLayout.WEST);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildLeftColumn() {
		JPanel leftColumn = new BuildBox("MATCHS DU JOUR", "Rencontres de la journée", matchDayListPanel);
		leftColumn.setPreferredSize(new Dimension(LEFT_COLUMN_WIDTH, 10));
		return leftColumn;
	}

	private JPanel buildCenterColumn() {
		return new BuildBox("MATCH SÉLECTIONNÉ", "Score et statistiques", matchDetailPanel);
	}

	private JPanel buildRightColumn() {
		JPanel rightColumn = new BuildBox("FINANCES DU MATCH", "Revenus et dépenses", matchFinancePanel);
		rightColumn.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 10));
		return rightColumn;
	}

	private void actions() {
		matchDayListPanel.setMatchSelectionListener(new DashboardMatchSelectionListener());
	}

	public LeagueManager getLeagueManager() {
		return leagueManager;
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public Game getSelectedGame() {
		return selectedGame;
	}

	public void setOpenLiveMatchAction(Runnable openLiveMatchAction) {
		this.openLiveMatchAction = openLiveMatchAction;
	}

	public void loadGamesOfDay(LocalDate date) {
		selectedDate = date;
		GameDay gameDay = leagueManager.getLeague().getReagularSeason().getCalendar().getCalendar().get(date);
		showGameDay(gameDay, date);
	}

	public void showGameDay(GameDay gameDay, LocalDate date) {
		selectedDate = date;
		selectedGameDay = gameDay;
		headerPanel.updateDate(date);
		matchDayListPanel.showGameDay(gameDay);

		if (gameDay == null || gameDay.getGames().isEmpty()) {
			resetSelectedGame();
			return;
		}

		updateSelectedGame(gameDay.getGames().get(0));
		repaint();
	}

	public void refreshSelectedGame() {
		if (selectedGameDay != null) {
			matchDayListPanel.showGameDay(selectedGameDay);
		}
		if (selectedGame != null) {
			updateSelectedGame(selectedGame);
		}
	}

	private void updateSelectedGame(Game game) {
		selectedGame = game;
		if (selectedGame == null || !selectedGame.isDisplayed()) {
			matchDetailPanel.showHiddenState(game, buildDayNumberText());
			matchFinancePanel.showHiddenState();
			return;
		}

		GameStat gameStat = leagueManager.getFinanceManager().getGameStat(game);
		matchDetailPanel.showGame(game, buildDayNumberText(), gameStat);
		matchFinancePanel.showGameFinance(game, gameStat);
	}

	private void resetSelectedGame() {
		selectedGameDay = null;
		selectedGame = null;
		matchDetailPanel.showEmptyState();
		matchFinancePanel.showHiddenState();
	}

	private String buildDayNumberText() {
		if (selectedDate == null) {
			return "Jour -";
		}
		long dayNumber = ChronoUnit.DAYS.between(CalendarConfiguration.REGULAR_SEASON_DEBUT_DATE, selectedDate) + 1;
		return "Jour " + dayNumber;
	}

	private class DashboardMatchSelectionListener implements MatchSelectionListener {
		@Override
		public void onMatchSelected(Game game) {
			updateSelectedGame(game);
		}

		@Override
		public void onMatchDetail(Game game) {
			updateSelectedGame(game);
			if (openLiveMatchAction != null) {
				openLiveMatchAction.run();
			}
		}
	}
}
