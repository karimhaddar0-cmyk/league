package gui.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import gui.panel.common.BuildBox;
import gui.panel.common.DashboardPanelUtil;
import gui.panel.common.SectionTitle;

/**
 * Dashboard dédié à la page Classement.
 */
public class RankingDashboard extends JPanel {

	private static final int IDEAL_DASHBOARD_SPACING = 16;
	private static final int IDEAL_DASHBOARD_HEADER_HEIGHT = 50;
	private static final int IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH = 340;
	private static final int IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH = 300;
	private static final Color IDEAL_DASHBOARD_BACKGROUND_COLOR = new Color(247, 248, 250);

	public RankingDashboard() {
		organize();
	}

	private void organize() {
		setLayout(new BorderLayout());
		setBackground(IDEAL_DASHBOARD_BACKGROUND_COLOR);

		JPanel content = buildContentPanel();
		content.add(buildHeader(), BorderLayout.NORTH);
		content.add(buildBody(), BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
	}

	private JPanel buildContentPanel() {
		return DashboardPanelUtil.createContentPanel(IDEAL_DASHBOARD_SPACING);
	}

	private JPanel buildHeader() {
		JPanel header = new SectionTitle("CLASSEMENT GÉNÉRAL", "Conférence Est - Saison régulière");
		header.setPreferredSize(new Dimension(IDEAL_DASHBOARD_LEFT_COLUMN_WIDTH, IDEAL_DASHBOARD_HEADER_HEIGHT));
		return header;
	}

	private JPanel buildBody() {
		JPanel body = DashboardPanelUtil.createBodyPanel(IDEAL_DASHBOARD_SPACING, IDEAL_DASHBOARD_SPACING);
		body.add(buildCenterColumn(), BorderLayout.CENTER);
		body.add(buildRightColumn(), BorderLayout.EAST);
		return body;
	}

	private JPanel buildCenterColumn() {
		return new BuildBox("CLASSEMENT COMPLET", "12 équipes", "TABLEAU CLASSEMENT");//! À changer le string par un jpanel quand on aura la fonctionnalité
	}

	private JPanel buildRightColumn() {
		JPanel column = DashboardPanelUtil.createGridColumn(2, 1, 0, 12, IDEAL_DASHBOARD_RIGHT_COLUMN_WIDTH);

		column.add(new BuildBox("ZONE PLAYOFFS", "Équipes qualifiées", "PLAYOFFS"));//! À changer le string par un jpanel quand on aura la fonctionnalité
		column.add(new BuildBox("PERFORMANCES", "Forme récente", "STATISTIQUES"));//! À changer le string par un jpanel quand on aura la fonctionnalité

		return column;
	}
}
