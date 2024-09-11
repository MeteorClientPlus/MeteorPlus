package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class GrimNew extends NoSlowMode {
	public GrimNew() {
		super(NoSlowModes.Grim_New);
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

		Hand hand = mc.player.getActiveHand();
		ClientPlayNetworkHandler network = mc.getNetworkHandler();
		assert network != null;
		if (hand == Hand.MAIN_HAND) {
            network.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0, 0, 0));
		}
		else if (hand == Hand.OFF_HAND) {
			network.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
			network.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
		}
	}
}
