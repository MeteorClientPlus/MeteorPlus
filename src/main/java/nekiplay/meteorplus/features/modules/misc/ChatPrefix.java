package nekiplay.meteorplus.features.modules.misc;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ChatPrefix extends Module {
	public ChatPrefix() {
		super(Categories.Misc, "meteor+-chat-prefix", "prefix for enabling and disabling Meteor+ modules.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
		.name("prefix")
		.description("Which prefix to be displayed for Meteor+ modules.")
		.defaultValue("Meteor+")
		.onChanged(reload -> setPrefixes())
		.build()
	);

	private final Setting<SettingColor> prefixColor = sgGeneral.add(new ColorSetting.Builder()
		.name("color")
		.description("Which color to use for the prefix.")
		.defaultValue(new SettingColor(0, 220, 4, 255))
		.build()
	);

	@Override
	public void onActivate() {
		setPrefixes();
	}

	@Override
	public void onDeactivate() {
		ChatUtils.unregisterCustomPrefix("nekiplay.meteorplus.features.modules");
	}

	public void setPrefixes() {
		if (isActive()) {
			ChatUtils.registerCustomPrefix("nekiplay.meteorplus.features.modules", this::getPrefix);
		}
	}

	public Text getPrefix() {
		MutableText value = Text.literal(prefix.get());
		MutableText prefix = Text.literal("");
		value.setStyle(value.getStyle().withColor(TextColor.fromRgb(prefixColor.get().getPacked())));
		prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY))
			.append(Text.literal("["))
			.append(value)
			.append(Text.literal("] "));
		return prefix;
	}
}
