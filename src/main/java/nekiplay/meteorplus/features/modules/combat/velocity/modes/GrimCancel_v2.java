package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

public class GrimCancel_v2 extends VelocityMode {
	public GrimCancel_v2() {
		super(VelocityModes.Grim_Cancel_v2);
	}

	private int skip = 0;

	private boolean canCancel = false;

	@Override
	public void onActivate() {
		canCancel = false;
		skip = 0;
	}

	@Override
	public void onDeactivate() {
		canCancel = false;
		skip = 0;
	}

	@Override
	public void onReceivePacket(PacketEvent.Receive event) {
		Packet<?> packet = event.packet;

		if (((packet instanceof ClientboundSetEntityMotionPacket motionPacket && motionPacket.id() == mc.player.getId()) || packet instanceof ClientboundExplodePacket) && canCancel) {
			event.cancel();
			canCancel = true;
		} else if (packet instanceof ClientboundPlayerPositionPacket) {
			skip = 3;
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		Packet<?> packet = event.packet;

		if (packet instanceof ServerboundMovePlayerPacket) {
			skip--;
			if (canCancel) {
				if (skip <= 0) {
					mc.getConnection().send(new ServerboundMovePlayerPacket.PosRot(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision));
					mc.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, mc.player.blockPosition(), Direction.UP));
				}
			}
		}
	}
}
