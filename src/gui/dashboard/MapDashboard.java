package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import gui.panel.common.BuildBox;
import gui.panel.common.SectionTitle;
import gui.panel.mapPanel.MapPanel;
/**
 * Dashboard dédié à la page Carte.
 */
public class MapDashboard extends JPanel {
	private static final int DASHBOARD_SPACING = 16;
	private static final int HEADER_HEIGHT = 50;
	private static final int LEFT_COLUMN_WIDTH = 270;
	private static final int RIGHT_COLUMN_WIDTH = 280;
	private static final int TEAM_DETAILS_HEIGHT = 190;
	private static final Color BACKGROUND_COLOR = new Color(247, 248, 250);

	public MapDashboard() {
		organize();
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		JPanel content = new JPanel(new BorderLayout(DASHBOARD_SPACING, DASHBOARD_SPACING));
		content.setOpaque(false);
		content.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, DASHBOARD_SPACING, DASHBOARD_SPACING, DASHBOARD_SPACING));
		return content;
	}

	private JPanel buildHeader(){
		JPanel header = new SectionTitle("Carte des equipes", "Distribution geographique");
		header.setPreferredSize(new Dimension(LEFT_COLUMN_WIDTH, HEADER_HEIGHT));
		return header;
	}

	private JPanel buildBody() {
		JPanel body = new JPanel(new BorderLayout(DASHBOARD_SPACING, DASHBOARD_SPACING));
		body.setOpaque(false);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildCenterColumn() {
		return new BuildBox("LOCALISATION DES FRANCHISES", "", new MapPanel());
	}

	private JPanel buildRightColumn(){
		JPanel column = new JPanel(new BorderLayout(0, 12));
		column.setOpaque(false);
		column.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 10));
		
		JPanel teamDetailsBox = new BuildBox("Détails de l'équipe", "Informations détaillées sur l'équipe sélectionnée", "INFOS ÉQUIPE");
		teamDetailsBox.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, TEAM_DETAILS_HEIGHT));

		column.add(teamDetailsBox, BorderLayout.NORTH);
		column.add(new BuildBox("Joueur de l'équipe", "", "JOUEURS"), BorderLayout.CENTER);

		return column;
	}

}
