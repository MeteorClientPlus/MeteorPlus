package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;

public class GrimSkip extends VelocityMode {
	public GrimSkip() {
		super(VelocityModes.Grim_Skip);
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
		if (packet instanceof ClientboundDamageEventPacket && ((ClientboundDamageEventPacket) packet).entityId() == mc.player.getId()) {
			canCancel = true;
		}
		if (((packet instanceof ClientboundSetEntityMotionPacket motionPacket && motionPacket.id() == mc.player.getId()) || packet instanceof ClientboundExplodePacket) && canCancel) {
			skip = 6;
			event.cancel();
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		Packet<?> packet = event.packet;

		if (packet instanceof ServerboundMovePlayerPacket) {
			if (skip > 0) {
				skip--;
				event.cancel();
			}
		}
	}
}
