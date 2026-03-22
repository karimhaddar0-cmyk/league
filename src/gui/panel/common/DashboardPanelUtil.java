package gui.panel.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class DashboardPanelUtil {

	public static JPanel createContentPanel(int spacing) {
		JPanel content = new JPanel(new BorderLayout(spacing, spacing));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(0, spacing, spacing, spacing));
		return content;
	}

	public static JPanel createBodyPanel(int horizontalSpacing, int verticalSpacing) {
		JPanel body = new JPanel(new BorderLayout(horizontalSpacing, verticalSpacing));
		body.setOpaque(false);
		return body;
	}

	public static JPanel createGridColumn(int rows, int columns, int horizontalGap, int verticalGap, int width) {
		JPanel column = new JPanel(new GridLayout(rows, columns, horizontalGap, verticalGap));
		column.setOpaque(false);
		column.setPreferredSize(new Dimension(width, 10));
		return column;
	}

	public static JPanel createRightColumn(int width, int spacing) {
		JPanel column = new JPanel(new BorderLayout(0, spacing));
		column.setOpaque(false);
		column.setPreferredSize(new Dimension(width, 10));
		return column;
	}
}
