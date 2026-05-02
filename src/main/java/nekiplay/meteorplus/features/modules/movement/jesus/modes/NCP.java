package nekiplay.meteorplus.features.modules.movement.jesus.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusMode;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusModes;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.Vec3;

public class NCP extends JesusMode {
	public NCP() {
		super(JesusModes.NCP);
	}
	float newSpeed = 0;
	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		mc.player.setSprinting(false);
		if (!mc.player.isInLiquid()) {
			return;
		}
		Vec3 velocity = mc.player.getDeltaMovement();

		if (mc.options.keyJump.isDown() && !mc.player.isShiftKeyDown() && !(mc.level.getBlockState(mc.player.blockPosition().offset(0, 1, 0)).getBlock() instanceof AirBlock)) {
			mc.player.setDeltaMovement(velocity.x, 0.12, velocity.z);
		}
		velocity = mc.player.getDeltaMovement();
		if (mc.options.keyShift.isDown()) {
			mc.player.setDeltaMovement(velocity.x, -0.12, velocity.z);
		}
		velocity = mc.player.getDeltaMovement();
		if (mc.level.getBlockState(mc.player.blockPosition().offset(0, 1, 0)).getBlock() instanceof AirBlock && mc.options.keyJump.isDown()) {
			mc.player.setShiftKeyDown(true);
			mc.player.setDeltaMovement(velocity.x, 0.12, velocity.z);
		}

		float yaw = mc.player.getYRot();
		Vec3 forward = Vec3.directionFromRotation(0, yaw);
		Vec3 right = Vec3.directionFromRotation(0, yaw + 90);

		double velX = 0;
		double velZ = 0;
		double s = 0.5;
		double speedValue = settings.speed.get();

		if (mc.options.keyUp.isDown()) {
			velX += forward.x * s * speedValue;
			velZ += forward.z * s * speedValue;
		}
		if (mc.options.keyDown.isDown()) {
			velX -= forward.x * s * speedValue;
			velZ -= forward.z * s * speedValue;
		}

		if (mc.options.keyRight.isDown()) {
			velX += right.x * s * speedValue;
			velZ += right.z * s * speedValue;
		}
		if (mc.options.keyLeft.isDown()) {
			velX -= right.x * s * speedValue;
			velZ -= right.z * s * speedValue;
		}

		if (velX >= settings.limit_speed.get().floatValue()) {
			velX = settings.limit_speed.get().floatValue();
		}
		if (velZ >= settings.limit_speed.get().floatValue()) {
			velZ = settings.limit_speed.get().floatValue();
		}
		((IVec3d) mc.player.getDeltaMovement()).meteor$set(velX, 0, velZ);
		mc.player.setShiftKeyDown(true);
	}
	@Override
	public void onDeactivate() {
		newSpeed = 0.6f;
		super.onDeactivate();
	}

	@Override
	public void onActivate() {
		newSpeed = 0.6f;
		newSpeed += settings.speed.get().floatValue();
		super.onActivate();
	}

	public void setMotion(double motion) {
		float forward = mc.player.zza;
		float yaw = mc.player.getYRot();
		if (forward == 0) {
			((IVec3d) mc.player.getDeltaMovement()).meteor$set(0,  mc.player.getDeltaMovement().y, 0);
		} else {
			double x = forward * motion * Math.cos(Math.toRadians(yaw + 90.0f)) * motion * Math.sin(Math.toRadians(yaw + 90.0f));
			double z = forward * motion * Math.sin(Math.toRadians(yaw + 90.0f)) * motion * Math.cos(Math.toRadians(yaw + 90.0f));

			((IVec3d) mc.player.getDeltaMovement()).meteor$set(x,  mc.player.getDeltaMovement().y, z);
		}
	}
}
