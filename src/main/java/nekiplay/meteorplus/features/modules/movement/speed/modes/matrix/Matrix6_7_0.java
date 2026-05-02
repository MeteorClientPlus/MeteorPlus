package nekiplay.meteorplus.features.modules.movement.speed.modes.matrix;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedMode;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedModes;
import nekiplay.meteorplus.utils.MovementUtils;

public class Matrix6_7_0 extends SpeedMode {
	public Matrix6_7_0() {
		super(SpeedModes.Matrix_6dot7dot0);
	}

	private int noVelocityY = 0;

	@Override
	public void onDeactivate() {
		mc.player.getAbilities().setFlyingSpeed(0.02f);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		work();
	}
	@Override
	public void onTickEventPost(TickEvent.Post event) {
		//work();
	}

	public void onReceivePacket(PacketEvent.Receive event) {
		if (event.packet instanceof ClientboundSetEntityMotionPacket velocity) {
			if (mc.player != null && mc.level != null && mc.level.getEntity(velocity.getId()) != null) {
				if (mc.player == mc.level.getEntity(velocity.getId()))
					noVelocityY = 10;
			}
		}
	}

	private void work() {
		if (!mc.player.onGround() && noVelocityY <= 0) {
			if (mc.player.getDeltaMovement().y > 0) {
				mc.player.getDeltaMovement().add(0, -0.0005, 0);
			}
			mc.player.getDeltaMovement().add(0, -0.0094001145141919810, 0);
		}
		if (!mc.player.onGround() && noVelocityY < 8) {
			if (MovementUtils.getSpeed() < 0.2177 && noVelocityY < 8) {
				MovementUtils.strafe(0.2177f);
			}
		}
		if (Math.abs(mc.player.getAbilities().getFlyingSpeed()) < 0.1) {
			mc.player.getAbilities().setFlyingSpeed(0.026f);
		}
		else {
			mc.player.getAbilities().setFlyingSpeed(0.0247f);
		}
		if (mc.player.onGround() && PlayerUtils.isMoving()) {
			mc.options.keyJump.setDown(false);
			mc.player.jumpFromGround();
			IVec3 v = (IVec3) mc.player.getDeltaMovement();
			v.meteor$setY(0.41050001145141919810);
			if (Math.abs(mc.player.getAbilities().getFlyingSpeed()) < 0.1) {
				MovementUtils.strafe(MovementUtils.getSpeed());
			}
		}
		if (!PlayerUtils.isMoving()) {
			IVec3 v = (IVec3) mc.player.getDeltaMovement();
			v.meteor$setXZ(0, 0);
		}
	}
}
