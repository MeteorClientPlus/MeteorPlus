package nekiplay.meteorplus.features.modules.combat.velocity.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityMode;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityModes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;

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

		if (packet instanceof ClientboundDamageEventPacket && ((ClientboundDamageEventPacket) packet).entityId() == mc.player.getId()) {
			canCancel = true;
		}

		if (((packet instanceof ClientboundSetEntityMotionPacket motionPacket && motionPacket.id() == mc.player.getId()) || packet instanceof ClientboundExplodePacket) && canCancel) {
			event.cancel();
			MeteorExecutor.execute(() -> {
               try { Thread.sleep(20); } catch (Exception ignore) { }

				mc.getConnection().send(new ServerboundMovePlayerPacket.PosRot(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision));
				mc.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, mc.player.blockPosition(), mc.player.getDirection().getOpposite()));
				canCancel = false;
            });
		}

	}
}
