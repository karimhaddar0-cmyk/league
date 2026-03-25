package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import config.GameConfiguration;
import data.sport.play.action.ActionResult;
import data.sport.setup.Game;
import data.sport.setup.GameResult;
import gui.panel.common.BuildBox;
import gui.panel.matchPanel.liveMatchPanel.LiveActionsPanel;
import gui.panel.matchPanel.liveMatchPanel.LiveMatchHeaderPanel;
import gui.panel.matchPanel.liveMatchPanel.LiveTeamStatsPanel;
import process.manager.LiveMatchStatistics;
import process.manager.SimulationManager;
import process.visitor.actionresult.LiveActionTextVisitor;

public class LiveMatchDashboard extends JPanel implements Runnable {
	private static final int DASHBOARD_SPACING = 16;
	private static final int SIDE_COLUMN_WIDTH = 270;
	private static final int LIVE_ROWS = 10;
	private static final int CHRONO_SPEED = 200;
	private static final int GAME_SECONDS_PER_TICK = 2;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	private Runnable backToMatchAction;
	private SimulationManager simulationManager;
	private LocalDate gameDate;
	private Game game;
	private String homeTeamName;
	private String awayTeamName;

	private ArrayList<LiveMatchStatistics.LiveAction> liveActions;
	private int liveActionIndex;
	private LiveMatchStatistics liveMatchStatistics;
	private Thread liveThread;
	private boolean stop;
	private int displayedQuarter;
	private int displayedRemainingTimeSeconds;
	private int currentActionRemainingTimeSeconds;

	private LiveMatchHeaderPanel headerPanel;
	private LiveActionsPanel liveActionsPanel;
	private LiveTeamStatsPanel homeStatsPanel;
	private LiveTeamStatsPanel awayStatsPanel;

	public LiveMatchDashboard() {
		create();
		organize();
		actions();
		resetLiveState();
		updateLiveDashboard();
	}

