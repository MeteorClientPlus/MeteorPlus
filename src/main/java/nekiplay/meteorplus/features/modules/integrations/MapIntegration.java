package nekiplay.meteorplus.features.modules.integrations;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.MixinPlugin;

public class MapIntegration extends Module {
	public MapIntegration() {
		super(MeteorPlusAddon.CATEGORYMODS, "Mini-map", "Improvements for mods on mini-maps.");
	}

	private final SettingGroup baritoneIntegration = settings.createGroup("Baritone");

	public final Setting<Boolean> baritoneGoto = baritoneIntegration.add(new BoolSetting.Builder()
		.name("Baritone support")
		.description("Moving in baritone at the selected location.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> baritoneElytra = baritoneIntegration.add(new BoolSetting.Builder()
		.name("Baritone elytra")
		.description("Elytra to location.")
		.defaultValue(true)
		.visible(() -> {
			boolean allow = false;
			for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
				if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
					allow = true;
					break;
				}
			}
			if (allow) {
				allow = baritoneGoto.get();
			}
			return allow;
		})
		.build()
	);

	private final SettingGroup fullMap = settings.createGroup("Full map");

	public final Setting<Boolean> showBlock = fullMap.add(new BoolSetting.Builder()
		.name("Show block")
		.description("Shows the name of the block in the clicked position.")
		.visible(() -> MixinPlugin.isXaeroWorldMapresent)
		.defaultValue(true)
		.build()
	);
}
