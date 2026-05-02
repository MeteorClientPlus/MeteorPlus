package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class Vulcan277 extends NoFallMode {

	public Vulcan277() {
		super(NoFallModes.Vulcan_2dot7dot7);
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof ServerboundMovePlayerPacket) {
			ServerboundMovePlayerPacket packet = (ServerboundMovePlayerPacket)event.packet;
			PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor)packet;

			if (mc.player.fallDistance > 7.0) {
				accessor.meteor$setOnGround(true);
				mc.player.fallDistance = 0f;
				var vel = mc.player.getDeltaMovement();
				mc.player.setDeltaMovement(vel.x, 0, vel.z);
			}
		}
	}
}
