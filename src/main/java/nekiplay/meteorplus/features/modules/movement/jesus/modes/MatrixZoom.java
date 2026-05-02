package nekiplay.meteorplus.features.modules.movement.jesus.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusMode;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusModes;

public class MatrixZoom extends JesusMode {
	public MatrixZoom() {
		super(JesusModes.Matrix_Zoom);
	}

	private final float range = 0.005f;

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
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
		if (mc.level.getBlockState(new BlockPos(mc.player.getBlockX(), (int) (mc.player.getBlockY() + range), mc.player.getBlockZ())).getBlock() == Blocks.WATER && !mc.player.horizontalCollision) {
			((IVec3) mc.player.getDeltaMovement()).meteor$set(velX, 0, velZ);
		}
	}
}
