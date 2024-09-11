package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

		if (((packet instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) packet).getId() == mc.player.getId()) || packet instanceof ExplosionS2CPacket) && canCancel) {
			event.cancel();
			canCancel = true;
		}
		else if (packet instanceof PlayerPositionLookS2CPacket) {
			skip = 3;
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		Packet<?> packet = event.packet;

		if (packet instanceof PlayerMoveC2SPacket) {
			skip--;
			if (canCancel) {
				if (skip <= 0) {
					mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
					mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, mc.player.getBlockPos(), Direction.UP));
				}
			}
		}
	}
}
