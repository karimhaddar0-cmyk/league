package gui.panel.calendarPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.calendar.GameDay;
import gui.managment.SimulateDayThread;
import gui.panel.common.RoundedButton;
import process.orchestrator.GUIInterface;

public class WeekViewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Font DISPLAY_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
	private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");

	private final JButton previousDayButton = new RoundedButton("Semaine -");
	private final JButton nextDayButton = new RoundedButton("Semaine +");
	private final JLabel currentDateLabel = new JLabel();
	private final JPanel matchDisplayPanel = new JPanel();
	private final JLabel loadingLabel = new JLabel("Simulation en cours...");

	private final GUIInterface guiInterface;
	private LocalDate displayedDate;
	private LocalDate lastSimulatedDate;
	private OpenMatchDayAction openMatchDayAction;

	public volatile boolean finishedSimulation = true;
	private LocalDate dayBeingSimulated;

	public WeekViewPanel(GUIInterface guiInterface) {
		this.guiInterface = guiInterface;
		create();
		organize();
		actions();
		showWaitingState();
	}

	private void create() {
		currentDateLabel.setFont(DISPLAY_FONT);
		currentDateLabel.setText("Date : -");
		matchDisplayPanel.setLayout(new BoxLayout(matchDisplayPanel, BoxLayout.Y_AXIS));
		matchDisplayPanel.setOpaque(false);
		loadingLabel.setFont(TEXT_FONT);
		loadingLabel.setVisible(false);
	}

	private void organize() {
		setLayout(new BorderLayout());
		JPanel topBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topBarPanel.add(previousDayButton);
		topBarPanel.add(currentDateLabel);
		topBarPanel.add(nextDayButton);
		topBarPanel.add(loadingLabel);

		add(topBarPanel, BorderLayout.NORTH);
		add(matchDisplayPanel, BorderLayout.CENTER);
	}

	private void actions() {
		previousDayButton.addActionListener(new PreviousWeekListener());
		nextDayButton.addActionListener(new NextWeekListener());
	}

	public void loadSeasonState() {
		if (!guiInterface.isSeasonInitialized()) {
			displayedDate = null;
			lastSimulatedDate = null;
			updateDisplay();
			return;
		}
		syncToSimulationDate(guiInterface.getCurrentDate());
	}

	public void syncToSimulationDate(LocalDate simulationDate) {
		if (!guiInterface.isSeasonInitialized() || simulationDate == null) {
			displayedDate = null;
			lastSimulatedDate = null;
			updateDisplay();
			return;
		}
		lastSimulatedDate = simulationDate;
		displayedDate = guiInterface.getCalendarDisplayDate(simulationDate);
		if (displayedDate == null) {
			displayedDate = simulationDate;
		}
		updateDisplay();
	}

	public void advanceDay() {
		if (!guiInterface.isSeasonInitialized() || displayedDate == null) {
			return;
		}

		LocalDate day = displayedDate;
		simulateDisplayedDay(day);
		lastSimulatedDate = day;
		displayedDate = guiInterface.getNextGameDay(day.plusDays(1));
		if (displayedDate == null) {
			displayedDate = day;
		}
		updateDisplay();
	}

	public void advanceWeek() {
		if (!guiInterface.isSeasonInitialized() || displayedDate == null) {
			return;
		}

		LocalDate weekStart = getWeekStart(displayedDate);
		LocalDate weekEnd = weekStart.plusDays(6);
		LocalDate simulatedDay = lastSimulatedDate;

		for (LocalDate day = weekStart; !day.isAfter(weekEnd); day = day.plusDays(1)) {
			if (hasGame(day)) {
				simulateDisplayedDay(day);
				simulatedDay = day;
			}
		}

		lastSimulatedDate = simulatedDay;
		LocalDate nextWeekStart = weekEnd.plusDays(1);
		if (!nextWeekStart.isAfter(guiInterface.getRegularSeasonEndDate())) {
			displayedDate = guiInterface.getNextGameDay(nextWeekStart);
		} else {
			displayedDate = lastSimulatedDate;
		}
		if (displayedDate == null) {
			displayedDate = lastSimulatedDate;
		}
		updateDisplay();
	}

	public void advanceSeason() {
		if (!guiInterface.isSeasonInitialized() || displayedDate == null) {
			return;
		}

		LocalDate simulatedDay = lastSimulatedDate;
		for (LocalDate day : guiInterface.getSeasonCalendar().keySet()) {
			if (day.isBefore(displayedDate)) {
				continue;
			}
			simulateDisplayedDay(day);
			simulatedDay = day;
		}

		lastSimulatedDate = simulatedDay;
		displayedDate = lastSimulatedDate;
		updateDisplay();
	}

	private void updateDisplay() {
		if (!guiInterface.isSeasonInitialized() || displayedDate == null) {
			showWaitingState();
			return;
		}
		currentDateLabel.setText(buildWeekLabel());
		updateWeekRows();
		repaint();
	}

	private String buildWeekLabel() {
		LocalDate weekStart = getWeekStart(displayedDate);
		LocalDate weekEnd = weekStart.plusDays(6);
		return "Semaine du " + WEEK_FORMATTER.format(weekStart) + " au " + WEEK_FORMATTER.format(weekEnd);
	}

	private void showWaitingState() {
		matchDisplayPanel.removeAll();
		JLabel waitingLabel = new JLabel("Saison non initialisée.");
		waitingLabel.setFont(TEXT_FONT);
		waitingLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		matchDisplayPanel.add(waitingLabel);
		matchDisplayPanel.revalidate();
		matchDisplayPanel.repaint();
	}

	public void setOpenMatchDayAction(OpenMatchDayAction openMatchDayAction) {
		this.openMatchDayAction = openMatchDayAction;
	}

	public LocalDate getCurrentDate() {
		return displayedDate;
	}

	public LocalDate getSimulationDate() {
		return lastSimulatedDate;
	}

	public void setSimulationDate(LocalDate date) {
		lastSimulatedDate = date;
		displayedDate = date;
		updateDisplay();
	}

	private void simulateDisplayedDay(LocalDate day) {
		finishedSimulation = false;
		Thread thread = new Thread(new SimulateDayThread(guiInterface, day, this));
		thread.start();
	}

	private boolean hasGame(LocalDate day) {
		GameDay gameDay = getGameDay(day);
		return gameDay != null && !gameDay.isEmpty();
	}

	private GameDay getGameDay(LocalDate day) {
		return guiInterface.getGameDay(day);
	}

	private void updateWeekRows() {
		matchDisplayPanel.removeAll();
		LocalDate weekStart = getWeekStart(displayedDate);
		for (int offset = 0; offset < 7; offset++) {
			LocalDate day = weekStart.plusDays(offset);
			if (day.isBefore(guiInterface.getRegularSeasonStartDate())
					|| day.isAfter(guiInterface.getRegularSeasonEndDate())) {
				continue;
			}
			GameDay gameDay = getGameDay(day);
			if (gameDay == null || gameDay.isEmpty()) {
				continue;
			}
			matchDisplayPanel.add(buildDayRow(day));
			matchDisplayPanel.add(Box.createVerticalStrut(12));
		}
		if (matchDisplayPanel.getComponentCount() == 0) {
			JLabel emptyLabel = new JLabel("Aucun match sur cette semaine.");
			emptyLabel.setFont(TEXT_FONT);
			emptyLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
			matchDisplayPanel.add(emptyLabel);
		} else {
			matchDisplayPanel.remove(matchDisplayPanel.getComponentCount() - 1);
		}
		matchDisplayPanel.revalidate();
		matchDisplayPanel.repaint();
	}

	private JPanel buildDayRow(LocalDate day) {
		GameDay gameDay = getGameDay(day);
		boolean displayed = gameDay.isDisplayed();

		ActionListener simulateAction = new SimulateDayListener(day, displayed);
		ActionListener detailAction = new DetailDayListener(gameDay, day);
		return new WeekDayRowPanel(day, gameDay, displayed, simulateAction, detailAction);
	}

	private LocalDate getWeekStart(LocalDate date) {
		return date.minusDays(date.getDayOfWeek().getValue() - 1L);
	}

	private void openMatchDashboard(GameDay gameDay, LocalDate date) {
		if (openMatchDayAction != null) {
			openMatchDayAction.open(gameDay, date);
		}
	}

	private void showPreviousWeek() {
		if (!guiInterface.isSeasonInitialized() || displayedDate == null) {
			return;
		}
		LocalDate previousWeek = displayedDate.minusDays(7);
		if (!previousWeek.isBefore(guiInterface.getRegularSeasonStartDate())) {
			displayedDate = guiInterface.getNextGameDay(previousWeek);
			if (displayedDate == null) {
				displayedDate = previousWeek;
			}
			updateDisplay();
		}
	}

	public static class OpenMatchDayAction {
		public void open(GameDay gameDay, LocalDate date) {
		}
	}

	private class PreviousWeekListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			showPreviousWeek();
		}
	}

	private class NextWeekListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			advanceWeek();
		}
	}

	private class SimulateDayListener implements ActionListener {
		private final LocalDate day;
		private final boolean displayed;

		private SimulateDayListener(LocalDate day, boolean displayed) {
			this.day = day;
			this.displayed = displayed;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (displayed) {
				return;
			}
			dayBeingSimulated = day;
			showLoadingState();
			simulateDisplayedDay(day);
			startSimualtionCharging(day);
		}
	}

	private class DetailDayListener implements ActionListener {
		private final GameDay gameDay;
		private final LocalDate day;

		private DetailDayListener(GameDay gameDay, LocalDate day) {
			this.gameDay = gameDay;
			this.day = day;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			openMatchDashboard(gameDay, day);
		}
	}

	private void startSimualtionCharging(LocalDate day) {
		while (!finishedSimulation) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			showLoadingState();
			// ajout d'une barre de chargement ou d'un indicateur de progression ici
		}
		hideLoadingState();
		lastSimulatedDate = day;
		displayedDate = day;
		updateDisplay();
	}

	private void showLoadingState() {
		previousDayButton.setEnabled(false);
		nextDayButton.setEnabled(false);
		loadingLabel.setVisible(true);
	}

	private void hideLoadingState() {
		previousDayButton.setEnabled(true);
		nextDayButton.setEnabled(true);
		loadingLabel.setVisible(false);
	}

	public boolean isFinishedSimulation() {
		return finishedSimulation;
	}

	public void setFinishedSimulation(boolean finishedSimulation) {
		this.finishedSimulation = finishedSimulation;
	}
}
