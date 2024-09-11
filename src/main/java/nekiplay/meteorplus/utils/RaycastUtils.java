package nekiplay.meteorplus.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class RaycastUtils {
	public static EntityHitResult raycastEntity(final double range, final float yaw, final float pitch, double boxexpand) {
		Entity camera = mc.getCameraEntity();
		Vec3d cameraVec = camera.getCameraPosVec(1f);

		final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
		final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

		final Vec3d rotation = new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);

		Vec3d vec3d3 = cameraVec.add(rotation.x * range, rotation.y * range, rotation.z * range);
		Box box = camera.getBoundingBox().stretch(rotation.multiply(range)).expand(boxexpand, boxexpand, boxexpand);

		return ProjectileUtil.raycast(camera, cameraVec, vec3d3, box, new Predicate<Entity>() {
			@Override
			public boolean test(Entity entity) {
				return !entity.isSpectator() && entity.isCollidable();
			}
		}, 0);
	}
	public static Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * ((float)Math.PI / 180);
		float g = -yaw * ((float)Math.PI / 180);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d(i * j, -k, h * j);
	}

	public static HitResult raycast(Vec3d camera, Vec3d rotation, double maxDistance, float tickDelta, boolean includeFluids) {
		Vec3d vec3d = camera;
		Vec3d vec3d2 = rotation;
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
	}

	public static BlockHitResult bucketRaycast(Vec3d camera, float pitch, float yaw, RaycastContext.FluidHandling fluidHandling) {
		float f = pitch;
		float g = yaw;
		Vec3d vec3d = camera;
		float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
		float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
		float j = -MathHelper.cos(-f * 0.017453292F);
		float k = MathHelper.sin(-f * 0.017453292F);
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
		return mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, mc.player));
	}
}
