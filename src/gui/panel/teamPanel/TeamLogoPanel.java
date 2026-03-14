package gui.panel.teamPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TeamLogoPanel extends JPanel {
	private static final String LOGO_FOLDER_PATH = "src/test/nba_logos/";
	private static final int DEFAULT_LOGO_SIZE = 64;

	private JLabel logoLabel;
	private String teamName;
	private int logoSize;

	public TeamLogoPanel() {
		this("", DEFAULT_LOGO_SIZE);
	}

	public TeamLogoPanel(String teamName, int logoSize) {
		this.teamName = teamName;
		this.logoSize = logoSize;
		create();
		organize();
		updateLogo();
	}

	private void create() {
		logoLabel = new JLabel("", JLabel.CENTER);
		logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		logoLabel.setForeground(new Color(80, 80, 80));
	}

	private void organize() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setPreferredSize(new Dimension(logoSize, logoSize));
		add(logoLabel, BorderLayout.CENTER);
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
		updateLogo();
	}

	private void updateLogo() {
		File logoFile = new File(LOGO_FOLDER_PATH + buildFileName(teamName));
		if (!logoFile.exists()) {
			showFallbackLabel();
			return;
		}

		ImageIcon icon = new ImageIcon(logoFile.getPath());
		if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
			showFallbackLabel();
			return;
		}
		Image scaledImage = icon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
		logoLabel.setText("");
		logoLabel.setIcon(new ImageIcon(scaledImage));
	}

	private void showFallbackLabel() {
		logoLabel.setIcon(null);
		logoLabel.setText(buildAbbreviation(teamName));
	}

	private String buildFileName(String teamName) {
		if (teamName == null || teamName.isEmpty()) {
			return "";
		}
		if ("Los Angeles Clippers".equals(teamName)) {
			return "LA_Clippers.png";
		}
		return teamName.replace(" ", "_") + ".png";
	}

	private String buildAbbreviation(String teamName) {
		if (teamName == null || teamName.isEmpty()) {
			return "---";
		}
		String[] words = teamName.split(" ");
		String abbreviation = "";
		for (int i = 0; i < words.length && abbreviation.length() < 3; i++) {
			if (!words[i].isEmpty()) {
				abbreviation += words[i].substring(0, 1).toUpperCase();
			}
		}
		while (abbreviation.length() < 3) {
			abbreviation += "X";
		}
		return abbreviation;
	}
}
