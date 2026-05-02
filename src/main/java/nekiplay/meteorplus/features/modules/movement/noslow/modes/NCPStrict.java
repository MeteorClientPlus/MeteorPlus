package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.core.Direction;

public class NCPStrict extends NoSlowMode {
	public NCPStrict() {
		super(NoSlowModes.NCP_Strict);
	}
	@Override
	public void onUse(PlayerUseMultiplierEvent event) {
		if (mc.player.isShiftKeyDown()) {
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
			ClientPacketListener network = mc.getConnection();
			network.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, mc.player.blockPosition(), Direction.DOWN));
		}
	}
}
