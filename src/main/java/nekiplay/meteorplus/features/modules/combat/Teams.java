package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;

import java.nio.charset.CharsetEncoder;

public class Teams extends Module {
	public Teams() {
		super(Categories.Combat, "teams", "Check if [entity] is in your own team using scoreboard, name color or team prefix.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final Setting<Boolean> scoreBoardTeam = sgGeneral.add(new BoolSetting.Builder()
		.name("Scoreboard-Team")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> colorTeam = sgGeneral.add(new BoolSetting.Builder()
		.name("color")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> gommeSkyWars = sgGeneral.add(new BoolSetting.Builder()
		.name("GommeHD-SkyWars")
		.defaultValue(false)
		.build()
	);
	public boolean isInYourTeam(Entity entity) {
		if (entity instanceof LivingEntity) {
			return isInYourTeam((LivingEntity) entity);
		}
		return false;
	}
	public boolean isInYourTeam(LivingEntity entity) {
		LocalPlayer player = mc.player;
		if (player == null) { return false; }

		if (!isActive()) { return false; }

		if (scoreBoardTeam.get() && player.getTeam() != null && entity.getTeam() != null && player.isAlliedTo(entity.getTeam())) {
			return true;
		}

		Component displayName = player.getDisplayName();
		if (gommeSkyWars.get() && displayName != null && entity.getDisplayName() != null) {
			String targetName = entity.getDisplayName().getString().replaceAll("§r", "");
			String clientName = displayName.getString().replaceAll("§r", "");

			if (targetName.startsWith("T") && clientName.startsWith("T"))

				if (Character.isDigit(targetName.charAt(1)) && Character.isDigit(clientName.charAt(1)))
					return targetName.charAt(1) == clientName.charAt(1);
		}

		if (colorTeam.get() && displayName != null && entity.getDisplayName() != null) {
			String targetName = entity.getDisplayName().getString().replaceAll("§r", "");
			String clientName = displayName.getString().replaceAll("§r", "");

			return targetName.startsWith("§" + clientName.charAt(1));
		}

		return false;
	}
}
