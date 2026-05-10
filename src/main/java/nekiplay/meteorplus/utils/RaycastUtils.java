package nekiplay.meteorplus.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class RaycastUtils {
	public static EntityHitResult raycastEntity(final double range, final float yaw, final float pitch, double boxexpand) {
		Entity camera = mc.getCameraEntity();
		Vec3 cameraVec = mc.player.getEyePosition();

		final float yawCos = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
		final float yawSin = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
		final float pitchCos = -Mth.cos(-pitch * 0.017453292F);
		final float pitchSin = Mth.sin(-pitch * 0.017453292F);

		final Vec3 rotation = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);

		Vec3 vec3d3 = cameraVec.add(rotation.x * range, rotation.y * range, rotation.z * range);
		AABB box = camera.getBoundingBox().expandTowards(rotation.scale(range)).inflate(boxexpand, boxexpand, boxexpand);

		return ProjectileUtil.getEntityHitResult(camera, cameraVec, vec3d3, box, new Predicate<Entity>() {
			@Override
			public boolean test(Entity entity) {
				return !entity.isSpectator() && entity.canBeCollidedWith(mc.player);
			}
		}, 0);
	}

	public static Vec3 getRotationVector(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180);
		float g = -yaw * ((float) Math.PI / 180);
		float h = Mth.cos(g);
		float i = Mth.sin(g);
		float j = Mth.cos(f);
		float k = Mth.sin(f);
		return new Vec3(i * j, -k, h * j);
	}

	public static HitResult raycast(Vec3 camera, Vec3 rotation, double maxDistance, float tickDelta, boolean includeFluids) {
		Vec3 vec3d = camera;
		Vec3 vec3d2 = rotation;
		Vec3 vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return mc.level.clip(new ClipContext(vec3d, vec3d3, ClipContext.Block.OUTLINE, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, mc.player));
	}

	public static BlockHitResult bucketRaycast(Vec3 camera, float pitch, float yaw, ClipContext.Fluid fluidHandling) {
		float f = pitch;
		float g = yaw;
		Vec3 vec3d = camera;
		float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
		float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
		float j = -Mth.cos(-f * 0.017453292F);
		float k = Mth.sin(-f * 0.017453292F);
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		Vec3 vec3d2 = vec3d.add((double) l * 5.0, (double) k * 5.0, (double) n * 5.0);
		return mc.level.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, fluidHandling, mc.player));
	}
}
