package nekiplay.meteorplus.features.modules.misc;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

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

	public Component getPrefix() {
		MutableComponent value = Component.literal(prefix.get());
		MutableComponent prefix = Component.literal("");
		value.setStyle(value.getStyle().withColor(TextColor.fromRgb(prefixColor.get().getPacked())));
		prefix.setStyle(prefix.getStyle().applyFormat(ChatFormatting.GRAY))
			.append(Component.literal("["))
			.append(value)
			.append(Component.literal("] "));
		return prefix;
	}
}
