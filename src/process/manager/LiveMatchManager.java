package process.manager;

import config.GameConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;

import data.sport.play.action.ActionResult;
import data.sport.setup.Game;
import data.sport.setup.GameResult;
public class LiveMatchManager implements Runnable {
	private static final int CHRONO_SPEED = 200;
	private static final int GAME_SECONDS_PER_TICK = 2;

	private LeagueManager leagueManager;
	private LocalDate gameDate;
	private Game game;
	private String homeTeamName;
	private String awayTeamName;
	private ArrayList<LiveAction> liveActions;
	private int liveActionIndex;
	private LiveMatchStatistics liveMatchStatistics;
	private Thread liveThread;
	private boolean stop;
	private int displayedQuarter;
	private int displayedRemainingTimeSeconds;
	private int currentActionRemainingTimeSeconds;
	private Runnable refreshAction;

	public LiveMatchManager() {
		homeTeamName = "HOME";
		awayTeamName = "AWAY";
		liveActions = new ArrayList<LiveAction>();
		liveMatchStatistics = new LiveMatchStatistics();
		stop = true;
		displayedQuarter = 1;
		displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
		currentActionRemainingTimeSeconds = 0;
	}

	public void setSimulationContext(LeagueManager leagueManager, LocalDate gameDate) {
		this.leagueManager = leagueManager;
		this.gameDate = gameDate;
	}

	public void setRefreshAction(Runnable refreshAction) {
		this.refreshAction = refreshAction;
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
		reset();
		requestRefresh();
	}

	public void reset() {
		liveActionIndex = 0;
		liveMatchStatistics.reset();
		displayedQuarter = 1;
		displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
		currentActionRemainingTimeSeconds = liveActions.isEmpty() ? 0
				: Math.max(1, liveActions.get(0).getAction().getActionTime());
	}

	public void play() {
		prepareMatch();
		if (!isMatchAvailable()) {
			requestRefresh();
			return;
		}
		if (liveActionIndex >= liveActions.size()) {
			reset();
		}
		startLiveReading();
		requestRefresh();
	}

	public void pause() {
		stopLiveReading();
		requestRefresh();
	}

	public void playCurrentQuarter() {
		stopLiveReading();
		prepareMatch();
		if (!isMatchAvailable() || liveActionIndex >= liveActions.size()) {
			requestRefresh();
			return;
		}
		int quarterToPlay = liveActions.get(liveActionIndex).getQuarter();
		while (liveActionIndex < liveActions.size() && liveActions.get(liveActionIndex).getQuarter() == quarterToPlay) {
			playNextAction(false);
		}
		requestRefresh();
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Thread.sleep(CHRONO_SPEED);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			decrementChronometer();
			if (!stop) {
				updateValues();
			}
		}
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public Game getGame() {
		return game;
	}

	public ArrayList<LiveAction> getLiveActions() {
		return liveActions;
	}

	public int getLiveActionIndex() {
		return liveActionIndex;
	}

	public int getDisplayedQuarter() {
		return displayedQuarter;
	}

	public int getDisplayedRemainingTimeSeconds() {
		return displayedRemainingTimeSeconds;
	}

	public LiveMatchStatistics getLiveMatchStatistics() {
		return liveMatchStatistics;
	}

	private void prepareMatch() {
		if (game == null || isMatchAvailable()) {
			return;
		}
		if (leagueManager == null || gameDate == null) {
			return;
		}
		if (!leagueManager.simulateGame(game, gameDate)) {
			return;
		}
		buildLiveActions();
		reset();
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
				liveActions.add(new LiveAction(quarterIndex + 1, action, remainingTime));
			}
		}
	}

	private void playNextAction(boolean refreshValues) {
		if (!isMatchAvailable()) {
			stopLiveReading();
			if (refreshValues) {
				requestRefresh();
			}
			return;
		}
		if (liveActionIndex >= liveActions.size()) {
			revealCurrentGame();
			stopLiveReading();
			if (refreshValues) {
				requestRefresh();
			}
			return;
		}

		LiveAction liveAction = liveActions.get(liveActionIndex);
		liveMatchStatistics.applyAction(liveAction.getAction());
		liveActionIndex++;
		displayedQuarter = liveAction.getQuarter();
		displayedRemainingTimeSeconds = liveAction.getRemainingTimeSeconds();
		if (liveActionIndex >= liveActions.size()) {
			revealCurrentGame();
			stopLiveReading();
		} else {
			LiveAction nextAction = liveActions.get(liveActionIndex);
			currentActionRemainingTimeSeconds = Math.max(1, nextAction.getAction().getActionTime());
			if (nextAction.getQuarter() != displayedQuarter) {
				displayedQuarter = nextAction.getQuarter();
				displayedRemainingTimeSeconds = GameConfiguration.QUARTER_DURATION;
			}
		}
		if (refreshValues) {
			requestRefresh();
		}
	}

	private void startLiveReading() {
		if (!isMatchAvailable() || !stop) {
			return;
		}
		stop = false;
		if (liveActionIndex < liveActions.size()) {
			LiveAction currentAction = liveActions.get(liveActionIndex);
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
			requestRefresh();
			return;
		}
		if (currentActionRemainingTimeSeconds <= 0) {
			playNextAction(true);
			return;
		}
		requestRefresh();
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

	private void revealCurrentGame() {
		if (game == null) {
			return;
		}
		game.setDisplayed(true);
	}

	private void requestRefresh() {
		if (refreshAction != null) {
			refreshAction.run();
		}
	}

	public boolean isMatchAvailable() {
		return isGameSimulated(game) && !liveActions.isEmpty();
	}
}