	private void create() {
		homeTeamName = "HOME";
		awayTeamName = "AWAY";
		liveActions = new ArrayList<LiveMatchStatistics.LiveAction>();
		liveMatchStatistics = new LiveMatchStatistics();
		stop = true;
		displayedQuarter = 1;
		displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
		currentActionRemainingTimeSeconds = 0;
		headerPanel = new LiveMatchHeaderPanel();
		liveActionsPanel = new LiveActionsPanel(LIVE_ROWS);
		homeStatsPanel = new LiveTeamStatsPanel();
		awayStatsPanel = new LiveTeamStatsPanel();
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
		JPanel content = new JPanel(new BorderLayout(DASHBOARD_SPACING, DASHBOARD_SPACING));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(
				DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING));
		return content;
	}

	private JPanel buildHeader() {
		return headerPanel;
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new BorderLayout(DASHBOARD_SPACING, 0));
		body.setOpaque(false);
		body.add(buildLeftColumn(), BorderLayout.WEST);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildLeftColumn() {
		JPanel leftColumn = new JPanel(new BorderLayout());
		leftColumn.setOpaque(false);
		leftColumn.setPreferredSize(new Dimension(SIDE_COLUMN_WIDTH, 10));
		leftColumn.add(new BuildBox("ÉQUIPE DOMICILE", "Statistiques", homeStatsPanel), BorderLayout.CENTER);
		return leftColumn;
	}

	private JPanel buildCenterColumn() {
		return new BuildBox("ACTIONS EN DIRECT", "Déroulement du match", liveActionsPanel);
	}

	private JPanel buildRightColumn() {
		JPanel rightColumn = new JPanel(new BorderLayout());
		rightColumn.setOpaque(false);
		rightColumn.setPreferredSize(new Dimension(SIDE_COLUMN_WIDTH, 10));
		rightColumn.add(new BuildBox("ÉQUIPE EXTÉRIEUR", "Statistiques", awayStatsPanel), BorderLayout.CENTER);
		return rightColumn;
	}

	private void actions() {
		headerPanel.getBackButton().addActionListener(new BackAction());
		headerPanel.getPlayButton().addActionListener(new PlayAction());
		headerPanel.getNextQuarterButton().addActionListener(new PlayQuarterAction());
		headerPanel.getPauseButton().addActionListener(new PauseAction());
	}

	public void setBackToMatchAction(Runnable backToMatchAction) {
		this.backToMatchAction = backToMatchAction;
	}

	public void setSimulationContext(SimulationManager simulationManager, LocalDate gameDate) {
		this.simulationManager = simulationManager;
		this.gameDate = gameDate;
	}

	public void setGame(Game game) {
		stopLiveReading();
		this.game = game;
		if (game == null) {
			homeTeamName = "HOME";
			awayTeamName = "AWAY";
		} else {
			homeTeamName = game.getGameContext().getHomeTeam().getName();
			awayTeamName = game.getGameContext().getAwayTeam().getName();
		}
		liveMatchStatistics.setGame(game);
		buildLiveActions();
		resetLiveState();
		updateLiveDashboard();
	}

	private void buildLiveActions() {
		liveActions.clear();
		if (game == null || game.getQuarterResults() == null) {
			return;
		}
		GameResult[] quarterResults = game.getQuarterResults();
		for (int quarterIndex = 0; quarterIndex < quarterResults.length; quarterIndex++) {
			GameResult quarter = quarterResults[quarterIndex];
			if (quarter == null || quarter.getActions() == null) {
				continue;
			}
			int remainingTime = GameConfiguration.QUARTER_DURATION;
			for (ActionResult action : quarter.getActions()) {
				remainingTime -= action.getActionTime();
				if (remainingTime < 0) {
					remainingTime = 0;
				}
				liveActions.add(new LiveMatchStatistics.LiveAction(quarterIndex + 1, action, remainingTime));
			}
		}
	}

	private void playNextAction() {
		if (!isMatchAvailable()) {
			stopLiveReading();
			updateLiveDashboard();
			return;
		}
		if (liveActionIndex >= liveActions.size()) {
			revealCurrentGame();
			stopLiveReading();
			updateLiveDashboard();
			return;
		}

		LiveMatchStatistics.LiveAction liveAction = liveActions.get(liveActionIndex);
		liveMatchStatistics.applyAction(liveAction.getAction());
		liveActionIndex++;
		displayedQuarter = liveAction.getQuarter();
		displayedRemainingTimeSeconds = liveAction.getRemainingTimeSeconds();
		if (liveActionIndex >= liveActions.size()) {
			revealCurrentGame();
			stopLiveReading();
		} else {
			LiveMatchStatistics.LiveAction nextAction = liveActions.get(liveActionIndex);
			currentActionRemainingTimeSeconds = Math.max(1, nextAction.getAction().getActionTime());
			if (nextAction.getQuarter() != displayedQuarter) {
				displayedQuarter = nextAction.getQuarter();
				displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
			}
		}
		updateLiveDashboard();
	}

	private void playCurrentQuarter() {
		if (!isMatchAvailable() || liveActionIndex >= liveActions.size()) {
			return;
		}
		int quarterToPlay = liveActions.get(liveActionIndex).getQuarter();
		while (liveActionIndex < liveActions.size() && liveActions.get(liveActionIndex).getQuarter() == quarterToPlay) {
			playNextAction();
		}
	}

	private void startLiveReading() {
		if (!isMatchAvailable() || !stop) {
			return;
		}
		stop = false;
		if (liveActionIndex < liveActions.size()) {
			LiveMatchStatistics.LiveAction currentAction = liveActions.get(liveActionIndex);
			displayedQuarter = currentAction.getQuarter();
			if (liveActionIndex == 0) {
				displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
			}
			currentActionRemainingTimeSeconds = Math.max(1, currentAction.getAction().getActionTime());
		}
		liveThread = new Thread(this, "live-match-thread");
		liveThread.start();
	}

	private void stopLiveReading() {
		stop = true;
		if (liveThread != null) {
			liveThread.interrupt();
			liveThread = null;
		}
	}

	public void run() {
		while (!stop) {
			try {
				Thread.sleep(CHRONO_SPEED);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			decrementChronometer();
			if (!stop) {
				SwingUtilities.invokeLater(new UpdateValuesRunnable());
			}
		}
	}

	private void resetLiveState() {
		liveActionIndex = 0;
		liveMatchStatistics.reset();
		displayedQuarter = 1;
		displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
		currentActionRemainingTimeSeconds = liveActions.isEmpty() ? 0
				: Math.max(1, liveActions.get(0).getAction().getActionTime());
	}

	private void updateLiveDashboard() {
		updateHeaderPanel();
		updateStatsPanels();
		updateActionsPanel();
	}

	private void updateHeaderPanel() {
		if (game == null) {
			headerPanel.updateHeader(null, null,
					liveMatchStatistics.getHomePoints(), liveMatchStatistics.getAwayPoints(), buildQuarterLabel(),
					buildQuarterTimeText());
			return;
		}
		headerPanel.updateHeader(game.getGameContext().getHomeTeam(), game.getGameContext().getAwayTeam(),
				liveMatchStatistics.getHomePoints(), liveMatchStatistics.getAwayPoints(), buildQuarterLabel(),
				buildQuarterTimeText());
	}

	private void updateStatsPanels() {
		homeStatsPanel.updateStats(liveMatchStatistics.getHomePoints(), liveMatchStatistics.getHomeRebounds(),
				liveMatchStatistics.getHomeAssists(), liveMatchStatistics.getHomeTurnovers(),
				liveMatchStatistics.getHomeFgPercent(), liveMatchStatistics.getHomeThreePercent(),
				liveMatchStatistics.getHomeBestPlayers());
		awayStatsPanel.updateStats(liveMatchStatistics.getAwayPoints(), liveMatchStatistics.getAwayRebounds(),
				liveMatchStatistics.getAwayAssists(), liveMatchStatistics.getAwayTurnovers(),
				liveMatchStatistics.getAwayFgPercent(), liveMatchStatistics.getAwayThreePercent(),
				liveMatchStatistics.getAwayBestPlayers());
	}

	private void updateActionsPanel() {
		liveActionsPanel.updateRows(buildDisplayedRows(), buildCenterMessage());
	}

	private String buildQuarterLabel() {
		if (!isMatchAvailable()) {
			return "Q-";
		}
		if (liveActionIndex >= liveActions.size()) {
			return "FIN";
		}
		return "Q" + displayedQuarter;
	}

	private String buildQuarterTimeText() {
		if (!isMatchAvailable()) {
			return "--:--";
		}
		int min = displayedRemainingTimeSeconds / 60;
		int sec = displayedRemainingTimeSeconds % 60;
		return String.format("%d:%02d", min, sec);
	}

	private String[] buildDisplayedRows() {
		String[] rows = new String[LIVE_ROWS];
		for (int i = 0; i < LIVE_ROWS; i++) {
			rows[i] = " ";
		}
		if (!isMatchAvailable()) {
			return rows;
		}
		int startIndex = Math.max(0, liveActionIndex - LIVE_ROWS);
		int rowIndex = LIVE_ROWS - (liveActionIndex - startIndex);
		for (int actionIndex = startIndex; actionIndex < liveActionIndex; actionIndex++) {
			rows[rowIndex] = buildActionLabel(liveActions.get(actionIndex));
			rowIndex++;
		}
		return rows;
	}

	private class UpdateValuesRunnable implements Runnable {
		@Override
		public void run() {
			updateValues();
		}
	}

	private String buildCenterMessage() {
		if (game == null) {
			return "Aucun match sélectionné.";
		}
		if (!isMatchAvailable()) {
			return "Match non disponible.";
		}
		if (liveActionIndex == 0) {
			return "Clique sur Play pour lancer le match.";
		}
		return "";
	}

	private String buildActionLabel(LiveMatchStatistics.LiveAction liveAction) {
		ActionResult action = liveAction.getAction();
		int remaining = liveAction.getRemainingTimeSeconds();
		int min = remaining / 60;
		int sec = remaining % 60;
		return "Q" + liveAction.getQuarter() + " " + String.format("%d:%02d", min, sec) + " - "
				+ action.accept(new LiveActionTextVisitor(game, homeTeamName, awayTeamName));
	}

	private boolean isGameSimulated(Game game) {
		if (game == null || game.getQuarterResults() == null || game.getQuarterResults().length == 0) {
			return false;
		}
		for (GameResult quarterResult : game.getQuarterResults()) {
			if (quarterResult == null || quarterResult.getActions() == null || quarterResult.getActions().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean isMatchAvailable() {
		return isGameSimulated(game) && !liveActions.isEmpty();
	}

	private void revealCurrentGame() {
		if (game == null) {
			return;
		}
		game.setDisplayed(true);
	}

	private class BackAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			stopLiveReading();
			if (backToMatchAction != null) {
				backToMatchAction.run();
			}
		}
	}

	private class PlayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isMatchAvailable()) {
				updateLiveDashboard();
				return;
			}
			if (liveActionIndex >= liveActions.size()) {
				resetLiveState();
				updateLiveDashboard();
			}
			startLiveReading();
		}
	}

	private class PlayQuarterAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			stopLiveReading();
			playCurrentQuarter();
		}
	}

	private class PauseAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			stopLiveReading();
		}
	}

	private void decrementChronometer() {
		if (stop || !isMatchAvailable() || liveActionIndex >= liveActions.size()) {
			stopLiveReading();
			return;
		}
		if (displayedRemainingTimeSeconds > 0) {
			displayedRemainingTimeSeconds -= GAME_SECONDS_PER_TICK;
			if (displayedRemainingTimeSeconds < 0) {
				displayedRemainingTimeSeconds = 0;
			}
		}
		currentActionRemainingTimeSeconds -= GAME_SECONDS_PER_TICK;
	}

	private void updateValues() {
		if (stop || !isMatchAvailable() || liveActionIndex >= liveActions.size()) {
			stopLiveReading();
			updateLiveDashboard();
			return;
		}
		if (currentActionRemainingTimeSeconds <= 0) {
			playNextAction();
			return;
		}
		updateLiveDashboard();
	}

}
