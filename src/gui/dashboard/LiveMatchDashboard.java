package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.sport.play.action.ActionResult;
import data.sport.setup.Game;
import gui.management.LiveAction;
import gui.management.LiveMatchManager;
import gui.management.LiveMatchStatistics;
import gui.panel.common.BuildBox;
import gui.panel.matchPanel.liveMatchPanel.LiveActionsPanel;
import gui.panel.matchPanel.liveMatchPanel.LiveMatchHeaderPanel;
import gui.panel.matchPanel.liveMatchPanel.LiveTeamStatsPanel;
import process.manager.LeagueManager;
import process.visitor.actionresult.LiveActionTextVisitor;

public class LiveMatchDashboard extends JPanel {
	private static final int DASHBOARD_SPACING = 16;
	private static final int SIDE_COLUMN_WIDTH = 270;
	private static final int LIVE_ROWS = 10;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	private Runnable backToMatchAction;
	private LiveMatchManager liveMatchManager;

	private LiveMatchHeaderPanel headerPanel;
	private LiveActionsPanel liveActionsPanel;
	private LiveTeamStatsPanel homeStatsPanel;
	private LiveTeamStatsPanel awayStatsPanel;

	public LiveMatchDashboard() {
		create();
		organize();
		actions();
		updateLiveDashboard();
	}

	private void create() {
		liveMatchManager = new LiveMatchManager();
		liveMatchManager.setRefreshAction(new RefreshAction());
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
		liveMatchManager.setSimulationContext(leagueManager, gameDate);
	}

	public void setGame(Game game) {
		liveMatchManager.setGame(game);
	}

	private void updateLiveDashboard() {
		LiveMatchStatistics liveMatchStatistics = liveMatchManager.getLiveMatchStatistics();
		headerPanel.updateHeader(liveMatchManager.getHomeTeamName(), liveMatchManager.getAwayTeamName(),
				liveMatchStatistics.getHomePoints(), liveMatchStatistics.getAwayPoints(),
				buildQuarterLabel(), buildQuarterTimeText());

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
		if (!liveMatchManager.isMatchAvailable()) {
			return "Q-";
		}
		if (liveMatchManager.getLiveActionIndex() >= liveMatchManager.getLiveActions().size()) {
			return "FIN";
		}
		return "Q" + liveMatchManager.getDisplayedQuarter();
	}

	private String buildQuarterTimeText() {
		if (!liveMatchManager.isMatchAvailable()) {
			return "--:--";
		}
		return formatTime(liveMatchManager.getDisplayedRemainingTimeSeconds());
	}

	private String[] buildDisplayedRows() {
		String[] rows = new String[LIVE_ROWS];
		for (int i = 0; i < LIVE_ROWS; i++) {
			rows[i] = " ";
		}
		if (!liveMatchManager.isMatchAvailable()) {
			return rows;
		}
		int startIndex = Math.max(0, liveMatchManager.getLiveActionIndex() - LIVE_ROWS);
		int rowIndex = LIVE_ROWS - (liveMatchManager.getLiveActionIndex() - startIndex);
		for (int actionIndex = startIndex; actionIndex < liveMatchManager.getLiveActionIndex(); actionIndex++) {
			rows[rowIndex] = buildActionLabel(liveMatchManager.getLiveActions().get(actionIndex));
			rowIndex++;
		}
		return rows;
	}

	private String buildCenterMessage() {
		if (liveMatchManager.getGame() == null) {
			return "Aucun match selectionne.";
		}
		if (!liveMatchManager.isMatchAvailable()) {
			return "Match non disponible.";
		}
		if (liveMatchManager.getLiveActionIndex() == 0) {
			return "Clique sur Play pour lancer le match.";
		}
		return "";
	}

	private String buildActionLabel(LiveAction liveAction) {
		ActionResult action = liveAction.getAction();
		return "Q" + liveAction.getQuarter() + " " + formatTime(liveAction.getRemainingTimeSeconds()) + " - "
				+ action.accept(new LiveActionTextVisitor(liveMatchManager.getGame(),
						liveMatchManager.getHomeTeamName(), liveMatchManager.getAwayTeamName()));
	}

	private String formatTime(int remainingTimeSeconds) {
		int min = remainingTimeSeconds / 60;
		int sec = remainingTimeSeconds % 60;
		return String.format("%d:%02d", min, sec);
	}

	private class BackAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			liveMatchManager.pause();
			if (backToMatchAction != null) {
				backToMatchAction.run();
			}
		}
	}

	private class PlayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			liveMatchManager.play();
		}
	}

	private class PlayQuarterAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			liveMatchManager.playCurrentQuarter();
		}
	}

	private class PauseAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			liveMatchManager.pause();
		}
	}

	private class RefreshAction implements Runnable {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new UpdateDashboardAction());
		}
	}

	private class UpdateDashboardAction implements Runnable {
		@Override
		public void run() {
			updateLiveDashboard();
		}
	}
}
