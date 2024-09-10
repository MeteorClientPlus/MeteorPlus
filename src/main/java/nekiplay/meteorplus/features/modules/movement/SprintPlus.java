package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;

public class SprintPlus extends Module {
	public SprintPlus() {
		super(Categories.Movement, "sprint+", "Better sprint module.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final Setting<Boolean> allDirections = sgGeneral.add(new BoolSetting.Builder()
		.name("All-directions")
		.defaultValue(false)
		.build()
	);
	private final Setting<Boolean> ignoreBlindness = sgGeneral.add(new BoolSetting.Builder()
		.name("Ignore-blindness")
		.defaultValue(false)
		.build()
	);

	public boolean shouldSprintOmnidirectionally() { return isActive() && allDirections.get(); }

	public boolean shouldIgnoreBlindness() { return isActive() && ignoreBlindness.get(); }


}
