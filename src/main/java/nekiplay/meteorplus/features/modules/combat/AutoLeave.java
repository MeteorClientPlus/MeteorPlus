package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.orbit.EventHandler;
import java.util.Objects;

public class AutoLeave extends Module {
	public AutoLeave() {
		super(Categories.Combat, "auto-leave", "Automatically logs out from the server when someone enters your render distance.");
	}
	private final SettingGroup ALSettings = settings.getDefaultGroup();
	private final Setting<Boolean> visualRangeIgnoreFriends = ALSettings.add(new BoolSetting.Builder()
		.name("ignore-friends")
		.description("Ignores friends.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> AutoDisable = ALSettings.add(new BoolSetting.Builder()
		.name("auto-disable")
		.description("Disables function after player detect.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> Command = ALSettings.add(new BoolSetting.Builder()
		.name("command")
		.description("Send command instead of leave.")
		.defaultValue(false)
		.build()
	);

	private final Setting<String> command_str = ALSettings.add(new StringSetting.Builder()
		.name("command:")
		.description("Send command in chat.")
		.defaultValue("/spawn")
		.visible(Command::get)
		.build()
	);

	@EventHandler
	public void onEntityAdded(EntityAddedEvent event) {
		if (mc.player == null) return;
		if (visualRangeIgnoreFriends.get()) {
			if (event.entity.isPlayer() && !Friends.get().isFriend((PlayerEntity) event.entity) && !Objects.equals(event.entity.getName(), mc.player.getName()) && !Objects.equals(event.entity.getName(), "FreeCamera")) {
				if (Command.get()) {
					ChatUtils.sendPlayerMsg(command_str.get());
					info((String.format("player §c%s§r was detected", event.entity.getName())));
				} else {
					mc.world.disconnect();
					mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal(String.format("[§dAuto Leaeve§r] player %s was detected", event.entity.getName()))));
				}
			if (AutoDisable.get()) this.toggle();
			}
		}
		else if (event.entity.isPlayer()){
				mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal(String.format("[§dAuto Leaeve§r] player %s was detected", event.entity.getName()))));
				if (AutoDisable.get()) this.toggle();
		}
	}
}
