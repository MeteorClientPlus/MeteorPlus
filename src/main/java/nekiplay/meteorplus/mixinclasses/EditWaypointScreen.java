package nekiplay.meteorplus.mixinclasses;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EditWaypointScreen extends EditSystemScreen<Waypoint> {
	private Runnable reload;
	public EditWaypointScreen(GuiTheme theme, Waypoint value, Runnable reload) {
		super(theme, value, reload);
		this.reload = reload;
	}

	@Override
	public Waypoint create() {
		return new Waypoint.Builder()
			.pos(mc.player.getBlockPos().up(2))
			.dimension(PlayerUtils.getDimension())
			.build();
	}

	@Override
	public boolean save() {

		boolean added = !isNew || Waypoints.get().add(value);
		return added;

	}

	@Override
	public Settings getSettings() {
		return value.settings;
	}
}
