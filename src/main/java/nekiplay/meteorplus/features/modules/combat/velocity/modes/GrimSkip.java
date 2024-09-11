package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

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

		if (((packet instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) packet).getId() == mc.player.getId()) || packet instanceof ExplosionS2CPacket) && canCancel) {
			skip = 6;
			event.cancel();
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		Packet<?> packet = event.packet;

		if (packet instanceof PlayerMoveC2SPacket) {
			if (skip > 0) {
				skip--;
				event.cancel();
			}
		}
	}
}
