package gui.panel.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.util.HashMap;


import javax.swing.ImageIcon;
import javax.swing.JPanel;

import gui.panel.mapPanel.TeamZone;

public class TeamMapPanel extends JPanel {
	private static final String MAP_IMAGE_PATH = "src/test/map.png";
	private static final Color POINT_COLOR = new Color(210, 48, 48);
	private static final int TEAM_POINT_RADIUS = 6;
	private static final double SOURCE_IMAGE_WIDTH = 1000.0;
	private static final double SOURCE_IMAGE_HEIGHT = 667.0;
	private static final int PREFERRED_WIDTH = 900;
	private static final int PREFERRED_HEIGHT = 520;

	private Image mapImage;
	private HashMap<String, TeamZone> zones;
	private String selectedTeamName;
	private Runnable teamSelectionAction;

	public TeamMapPanel() {
		create();
		organize();
		actions();
	}

	private void create() {
		zones = new HashMap<String, TeamZone>();
		loadImage();
		createZones();
	}

	private void organize() {
		setOpaque(true);
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
	}

	private void actions() {
		addMouseListener(new MapMouseListener());
	}

	private void loadImage() {
		ImageIcon icon = new ImageIcon(MAP_IMAGE_PATH);
		mapImage = icon.getImage();
	}

	private void createZones() {
		zones.put("Brooklyn Nets", createZoneFromPixels(805, 249));
		zones.put("Philadelphia 76ers", createZoneFromPixels(746, 248));
		zones.put("New York Knicks", createZoneFromPixels(821, 209));
		zones.put("Toronto Raptors", createZoneFromPixels(766, 183));
		zones.put("Chicago Bulls", createZoneFromPixels(617, 269));
		zones.put("Cleveland Cavaliers", createZoneFromPixels(704, 270));
		zones.put("Detroit Pistons", createZoneFromPixels(674, 243));
		zones.put("Boston Celtics", createZoneFromPixels(853, 124));
		zones.put("Indiana Pacers", createZoneFromPixels(649, 291));
		zones.put("Milwaukee Bucks", createZoneFromPixels(607, 199));
		zones.put("Atlanta Hawks", createZoneFromPixels(691, 406));
		zones.put("Charlotte Hornets", createZoneFromPixels(602, 477));
		zones.put("Miami Heat", createZoneFromPixels(769, 529));
		zones.put("Orlando Magic", createZoneFromPixels(756, 481));
		zones.put("Washington Wizards", createZoneFromPixels(772, 284));
		zones.put("Denver Nuggets", createZoneFromPixels(393, 325));
		zones.put("Minnesota Timberwolves", createZoneFromPixels(525, 194));
		zones.put("Oklahoma City Thunder", createZoneFromPixels(508, 373));
		zones.put("Portland Trail Blazers", createZoneFromPixels(181, 172));
		zones.put("Utah Jazz", createZoneFromPixels(292, 276));
		zones.put("Golden State Warriors", createZoneFromPixels(142, 277));
		zones.put("Los Angeles Clippers", createZoneFromPixels(202, 370));
		zones.put("Los Angeles Lakers", createZoneFromPixels(166, 368));
		zones.put("Phoenix Suns", createZoneFromPixels(286, 419));
		zones.put("Sacramento Kings", createZoneFromPixels(186, 242));
		zones.put("Dallas Mavericks", createZoneFromPixels(499, 435));
		zones.put("Houston Rockets", createZoneFromPixels(538, 487));
		zones.put("Memphis Grizzlies", createZoneFromPixels(620, 383));
		zones.put("New Orleans Pelicans", createZoneFromPixels(603, 475));
		zones.put("San Antonio Spurs", createZoneFromPixels(475, 502));
	}

	private TeamZone createZoneFromPixels(int pixelX, int pixelY) {
		double xRatio = pixelX / SOURCE_IMAGE_WIDTH;
		double yRatio = pixelY / SOURCE_IMAGE_HEIGHT;
		return new TeamZone(xRatio, yRatio, TEAM_POINT_RADIUS);
	}

	private void selectTeamAtPosition(int mouseX, int mouseY) {
		if (mapImage == null) {
			return;
		}

		TeamMapPanel panel = this;
		int imageWidth = mapImage.getWidth(panel);
		int imageHeight = mapImage.getHeight(panel);

		double widthRatio = (double) panel.getWidth() / imageWidth;
		double heightRatio = (double) panel.getHeight() / imageHeight;
		double scale = Math.min(widthRatio, heightRatio);

		int drawWidth = (int) (imageWidth * scale);
		int drawHeight = (int) (imageHeight * scale);

		if (mouseX < 0 || mouseY < 0 || mouseX > drawWidth || mouseY > drawHeight) {
			return;
		}

		for (String teamName : zones.keySet()) {
			TeamZone zone = zones.get(teamName);

			if (zone.contains(mouseX, mouseY, drawWidth, drawHeight)) {
				selectedTeamName = teamName;
				repaint();
				if (teamSelectionAction != null) {
					teamSelectionAction.run();
				}
				return;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (mapImage == null) {
			return;
		}

		TeamMapPanel panel = this;
		int imageWidth = mapImage.getWidth(panel);
		int imageHeight = mapImage.getHeight(panel);

		double widthRatio = (double) panel.getWidth() / imageWidth;
		double heightRatio = (double) panel.getHeight() / imageHeight;
		double scale = Math.min(widthRatio, heightRatio);

		int drawWidth = (int) (imageWidth * scale);
		int drawHeight = (int) (imageHeight * scale);

		g.drawImage(mapImage, 0, 0, drawWidth, drawHeight, panel);

		for (String teamName : zones.keySet()) {
			TeamZone zone = zones.get(teamName);
			int centerX = zone.getX(drawWidth);
			int centerY = zone.getY(drawHeight);
			int radius = zone.getRadius();
			int diameter = radius * 2;

			if (teamName.equals(selectedTeamName)) {
				g.setColor(new Color(55, 132, 179));
			} else {
				g.setColor(POINT_COLOR);
			}
			g.fillOval(centerX - radius, centerY - radius, diameter, diameter);
		}
	}

	public void setSelectedTeamName(String selectedTeamName) {
		this.selectedTeamName = selectedTeamName;
		repaint();
	}

	public String getSelectedTeamName() {
		return selectedTeamName;
	}

	public void setTeamSelectionAction(Runnable teamSelectionAction) {
		this.teamSelectionAction = teamSelectionAction;
	}

	private class MapMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent event) {
			selectTeamAtPosition(event.getX(), event.getY());
		}

		@Override
		public void mousePressed(MouseEvent event) {
		}

		@Override
		public void mouseReleased(MouseEvent event) {
		}

		@Override
		public void mouseEntered(MouseEvent event) {
		}

		@Override
		public void mouseExited(MouseEvent event) {
		}
	}
}
