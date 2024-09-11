package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;

public class NCPStrict extends NoSlowMode {
	public NCPStrict() {
		super(NoSlowModes.NCP_Strict);
	}
	@Override
	public void onUse(PlayerUseMultiplierEvent event) {
		if (mc.player.isSneaking()) {
			event.setForward(settings.sneakForward.get().floatValue());
			event.setSideways(settings.sneakSideways.get().floatValue());
		}
		else if (mc.player.isUsingItem()) {
			event.setForward(settings.usingForward.get().floatValue());
			event.setSideways(settings.usingSideways.get().floatValue());
		}
		else {
			event.setForward(settings.otherForward.get().floatValue());
			event.setSideways(settings.otherSideways.get().floatValue());
		}

		if (mc.player.isUsingItem()) {
			ClientPlayNetworkHandler network = mc.getNetworkHandler();
			network.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, mc.player.getBlockPos(), Direction.DOWN));
		}
	}
}
