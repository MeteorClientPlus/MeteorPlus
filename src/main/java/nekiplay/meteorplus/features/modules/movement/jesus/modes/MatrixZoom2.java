package nekiplay.meteorplus.features.modules.movement.jesus.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusMode;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusModes;

public class MatrixZoom2 extends JesusMode {
	public MatrixZoom2() {
		super(JesusModes.Matrix_Zoom_2);
	}

	private final float range = 0.005f;
	private int tick = 0;

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
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
		if (mc.world.getBlockState(new BlockPos((int) mc.player.getPos().x, (int) (mc.player.getPos().y + range), (int) mc.player.getPos().z)).getBlock() == Blocks.WATER && !mc.player.horizontalCollision) {
			if (tick == 0) {
				((IVec3d) mc.player.getVelocity()).set(velX, 0.030091, velZ);
			}
			else if (tick == 1) {
				((IVec3d) mc.player.getVelocity()).set(velX, -0.030091, velZ);
			}
		}
	}
}
