package nekiplay.meteorplus.features.modules.integrations;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.MeteorPlusAddon;

public class WhereIsIt extends Module {
	public WhereIsIt() {
		super(MeteorPlusAddon.CATEGORYMODS, "where-is-it", "ChestTracker modification.");
	}

	public SettingGroup defaultGroup = settings.getDefaultGroup();

	public final Setting<Boolean> background = defaultGroup.add(new BoolSetting.Builder()
		.name("draw-background")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> suport_color_symbols = defaultGroup.add(new BoolSetting.Builder()
		.name("use-color-symbols")
		.defaultValue(true)
		.build()
	);

	public final Setting<SettingColor> visible_text_color = defaultGroup.add(new ColorSetting.Builder()
		.name("Visible-text-color")
		.visible(() -> !suport_color_symbols.get())
		.build()
	);

	public final Setting<SettingColor> notvisible_text_color = defaultGroup.add(new ColorSetting.Builder()
		.name("not-visible-text-color")
		.visible(() -> !suport_color_symbols.get())
		.build()
	);

	public final Setting<Double> y_offset = defaultGroup.add(new DoubleSetting.Builder()
		.name("y-offset")
		.description("change-y-offset.")
		.defaultValue(-1)
		.max(15)
		.min(-15)
		.sliderRange(-15, 15)
		.build()
	);

	public final Setting<Double> text_scale = defaultGroup.add(new DoubleSetting.Builder()
		.name("text-scale")
		.defaultValue(1)
		.max(15)
		.min(0)
		.sliderRange(0, 15)
		.build()
	);
}
