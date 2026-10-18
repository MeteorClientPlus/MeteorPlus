package nekiplay.meteorplus.utils;

import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ElytraUtils {
	public static void startFly() {
		if (mc.player != null && mc.player.connection != null) {
			mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
		}
	}
}
