package gui.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gui.layout.strategy.ButtonHighlightStrategy;
import gui.layout.strategy.SidebarHighlightStrategy;

public class SidebarPanel extends JPanel {
	private static final Color SIDEBAR_BACKGROUND_COLOR = new Color(255, 255, 255);
	private static final Color ACTIVE_BUTTON_BACKGROUND_COLOR = new Color(230, 235, 240);
	private static final Color BUTTON_TEXT_COLOR = new Color(40, 40, 40);

	private JButton matchButton = new JButton("Match");
	private JButton calendarButton = new JButton("Calendrier");
	private JButton rankingButton = new JButton("Classement");
	private JButton financeButton = new JButton("Finance");
	private JButton mapButton = new JButton("Carte");
	private JButton exitButton = new JButton("Quitter");
	private Map<String, SidebarHighlightStrategy> highlightStrategies = new HashMap<String, SidebarHighlightStrategy>();
	private JButton[] menuButtons;

	public SidebarPanel() {
		create();
		organize();
	}

	private void create() {
		menuButtons = new JButton[] { matchButton, calendarButton, rankingButton, financeButton, mapButton };
		initializeHighlightStrategies();
	}

	private void organize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(240, 0));
		setBackground(SIDEBAR_BACKGROUND_COLOR);

		add(buildTopSection(), BorderLayout.NORTH);
		add(buildMenuSection(), BorderLayout.CENTER);
		add(buildBottomSection(), BorderLayout.SOUTH);
	}

	private JPanel buildTopSection() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(SIDEBAR_BACKGROUND_COLOR);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		ImageIcon logoIcon = new ImageIcon("img/logo.png");
		JLabel logoLabel = new JLabel(logoIcon);
		logoLabel.setAlignmentX(CENTER_ALIGNMENT);

		JLabel title = new JLabel("NBA League");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		title.setAlignmentX(CENTER_ALIGNMENT);

		JLabel subtitle = new JLabel("Management");
		subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		subtitle.setForeground(Color.GRAY);
		subtitle.setAlignmentX(CENTER_ALIGNMENT);

		panel.add(logoLabel);
		panel.add(Box.createVerticalStrut(10));
		panel.add(title);
		panel.add(subtitle);

		return panel;
	}

	private JPanel buildMenuSection() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(SIDEBAR_BACKGROUND_COLOR);

		configureMenuButton(matchButton);
		configureMenuButton(calendarButton);
		configureMenuButton(rankingButton);
		configureMenuButton(financeButton);
		configureMenuButton(mapButton);

		matchButton.addActionListener(new HighlightAction(matchButton));
		calendarButton.addActionListener(new HighlightAction(calendarButton));
		rankingButton.addActionListener(new HighlightAction(rankingButton));
		financeButton.addActionListener(new HighlightAction(financeButton));
		mapButton.addActionListener(new HighlightAction(mapButton));

		highlightActiveButton(matchButton);

		panel.add(matchButton);
		panel.add(calendarButton);
		panel.add(rankingButton);
		panel.add(financeButton);
		panel.add(mapButton);

		panel.add(Box.createVerticalGlue());

		return panel;
	}

	private JPanel buildBottomSection() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(SIDEBAR_BACKGROUND_COLOR);

		configureMenuButton(exitButton);
		panel.add(exitButton, BorderLayout.SOUTH);

		return panel;
	}

	private void configureMenuButton(JButton button) {
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(true);
		button.setOpaque(true);

		button.setBackground(SIDEBAR_BACKGROUND_COLOR);
		button.setForeground(BUTTON_TEXT_COLOR);
		button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

		button.setPreferredSize(new Dimension(200, 50));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		button.setMargin(new Insets(0, 20, 0, 10));
	}

	private class HighlightAction implements ActionListener {
		private JButton button;
		public HighlightAction(JButton button) {
			this.button = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			highlightActiveButton(button);
		}
	}

	private void initializeHighlightStrategies() {
		highlightStrategies.put("match", new ButtonHighlightStrategy(
				matchButton, menuButtons, SIDEBAR_BACKGROUND_COLOR, ACTIVE_BUTTON_BACKGROUND_COLOR));
		highlightStrategies.put("calendar", new ButtonHighlightStrategy(
				calendarButton, menuButtons, SIDEBAR_BACKGROUND_COLOR, ACTIVE_BUTTON_BACKGROUND_COLOR));
		highlightStrategies.put("ranking", new ButtonHighlightStrategy(
				rankingButton, menuButtons, SIDEBAR_BACKGROUND_COLOR, ACTIVE_BUTTON_BACKGROUND_COLOR));
		highlightStrategies.put("finance", new ButtonHighlightStrategy(
				financeButton, menuButtons, SIDEBAR_BACKGROUND_COLOR, ACTIVE_BUTTON_BACKGROUND_COLOR));
		highlightStrategies.put("map", new ButtonHighlightStrategy(
				mapButton, menuButtons, SIDEBAR_BACKGROUND_COLOR, ACTIVE_BUTTON_BACKGROUND_COLOR));
	}

	private void highlightActiveButton(JButton activeButton) {
		JButton[] buttons = {
			matchButton,
			calendarButton,
			rankingButton,
			financeButton,
			mapButton
		};

		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setBackground(SIDEBAR_BACKGROUND_COLOR);
		}

		activeButton.setBackground(ACTIVE_BUTTON_BACKGROUND_COLOR);
	}

	public void setActiveSection(String sectionName) {
		SidebarHighlightStrategy strategy = highlightStrategies.get(sectionName);
		if (strategy != null) {
			strategy.highlight();
		}
	}

	public JButton getMatchButton() { 
		return matchButton; 
	}
	public JButton getCalendarButton() { 
		return calendarButton; 
	}
	public JButton getRankingButton() { 
		return rankingButton; 
	}
	public JButton getFinanceButton() { 
		return financeButton; 
	}
	public JButton getMapButton() { 
		return mapButton; 
	}
	public JButton getExitButton() { 
		return exitButton; 
	}
}
