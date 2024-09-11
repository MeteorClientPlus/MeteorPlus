package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.mixin.minecraft.entity.PlayerMoveC2SPacketAccessor;

public class No_Ground extends NoFallMode {
	/*
	Tested on: oldfrog.org (NCP)
	 */
	public No_Ground() {
		super(NoFallModes.No_Ground);
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof IPlayerMoveC2SPacket move) {
			PlayerMoveC2SPacketAccessor move2 = (PlayerMoveC2SPacketAccessor) move;
			if (move2.getOnGround()) {
				move2.setOnGround(false);
			}
		}
	}
}
