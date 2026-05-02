package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;

public class GrimNew extends NoSlowMode {
	public GrimNew() {
		super(NoSlowModes.Grim_New);
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

		InteractionHand hand = mc.player.getUsedItemHand();
		ClientPacketListener network = mc.getConnection();
		assert network != null;
		if (hand == InteractionHand.MAIN_HAND) {
            network.send(new ServerboundUseItemPacket(InteractionHand.OFF_HAND, 0, 0, 0));
		}
		else if (hand == InteractionHand.OFF_HAND) {
			network.send(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot() % 8 + 1));
			network.send(new ServerboundSetCarriedItemPacket(mc.player.getInventory().getSelectedSlot()));
		}
	}
}
