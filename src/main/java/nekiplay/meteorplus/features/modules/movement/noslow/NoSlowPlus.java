package nekiplay.meteorplus.features.modules.movement.noslow;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.modes.*;

public class NoSlowPlus extends Module {
	public NoSlowPlus() {
		super(Categories.Movement, "no-slow+", "Remove or increase slowness.");
	}
	public SettingGroup defaultGroup = settings.getDefaultGroup();

	private NoSlowMode currentMode;

	public final Setting<NoSlowModes> mode = defaultGroup.add(new EnumSetting.Builder<NoSlowModes>()
		.name("mode")
		.description("The method of applying no slow.")
		.defaultValue(NoSlowModes.Vanila)
		.onModuleActivated(spiderModesSetting -> onModeChanged(spiderModesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);


	public SettingGroup usingItemGroup = settings.createGroup("Using item");
	public SettingGroup sneakGroup = settings.createGroup("Sneak");
	public SettingGroup otherGroup = settings.createGroup("Other");

	public final Setting<Double> usingForward = usingItemGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	public final Setting<Double> usingSideways = usingItemGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	public final Setting<Double> sneakForward = sneakGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	public final Setting<Double> sneakSideways = sneakGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	public final Setting<Double> otherForward = otherGroup.add(new DoubleSetting.Builder()
		.name("forward-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	public final Setting<Double> otherSideways = otherGroup.add(new DoubleSetting.Builder()
		.name("sideways-multiplier")
		.defaultValue(1)
		.min(0.2)
		.sliderRange(0.2, 1)
		.visible(() -> mode.get() != NoSlowModes.Matrix)
		.build()
	);

	private void onModeChanged(NoSlowModes mode) {
		switch (mode) {
			case Vanila -> currentMode = new Vanila();
			case NCP_Strict -> currentMode = new NCPStrict();
			case Grim_1dot8 -> currentMode = new Grim();
			case Grim_New -> currentMode = new GrimNew();
			case Matrix -> currentMode = new Matrix();
		}
	}

	@EventHandler
	private void onUse(PlayerUseMultiplierEvent event) {
		currentMode.onUse(event);
	}

	@EventHandler
	private void onTickEventPre(TickEvent.Pre event) {
		currentMode.onTickEventPre(event);
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}
}
