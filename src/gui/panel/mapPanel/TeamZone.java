package gui.panel.mapPanel;

public class TeamZone {

	private final double xRatio;
	private final double yRatio;
	private final int radius;

	public TeamZone(double xRatio, double yRatio, int radius) {
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		this.radius = radius;
	}

	public int getX(int width) {
		return (int) (xRatio * width);
	}

	public int getY(int height) {
		return (int) (yRatio * height);
	}

	public int getRadius() {
		return radius;
	}

	public boolean contains(int mouseX, int mouseY, int width, int height) {
		int centerX = getX(width);
		int centerY = getY(height);
		int deltaX = mouseX - centerX;
		int deltaY = mouseY - centerY;

		return deltaX * deltaX + deltaY * deltaY <= radius * radius;
	}
}
