package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.mixin.minecraft.entity.PlayerMoveC2SPacketAccessor;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class No_Ground_Elytra extends NoFallMode {
	/*
	Tested on: oldfrog.org (NCP)
	 */
	public No_Ground_Elytra() {
		super(NoFallModes.No_Ground_Elytra);
	}


	@Override
	public void onActivate() {
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (!elytra.found()) {
			ChatUtils.error("Elytra not found, this bypass for working need elytra");
		}
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {

		if (mc.player.fallDistance > 2) {
			FindItemResult elytra = InvUtils.find(Items.ELYTRA);
			if (elytra.found()) {
				int slot = elytra.slot();
				if (mc.player.getInventory().getArmorStack(2).getItem() != Items.ELYTRA) {
					InvUtils.move().from(slot).toArmor(2);
				}
			}
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof IPlayerMoveC2SPacket move) {
			PlayerMoveC2SPacketAccessor move2 = (PlayerMoveC2SPacketAccessor) move;
			if (move2.getOnGround()) {
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
				move2.setOnGround(false);
			}
		}
	}
}
