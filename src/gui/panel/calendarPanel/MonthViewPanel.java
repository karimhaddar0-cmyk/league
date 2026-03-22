package gui.panel.calendarPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.calendar.GameDay;
import data.sport.setup.Game;
import gui.dashboard.MatchDashboard;
import gui.panel.common.TeamDisplayUtil;
import process.utilitary.CalendarUtilitary;

public class MonthViewPanel extends JPanel {
	private static final String[] DAY_NAMES = {"LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM"};
	private static final Color CURRENT_DAY_COLOR = new Color(0x2F, 0x80, 0xA9);
	private MatchDashboard matchDashboard;
	private Runnable showMatchDashboardAction;

	public MonthViewPanel() {
		setLayout(new GridLayout(0, 7));
		setBackground(Color.WHITE);
	}

	public void showMonth(YearMonth displayedMonth, LocalDate currentDate, HashMap<LocalDate, GameDay> calendar) {
		removeAll();

		for (int i = 0; i < DAY_NAMES.length; i++) {
			add(buildDayNameLabel(DAY_NAMES[i]));
		}

		LocalDate firstDayOfMonth = displayedMonth.atDay(1);
		int firstDayColumn = firstDayOfMonth.getDayOfWeek().getValue();
		int dayOffset = firstDayColumn - 1;
		LocalDate firstDateShown = firstDayOfMonth.minusDays(dayOffset);

		for (int i = 0; i < 42; i++) {
			LocalDate date = firstDateShown.plusDays(i);
			GameDay gameDay = null;
			if (calendar != null) {
				gameDay = calendar.get(date);
			}
			add(buildDayPanel(date, displayedMonth, gameDay, currentDate));
		}

		revalidate();
		repaint();
	}

	private JLabel buildDayNameLabel(String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		label.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		return label;
	}

	private JPanel buildDayPanel(LocalDate date, YearMonth displayedMonth, GameDay gameDay, LocalDate currentDate) {
		JPanel dayPanel = new JPanel();
		dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
		dayPanel.setOpaque(true);
		dayPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		dayPanel.setBackground(Color.WHITE);
		boolean sameMonth = isSameMonth(date, displayedMonth);

		if (sameMonth && gameDay != null && !gameDay.isEmpty()) {
			dayPanel.addMouseListener(new DayClickListener(gameDay, date));
		}

		if (gameDay != null && gameDay.isDisplayed()) {
			dayPanel.setBackground(new Color(230, 230, 230));
		}

		JLabel dayNumberLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
		if (!sameMonth) {
			dayNumberLabel.setForeground(Color.LIGHT_GRAY);
		}
		if (date.equals(currentDate)) {
			dayNumberLabel.setOpaque(true);
			dayNumberLabel.setBackground(CURRENT_DAY_COLOR);
			dayNumberLabel.setForeground(Color.WHITE);
		}
		dayPanel.add(dayNumberLabel);

		if (gameDay != null && !gameDay.isEmpty() && sameMonth) {
			ArrayList<Game> displayedGames = getBestGames(gameDay.getGames(), date);
			int matchCount = Math.min(3, displayedGames.size());
			for (int i = 0; i < matchCount; i++) {
				String homeTeam = TeamDisplayUtil.getAbbreviation(displayedGames.get(i).getGameContext().getHomeTeam());
				String awayTeam = TeamDisplayUtil.getAbbreviation(displayedGames.get(i).getGameContext().getAwayTeam());
				dayPanel.add(new JLabel(homeTeam + " vs " + awayTeam));
			}

			int remainingMatches = gameDay.getGames().size() - matchCount;
			if (remainingMatches > 0) {
				dayPanel.add(new JLabel("+" + remainingMatches + " autres"));
			}
		}

		return dayPanel;
	}

	private boolean isSameMonth(LocalDate date, YearMonth displayedMonth) {
		return date.getYear() == displayedMonth.getYear()
				&& date.getMonthValue() == displayedMonth.getMonthValue();
	}

	public static String buildMonthText(YearMonth yearMonth) {
		String monthText = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + yearMonth.getYear();
		return monthText;
	}

	public void setMatchDashboard(MatchDashboard matchDashboard, Runnable showMatchDashboardAction) {
		this.matchDashboard = matchDashboard;
		this.showMatchDashboardAction = showMatchDashboardAction;
	}

	private ArrayList<Game> getBestGames(ArrayList<Game> games, LocalDate date) {
		ArrayList<Game> remainingGames = new ArrayList<Game>(games);
		ArrayList<Game> bestGames = new ArrayList<Game>();

		while (!remainingGames.isEmpty() && bestGames.size() < 3) {
			Game bestGame = remainingGames.get(0);

			for (int i = 1; i < remainingGames.size(); i++) {
				double currentScore = CalendarUtilitary.popularityScoreGame(remainingGames.get(i), date);
				double bestScore = CalendarUtilitary.popularityScoreGame(bestGame, date);

				if (currentScore > bestScore) {
					bestGame = remainingGames.get(i);
				}
			}

			bestGames.add(bestGame);
			remainingGames.remove(bestGame);
		}

		return bestGames;
	}

	private class DayClickListener implements MouseListener {
		private GameDay gameDay;
		private LocalDate date;

		private DayClickListener(GameDay gameDay, LocalDate date) {
			this.gameDay = gameDay;
			this.date = date;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (matchDashboard != null) {
				matchDashboard.showGameDay(gameDay, date);
			}
			if (showMatchDashboardAction != null) {
				showMatchDashboardAction.run();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

}
