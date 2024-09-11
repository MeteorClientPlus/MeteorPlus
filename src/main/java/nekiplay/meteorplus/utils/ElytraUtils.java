package nekiplay.meteorplus.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ElytraUtils {
	public static void startFly() {
		if (mc.player != null && mc.player.networkHandler != null) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
		}
	}

	public static void fakeInventoryOpen(boolean open) {
		if (mc.player != null && mc.player.networkHandler != null) {
			if (open)
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
			else
				mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
		}
	}
}
