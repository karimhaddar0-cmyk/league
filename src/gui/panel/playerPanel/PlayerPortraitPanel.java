package gui.panel.playerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.player.Player;

public class PlayerPortraitPanel extends JPanel {
	private static final String PORTRAIT_FOLDER_PATH = "src/test/portraits/";
	private static final int DEFAULT_PORTRAIT_WIDTH = 80;
	private static final int DEFAULT_PORTRAIT_HEIGHT = 58;

	private JLabel portraitLabel;
	private Player player;
	private int portraitWidth;
	private int portraitHeight;

	public PlayerPortraitPanel() {
		this(null, DEFAULT_PORTRAIT_WIDTH, DEFAULT_PORTRAIT_HEIGHT);
	}

	public PlayerPortraitPanel(Player player, int portraitWidth, int portraitHeight) {
		this.player = player;
		this.portraitWidth = portraitWidth;
		this.portraitHeight = portraitHeight;
		create();
		organize();
		updatePortrait();
	}

	private void create() {
		portraitLabel = new JLabel("", JLabel.CENTER);
		portraitLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		portraitLabel.setForeground(new Color(80, 80, 80));
	}

	private void organize() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setPreferredSize(new Dimension(portraitWidth, portraitHeight));
		add(portraitLabel, BorderLayout.CENTER);
	}

	public void setPlayer(Player player) {
		this.player = player;
		updatePortrait();
	}

	private void updatePortrait() {
		File portraitFile = new File(PORTRAIT_FOLDER_PATH + buildFileName());
		if (!portraitFile.exists()) {
			showFallbackLabel();
			return;
		}

		ImageIcon icon = new ImageIcon(portraitFile.getPath());
		if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
			showFallbackLabel();
			return;
		}

		Image scaledImage = icon.getImage().getScaledInstance(portraitWidth, portraitHeight, Image.SCALE_SMOOTH);
		portraitLabel.setText("");
		portraitLabel.setIcon(new ImageIcon(scaledImage));
	}

	private void showFallbackLabel() {
		portraitLabel.setIcon(null);
		portraitLabel.setText(buildAbbreviation());
	}

	private String buildFileName() {
		if (player == null || player.getId() == null || player.getId().isEmpty()) {
			return "";
		}
		return player.getId() + ".png";
	}

	private String buildAbbreviation() {
		if (player == null || player.getName() == null || player.getName().isEmpty()) {
			return "--";
		}

		String[] words = player.getName().split(" ");
		String abbreviation = "";
		for (int i = 0; i < words.length && abbreviation.length() < 2; i++) {
			if (!words[i].isEmpty()) {
				abbreviation += words[i].substring(0, 1).toUpperCase();
			}
		}

		while (abbreviation.length() < 2) {
			abbreviation += "X";
		}
		return abbreviation;
	}
}
