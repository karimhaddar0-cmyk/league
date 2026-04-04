package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import gui.panel.common.DashboardPanelUtil;

public class LoadingDashboard extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 28);
	private static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

	private JLabel titleLabel;
	private JLabel detailLabel;
	private JProgressBar progressBar;

	public LoadingDashboard() {
		create();
		organize();
	}

	private void create() {
		titleLabel = new JLabel("Chargement de la simulation", SwingConstants.CENTER);
		titleLabel.setFont(TITLE_FONT);

		detailLabel = new JLabel("Initialisation de la saison en cours...", SwingConstants.CENTER);
		detailLabel.setFont(TEXT_FONT);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(320, 18));
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(DashboardPanelUtil.DASHBOARD_BACKGROUND_COLOR);

		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(120, 80, 120, 80));

		titleLabel.setAlignmentX(CENTER_ALIGNMENT);
		detailLabel.setAlignmentX(CENTER_ALIGNMENT);
		progressBar.setAlignmentX(CENTER_ALIGNMENT);

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(titleLabel);
		centerPanel.add(Box.createVerticalStrut(18));
		centerPanel.add(detailLabel);
		centerPanel.add(Box.createVerticalStrut(24));
		centerPanel.add(progressBar);
		centerPanel.add(Box.createVerticalGlue());

		add(centerPanel, BorderLayout.CENTER);
	}
}
