package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

public class Grim extends NoSlowMode {
	public Grim() {
		super(NoSlowModes.Grim_1dot8);
	}

	@Override
	public void onUse(PlayerUseMultiplierEvent event) {
		if (mc.player.isShiftKeyDown()) {
			event.setForward(settings.sneakForward.get().floatValue());
			event.setSideways(settings.sneakSideways.get().floatValue());
		} else if (mc.player.isUsingItem()) {
			event.setForward(settings.usingForward.get().floatValue());
			event.setSideways(settings.usingSideways.get().floatValue());
		} else {
			event.setForward(settings.otherForward.get().floatValue());
			event.setSideways(settings.otherSideways.get().floatValue());
		}

		if (mc.player.isUsingItem()) {
			ClientPacketListener network = mc.getConnection();
			assert network != null;
			network.send(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot() % 8 + 1));
			network.send(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot()));
		}
	}
}
