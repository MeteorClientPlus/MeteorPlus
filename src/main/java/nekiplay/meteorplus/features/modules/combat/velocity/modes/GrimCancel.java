package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class GrimCancel extends VelocityMode {
	public GrimCancel() {
		super(VelocityModes.Grim_Cancel);
	}

	private boolean canCancel = false;

	@Override
	public void onActivate() {
		canCancel = false;
	}

	@Override
	public void onDeactivate() {
		canCancel = false;
	}

	@Override
	public void onReceivePacket(PacketEvent.Receive event) {
		Packet<?> packet = event.packet;

		if (packet instanceof EntityDamageS2CPacket && ((EntityDamageS2CPacket) packet).entityId() == mc.player.getId()) {
			canCancel = true;
		}

		if (((packet instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket) packet).getEntityId() == mc.player.getId()) || packet instanceof ExplosionS2CPacket) && canCancel) {
			event.cancel();
			MeteorExecutor.execute(() -> {
               try { Thread.sleep(20); } catch (Exception ignore) { }

				mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
				mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, mc.player.getBlockPos(), mc.player.getHorizontalFacing().getOpposite()));
				canCancel = false;
            });
		}

	}
}
