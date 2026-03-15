package gui.dashboard;
import config.GameConfiguration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import data.calendar.GameDay;
import data.player.Player;
import data.sport.play.action.ActionResult;
import data.sport.play.action.Block;
import data.sport.play.action.EndOfTime;
import data.sport.play.action.MissedShot;
import data.sport.play.action.PointScored;
import data.sport.play.action.Rebound;
import data.sport.play.action.Turnover;
import data.sport.setup.Game;
import data.sport.setup.GameResult;
import gui.management.LiveMatchStatistics;
import gui.panel.common.BuildBox;
import gui.panel.liveMatchPanel.LiveActionsPanel;
import gui.panel.liveMatchPanel.LiveMatchHeaderPanel;
import gui.panel.liveMatchPanel.LiveTeamStatsPanel;
import process.manager.LeagueManager;
import process.visitor.actionresult.ActionResultVisitor;

public class LiveMatchDashboard extends JPanel implements Runnable {
	private static final int DASHBOARD_SPACING = 16;
	private static final int SIDE_COLUMN_WIDTH = 270;
	private static final int LIVE_ROWS = 10;
	private static final int LIVE_DELAY_MS = 700;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	private Runnable backToMatchAction;
	private LeagueManager leagueManager;
	private LocalDate gameDate;
	private Game game;
	private String homeTeamName;
	private String awayTeamName;

	private ArrayList<LiveMatchStatistics.LiveAction> liveActions;
	private int liveActionIndex;
	private LiveMatchStatistics liveMatchStatistics;
	private LiveMatchDashboard instance;
	private boolean stop;

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
		instance = this;
		stop = true;
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

