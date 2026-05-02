package nekiplay.meteorplus.utils;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import static java.lang.Math.*;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static net.minecraft.util.Mth.wrapDegrees;
import static org.joml.Math.clamp;

public class RotationUtils {
	public static Vec3 getEyesPos() {
		LocalPlayer player = mc.player;

		return new Vec3(player.getX(),
			player.getY() + player.getEyeHeight(player.getPose()),
			player.getZ());
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.yaw, b.yaw), a.getPitch() - b.getPitch());
	}

	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.player.getX(), mc.player.getBoundingBox().minY +
			mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

		if (predict) {
			if (mc.player.onGround()) {
				eyesPos.add(mc.player.getDeltaMovement().x, 0.0, mc.player.getDeltaMovement().z);
			} else
				eyesPos.add(mc.player.getDeltaMovement().x, mc.player.getDeltaMovement().y, mc.player.getDeltaMovement().z);
		}

		final double diffX = vec.x - eyesPos.x;
		final double diffY = vec.y - eyesPos.y;
		final double diffZ = vec.z - eyesPos.z;

		return new Rotation(
			(float) toDegrees(atan2(diffZ, diffX)) - 90F,
			(float) (-toDegrees(atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)))));
	}

	public static Vec3 getCenter(final AABB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
			currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
			currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
			));
	}

	public static Rotation getNeededRotations(Vec3 vec) {
		Vec3 eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) toDegrees(atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -toDegrees(atan2(diffY, diffXZ));

		return new Rotation(yaw, pitch);
	}


	public static double getAngleToLookVec(Vec3 vec) {
		Rotation needed = getNeededRotations(vec);

		LocalPlayer player = mc.player;
		float currentYaw = wrapDegrees(player.getYRot());
		float currentPitch = wrapDegrees(player.getXRot());

		float diffYaw = currentYaw - needed.yaw;
		float diffPitch = currentPitch - needed.pitch;

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}


	public static final class Rotation {
		private float yaw;
		private float pitch;

		public Rotation(float yaw, float pitch) {
			this.yaw = wrapDegrees(yaw);
			this.pitch = wrapDegrees(pitch);
		}

		public Rotation(double yaw, double pitch) {
			this.yaw = wrapDegrees((float) yaw);
			this.pitch = wrapDegrees((float) pitch);
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}

		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		public void setPitch(float pitch) {
			this.pitch = pitch;
		}
	}

	public static Vector3d getVector3d(LivingEntity me, LivingEntity to, float range) {

		// САМЫЙ ПРОСТОЙ И ПРАВИЛЬНЫЙ РАСЧЕТ ОТ ЛИПЫ //

		double wHalf = to.getBbWidth() / 2;

		double yExpand = clamp(me.getEyeHeight(me.getPose()) - to.getY(), 0, to.getBbHeight() * (mc.player.distanceTo(to) / (range)));

		double xExpand = clamp(mc.player.position().x - to.position().x, -wHalf, wHalf);
		double zExpand = clamp(mc.player.position().z - to.position().z, -wHalf, wHalf);

		return new Vector3d(
			to.getX() - me.getX() + xExpand,
			to.getY() - me.getEyeHeight(me.getPose()) + yExpand,
			to.getZ() - me.getZ() + zExpand
		);
	}

	public static double getDistance(LivingEntity entity, float range) {
		return getVector3d(mc.player, entity, range).length();
	}

	public static double getDegree(LivingEntity entity, float range) {
		Vector3d vec = getVector3d(mc.player, entity, range);

		double yaw = wrapDegrees(toDegrees(atan2(vec.z, vec.x)) - 90);
		double delta = wrapDegrees(yaw - mc.player.getYRot());

		if (abs(delta) > 180) delta -= signum(delta) * 360;

		return abs(delta);
	}

	public static boolean isLookingAtMe(LivingEntity target) {
		double x = target.getX() - mc.player.getX();
		double z = target.getZ() - mc.player.getZ();

		float entityYaw = wrapDegrees(target.getYRot());
		double yaw = toDegrees(atan2(z, x)) + 90.0;

		return abs(wrapDegrees(yaw - entityYaw)) <= 90;
	}

	public static boolean isInHitBox(LivingEntity me, LivingEntity to) {
		double wHalf = to.getBbWidth() / 2;

		double yExpand = clamp(me.getEyeHeight(me.getPose()) - to.getY(), 0, to.getBbHeight());

		double xExpand = clamp(mc.player.getX() - to.getX(), -wHalf, wHalf);
		double zExpand = clamp(mc.player.getZ() - to.getZ(), -wHalf, wHalf);

		return new Vector3d(
			to.getX() - me.getX() + xExpand,
			to.getY() - me.getEyeHeight(me.getPose()) + yExpand,
			to.getZ() - me.getZ() + zExpand
		).length() == 0;
	}
}
