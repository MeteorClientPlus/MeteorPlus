package nekiplay.meteorplus.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ElytraUtils {
	public static void startFly() {
		if (mc.player != null && mc.player.connection != null) {
			mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
		}
	}

	public static void fakeInventoryOpen(boolean open) {
		if (mc.player != null && mc.player.connection != null) {
			if (open)
				mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
			else
				mc.player.connection.send(new ServerboundContainerClosePacket(0));
		}
	}
}