	public void setSimulationContext(LeagueManager leagueManager, LocalDate gameDate) {
		this.leagueManager = leagueManager;
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
			for (ActionResult action : quarter.getActions()) {
				liveActions.add(new LiveMatchStatistics.LiveAction(quarterIndex + 1, action));
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
		liveMatchStatistics.applyAction(liveAction.getAction(), new LiveMatchStatistics.HomePlayerChecker() {
			@Override
			public boolean isHomePlayer(Player player) {
				return LiveMatchDashboard.this.isHomePlayer(player);
			}
		});
		liveActionIndex++;
		if (liveActionIndex >= liveActions.size()) {
			revealCurrentGame();
			stopLiveReading();
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
		Thread liveThread = new Thread(instance);
		liveThread.start();
	}

	private void stopLiveReading() {
		stop = true;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Thread.sleep(LIVE_DELAY_MS);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			if (!stop) {
				playNextAction();
			}
		}
	}

	private void resetLiveState() {
		liveActionIndex = 0;
		liveMatchStatistics.reset();
	}

	private void updateLiveDashboard() {
		headerPanel.updateHeader(homeTeamName, awayTeamName, liveMatchStatistics.getHomePoints(),
				liveMatchStatistics.getAwayPoints(), buildQuarterLabel(), buildQuarterTimeText());

		homeStatsPanel.updateStats(liveMatchStatistics.getHomePoints(), liveMatchStatistics.getHomeRebounds(),
				liveMatchStatistics.getHomeAssists(), liveMatchStatistics.getHomeTurnovers(),
				liveMatchStatistics.getHomeFgPercent(), liveMatchStatistics.getHomeThreePercent(),
				liveMatchStatistics.getHomeBestPlayers());
		awayStatsPanel.updateStats(liveMatchStatistics.getAwayPoints(), liveMatchStatistics.getAwayRebounds(),
				liveMatchStatistics.getAwayAssists(), liveMatchStatistics.getAwayTurnovers(),
				liveMatchStatistics.getAwayFgPercent(), liveMatchStatistics.getAwayThreePercent(),
				liveMatchStatistics.getAwayBestPlayers());

		liveActionsPanel.updateRows(buildDisplayedRows(), buildCenterMessage());
	}

	private String buildQuarterLabel() {
		if (!isMatchAvailable()) {
			return "Q-";
		}
		if (liveActionIndex <= 0) {
			return "Q1";
		}
		if (liveActionIndex >= liveActions.size()) {
			return "FIN";
		}
		return "Q" + liveActions.get(liveActionIndex).getQuarter();
	}

	private String buildQuarterTimeText() {
		if (!isMatchAvailable()) {
			return "--:--";
		}
		if (liveActionIndex <= 0) {
			return "12:00";
		}
		LiveMatchStatistics.LiveAction currentAction = liveActions.get(liveActionIndex - 1);
		int remaining = GameConfiguration.QUARTER_DURATION - currentAction.getAction().getActionTime();
		if (remaining < 0) {
			remaining = 0;
		}
		int min = remaining / 60;
		int sec = remaining % 60;
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
		int remaining = GameConfiguration.QUARTER_DURATION - action.getActionTime();
		if (remaining < 0) {
			remaining = 0;
		}
		int min = remaining / 60;
		int sec = remaining % 60;
		return "Q" + liveAction.getQuarter() + " " + String.format("%d:%02d", min, sec) + " - "
				+ action.accept(new LiveActionTextVisitor());
	}

	private boolean isHomePlayer(Player player) {
		if (game == null || player == null) {
			return false;
		}
		return game.getGameContext().getHomeTeam().getPlayers().containsKey(player.getName());
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

	private int computeDisplayedPoints(PointScored pointScored) {
		if (pointScored.getOffensiveAction() == null) {
			return pointScored.getPointsScored();
		}
		String offensiveName = pointScored.getOffensiveAction().getName();
		if (GameConfiguration.THREEPOINT.equals(offensiveName)) {
			return 3;
		}
		if (GameConfiguration.TWOPOINT.equals(offensiveName)) {
			return 2;
		}
		return 1;
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

	private class LiveActionTextVisitor implements ActionResultVisitor<String> {
		@Override
		public String visit(PointScored pointScored) {
			Player scorer = pointScored.getScorerPlayer();
			String team = isHomePlayer(scorer) ? homeTeamName : awayTeamName;
			return team + " - " + scorer.getName() + " +" + computeDisplayedPoints(pointScored);
		}

		@Override
		public String visit(MissedShot missedShot) {
			Player shooter = missedShot.getShooter();
			String team = isHomePlayer(shooter) ? homeTeamName : awayTeamName;
			String shotLabel = "tir";
			if (missedShot.getOffensiveAction() != null) {
				String shotType = missedShot.getOffensiveAction().getName();
				if (GameConfiguration.THREEPOINT.equals(shotType)) {
					shotLabel = "3 points";
				} else if (GameConfiguration.TWOPOINT.equals(shotType)) {
					shotLabel = "2 points";
				} else if (GameConfiguration.FOULDRAW.equals(shotType)) {
					shotLabel = "lancer franc";
				}
			}
			return team + " - " + shooter.getName() + " rate un " + shotLabel;
		}

		@Override
		public String visit(Turnover turnover) {
			Player intercepted = turnover.getInterceptedPlayer();
			String team = isHomePlayer(intercepted) ? homeTeamName : awayTeamName;
			return team + " - Ballon perdu " + intercepted.getName();
		}

		@Override
		public String visit(Block block) {
			Player blocker = block.getBlockingPlayer();
			String team = isHomePlayer(blocker) ? homeTeamName : awayTeamName;
			return team + " - Contre " + blocker.getName();
		}

		@Override
		public String visit(Rebound rebound) {
			Player reboundPlayer = rebound.getReboundPlayer();
			String team = isHomePlayer(reboundPlayer) ? homeTeamName : awayTeamName;
			return team + " - Rebond " + reboundPlayer.getName();
		}

		@Override
		public String visit(EndOfTime endOfTime) {
			return "Fin de période";
		}
	}
}
