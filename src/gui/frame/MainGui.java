package gui.frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.dashboard.CalendarDashboard;
import gui.dashboard.FinanceDashboard;
import gui.dashboard.LiveMatchDashboard;
import gui.dashboard.LoadingDashboard;
import gui.dashboard.MapDashboard;
import gui.dashboard.MatchDashboard;
import gui.dashboard.OpeningDashboard;
import gui.dashboard.RankingDashboard;
import gui.dashboard.RosterDashboard;
import gui.layout.SidebarPanel;
import gui.managment.StartSimulationThread;
import process.orchestrator.GUIInterface;

public class MainGui extends JFrame {
	private static final long serialVersionUID = 1L;

	private MainGui instance = this;
	private static final String OPENING_CARD = "opening";
	private static final String LOADING_CARD = "loading";
	private static final String MAIN_CARD = "main";

	private CardLayout rootLayout;
	private JPanel rootPanel;
	private CardLayout dashboardLayout;
	private JPanel dashboardPanel;
	private OpeningDashboard openingPanel;
	private LoadingDashboard loadingDashboard;
	private JPanel mainPanel;
	private CalendarDashboard calendarDashboard;
	private MatchDashboard matchDashboard;
	private LiveMatchDashboard liveMatchDashboard;
	private MapDashboard mapDashboard;
	private RosterDashboard rosterDashboard;
	private GUIInterface guiInterface;
	private SidebarPanel sidebar;

	public MainGui(GUIInterface guiInterface) {
		this.guiInterface = guiInterface;
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
		openingPanel = new OpeningDashboard(guiInterface);
		loadingDashboard = new LoadingDashboard();
		mainPanel = buildApplicationPanel();
	}

	private void organize() {
		rootPanel.add(openingPanel, OPENING_CARD);
		rootPanel.add(loadingDashboard, LOADING_CARD);
		rootPanel.add(mainPanel, MAIN_CARD);

		setLayout(new BorderLayout());
		add(rootPanel, BorderLayout.CENTER);

		dashboardLayout.show(dashboardPanel, "match");
		rootLayout.show(rootPanel, OPENING_CARD);

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

		matchDashboard = new MatchDashboard(guiInterface);
		liveMatchDashboard = new LiveMatchDashboard(guiInterface);
		mapDashboard = new MapDashboard(guiInterface);
		rosterDashboard = new RosterDashboard(guiInterface);
		dashboardPanel.add(matchDashboard, "match");
		dashboardPanel.add(liveMatchDashboard, "liveMatch");
		calendarDashboard = new CalendarDashboard(guiInterface, matchDashboard, new ShowMatchDashboardAction(),
				rosterDashboard, mapDashboard);
		dashboardPanel.add(calendarDashboard, "calendar");
		dashboardPanel.add(new RankingDashboard(), "ranking");
		dashboardPanel.add(new FinanceDashboard(), "finance");
		dashboardPanel.add(mapDashboard, "map");
		dashboardPanel.add(rosterDashboard, "roster");

		matchDashboard.setOpenLiveMatchAction(new ShowLiveMatchDashboardAction());
		liveMatchDashboard.setBackToMatchAction(new ShowMatchDashboardAction());
		mapDashboard.setOpenRosterAction(new ShowRosterDashboardAction());
		rosterDashboard.setBackToMapAction(new ShowMapDashboardAction());

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

			rootLayout.show(rootPanel, LOADING_CARD);
			Thread startSimulationThread = new Thread(
					new StartSimulationThread(
							guiInterface, instance),
					"start-simulation-thread");
			startSimulationThread.start();
		}
	}

	public void finishSimulationLoading() {
		calendarDashboard.refreshSeasonState();
		matchDashboard.loadGamesOfDay(guiInterface.getCurrentDate());
		sidebar.setActiveSection("match");
		dashboardLayout.show(dashboardPanel, "match");
		rootLayout.show(rootPanel, MAIN_CARD);
	}

	private class QuitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainGui frame = MainGui.this;
			String question = "Voulez-vous vraiment quitter la simulation ?";
			int choice = JOptionPane.showConfirmDialog(frame, question, "Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (choice == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
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
			liveMatchDashboard.setGame(matchDashboard.getSelectedGame());
			dashboardLayout.show(dashboardPanel, "liveMatch");
		}
	}

	private class ShowRosterDashboardAction implements Runnable {
		@Override
		public void run() {
			rosterDashboard.setSelectedTeam(mapDashboard.getSelectedTeam());
			sidebar.setActiveSection("map");
			dashboardLayout.show(dashboardPanel, "roster");
		}
	}

	private class ShowMapDashboardAction implements Runnable {
		@Override
		public void run() {
			sidebar.setActiveSection("map");
			dashboardLayout.show(dashboardPanel, "map");
		}
	}
}
