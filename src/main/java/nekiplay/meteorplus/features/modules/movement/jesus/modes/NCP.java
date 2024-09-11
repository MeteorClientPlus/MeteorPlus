package nekiplay.meteorplus.features.modules.movement.jesus.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusMode;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusModes;
import net.minecraft.block.AirBlock;
import net.minecraft.util.math.Vec3d;

public class NCP extends JesusMode {
	public NCP() {
		super(JesusModes.NCP);
	}
	float newSpeed = 0;
	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		mc.player.setSprinting(false);
		if (!mc.player.isInFluid()) {
			return;
		}
		Vec3d velocity = mc.player.getVelocity();

		if (mc.options.jumpKey.isPressed() && !mc.player.isSneaking() && !(mc.world.getBlockState(mc.player.getBlockPos().add(0, 1, 0)).getBlock() instanceof AirBlock)) {
			mc.player.setVelocity(velocity.x, 0.12, velocity.z);
		}
		velocity = mc.player.getVelocity();
		if (mc.options.sneakKey.isPressed()) {
			mc.player.setVelocity(velocity.x, -0.12, velocity.z);
		}
		velocity = mc.player.getVelocity();
		if (mc.world.getBlockState(mc.player.getBlockPos().add(0, 1, 0)).getBlock() instanceof AirBlock && mc.options.jumpKey.isPressed()) {
			mc.player.setSneaking(true);
			mc.player.setVelocity(velocity.x, 0.12, velocity.z);
		}

		float yaw = mc.player.getYaw();
		Vec3d forward = Vec3d.fromPolar(0, yaw);
		Vec3d right = Vec3d.fromPolar(0, yaw + 90);

		double velX = 0;
		double velZ = 0;
		double s = 0.5;
		double speedValue = settings.speed.get();

		if (mc.options.forwardKey.isPressed()) {
			velX += forward.x * s * speedValue;
			velZ += forward.z * s * speedValue;
		}
		if (mc.options.backKey.isPressed()) {
			velX -= forward.x * s * speedValue;
			velZ -= forward.z * s * speedValue;
		}

		if (mc.options.rightKey.isPressed()) {
			velX += right.x * s * speedValue;
			velZ += right.z * s * speedValue;
		}
		if (mc.options.leftKey.isPressed()) {
			velX -= right.x * s * speedValue;
			velZ -= right.z * s * speedValue;
		}

		if (velX >= settings.limit_speed.get().floatValue()) {
			velX = settings.limit_speed.get().floatValue();
		}
		if (velZ >= settings.limit_speed.get().floatValue()) {
			velZ = settings.limit_speed.get().floatValue();
		}
		((IVec3d) mc.player.getVelocity()).set(velX, 0, velZ);
		mc.player.setSneaking(true);
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
		float forward = mc.player.forwardSpeed;
		float yaw = mc.player.getYaw();
		if (forward == 0) {
			((IVec3d) mc.player.getVelocity()).set(0,  mc.player.getVelocity().y, 0);
		} else {
			double x = forward * motion * Math.cos(Math.toRadians(yaw + 90.0f)) * motion * Math.sin(Math.toRadians(yaw + 90.0f));
			double z = forward * motion * Math.sin(Math.toRadians(yaw + 90.0f)) * motion * Math.cos(Math.toRadians(yaw + 90.0f));

			((IVec3d) mc.player.getVelocity()).set(x,  mc.player.getVelocity().y, z);
		}
	}
}
