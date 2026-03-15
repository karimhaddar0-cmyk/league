package gui.frame;

//! a relire 

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gui.dashboard.CalendarDashboard;
import gui.dashboard.FinanceDashboard;
import gui.dashboard.LiveMatchDashboard;
import gui.dashboard.MapDashboard;
import gui.dashboard.MatchDashboard;
import gui.dashboard.OpeningDashboard;
import gui.dashboard.RankingDashboard;
import gui.layout.SidebarPanel;
import process.manager.SimulationManager;

public class MainGui extends JFrame {

	private CardLayout rootLayout;
	private JPanel rootPanel;
	private CardLayout dashboardLayout;
	private JPanel dashboardPanel;
	private OpeningDashboard openingPanel;
	private JPanel mainPanel;
	private CalendarDashboard calendarDashboard;
	private MatchDashboard matchDashboard;
	private LiveMatchDashboard liveMatchDashboard;
	private SimulationManager simulationManager;
	private SidebarPanel sidebar;

	public MainGui() {
		create();
		organize();
		actions();
	}

	private void create() {
		setTitle("NBA League");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		rootLayout = new CardLayout();
		rootPanel = new JPanel(rootLayout);

		dashboardLayout = new CardLayout();
		dashboardPanel = new JPanel(dashboardLayout);

		openingPanel = new OpeningDashboard();
		mainPanel = buildApplicationPanel();
	}

	private void organize() {
		rootPanel.add(openingPanel, "opening");
		rootPanel.add(mainPanel, "main");

		setLayout(new BorderLayout());
		add(rootPanel, BorderLayout.CENTER);

		dashboardLayout.show(dashboardPanel, "match");
		rootLayout.show(rootPanel, "opening");

		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void actions() {
		openingPanel.getContinueButton().addActionListener(new OpenApplicationAction(openingPanel));
	}

	private JPanel buildApplicationPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		sidebar = new SidebarPanel();
		simulationManager = new SimulationManager();

		matchDashboard = new MatchDashboard(simulationManager.getLeagueManager());
		liveMatchDashboard = new LiveMatchDashboard();
		dashboardPanel.add(matchDashboard, "match");
		dashboardPanel.add(liveMatchDashboard, "liveMatch");
		calendarDashboard = new CalendarDashboard(simulationManager, matchDashboard, new ShowMatchDashboardAction());
		dashboardPanel.add(calendarDashboard, "calendar");
		dashboardPanel.add(new RankingDashboard(), "ranking");
		dashboardPanel.add(new FinanceDashboard(), "finance");
		dashboardPanel.add(new MapDashboard(), "map");

		matchDashboard.setOpenLiveMatchAction(new ShowLiveMatchDashboardAction());
		liveMatchDashboard.setBackToMatchAction(new ShowMatchDashboardAction());

		sidebar.getMatchButton().addActionListener(new SwitchDashboardAction("match"));
		sidebar.getCalendarButton().addActionListener(new SwitchDashboardAction("calendar"));
		sidebar.getRankingButton().addActionListener(new SwitchDashboardAction("ranking"));
		sidebar.getFinanceButton().addActionListener(new SwitchDashboardAction("finance"));
		sidebar.getMapButton().addActionListener(new SwitchDashboardAction("map"));
		sidebar.getExitButton().addActionListener(new QuitAction());

		mainPanel.add(sidebar, BorderLayout.WEST);
		mainPanel.add(dashboardPanel, BorderLayout.CENTER);

		return mainPanel;
	}

	private class SwitchDashboardAction implements ActionListener {
		private String cardName;

		public SwitchDashboardAction(String cardName) {
			this.cardName = cardName;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("calendar".equals(cardName)) {
				calendarDashboard.refreshSeasonState();
			}
			if ("match".equals(cardName)) {
				matchDashboard.refreshSelectedGame();
			}
			sidebar.setActiveSection(cardName);
			dashboardLayout.show(dashboardPanel, cardName);
		}
	}

	private class OpenApplicationAction implements ActionListener {
		private OpeningDashboard openingPanel;

		public OpenApplicationAction(OpeningDashboard openingPanel) {
			this.openingPanel = openingPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!openingPanel.hasSelectedProfil()) {
				openingPanel.showSelectionWarning();
				return;
			}

			calendarDashboard.startSeason();
			matchDashboard.loadGamesOfDay(simulationManager.getCurrentDate());
			sidebar.setActiveSection("match");
			dashboardLayout.show(dashboardPanel, "match");
			rootLayout.show(rootPanel, "main");
		}
	}

	private class QuitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	private class ShowMatchDashboardAction implements Runnable {
		@Override
		public void run() {
			calendarDashboard.refreshSeasonState();
			matchDashboard.refreshSelectedGame();
			sidebar.setActiveSection("match");
			dashboardLayout.show(dashboardPanel, "match");
		}
	}

	private class ShowLiveMatchDashboardAction implements Runnable {
		@Override
		public void run() {
			liveMatchDashboard.setSimulationContext(matchDashboard.getLeagueManager(), matchDashboard.getSelectedDate());
			liveMatchDashboard.setGame(matchDashboard.getSelectedGame());
			dashboardLayout.show(dashboardPanel, "liveMatch");
		}
	}

}
