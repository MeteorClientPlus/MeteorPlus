package nekiplay.meteorplus.mixinclasses;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;

public class WIcon extends WWidget {
	private final Waypoint waypoint;

	public WIcon(Waypoint waypoint) {
		this.waypoint = waypoint;
	}

	@Override
	protected void onCalculateSize() {
		double s = theme.scale(32);

		width = s;
		height = s;
	}

	@Override
	protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
		renderer.post(() -> waypoint.renderIcon(x, y, 1, width));
	}
}
