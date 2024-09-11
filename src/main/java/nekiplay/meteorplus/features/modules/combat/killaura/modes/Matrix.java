package nekiplay.meteorplus.features.modules.combat.killaura.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusMode;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusModes;
import nekiplay.meteorplus.utils.GameSensitivityUtils;
import nekiplay.meteorplus.utils.math.StopWatch;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Objects;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.allowCrit;
import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.needCrit;
import static nekiplay.meteorplus.utils.RaycastUtils.raycastEntity;
import static net.minecraft.util.math.MathHelper.*;

public class Matrix extends KillAuraPlusMode {
	public Matrix() {
		super(KillAuraPlusModes.Matrix);
	}
	private final ArrayList<Entity> targets = new ArrayList<>();
	@Override
	public void onTickPre(TickEvent.Pre event) {
		if (target == null || !entityCheck(target)) {
			TargetUtils.getList(targets, this::entityCheck, settings.priority.get(), 15);
			if (!targets.isEmpty() && targets.get(0) instanceof LivingEntity livingEntity) {
				target = livingEntity;
			}
		}

		if (target != null && target.isAlive()) {
			isRotated = false;

			EntityHitResult result = raycastEntity(settings.range.get(), rotateVector.getX(), rotateVector.getY(), 0f);
			if (settings.onlyCrits.get() && !allowCrit() && needCrit(target)) {

			}
			else if (delayCheck() && result != null && result.getType() == HitResult.Type.ENTITY) {
				attack(target);
				ticks = 2;
			}



            if (settings.rotationType.get() == Type.Fast) {
				if (ticks > 0) {
					updateRotation(true, 180, 90);
					Rotations.rotate(rotateVector.getX(), rotateVector.getY());
					ticks--;
				} else {
					reset();
				}
			} else {
				if (!isRotated) {
					updateRotation(false, 80, 35);
					Rotations.rotate(rotateVector.getX(), rotateVector.getY());
				}
			}
		}
		else {
			reset();
		}
	}

	private boolean delayCheck() {
		return mc.player.getAttackCooldownProgress(0.5f) >= 1;
	}

	private void attack(Entity target) {

		mc.interactionManager.attackEntity(mc.player, target);
		mc.player.swingHand(Hand.MAIN_HAND);
	}

 	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
		if ((entity instanceof LivingEntity livingEntity && livingEntity.isDead()) || !entity.isAlive()) return false;

		Box hitbox = entity.getBoundingBox();
		if (!PlayerUtils.isWithin(
			clamp(mc.player.getX(), hitbox.minX, hitbox.maxX),
			clamp(mc.player.getY(), hitbox.minY, hitbox.maxY),
			clamp(mc.player.getZ(), hitbox.minZ, hitbox.maxZ),
			settings.range.get()
		)) return false;

		if (!settings.entities.get().contains(entity.getType())) return false;
		if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, settings.wallsRange.get())) return false;
		if (settings.ignoreTamed.get()) {
			if (entity instanceof Tameable tameable
				&& tameable.getOwnerUuid() != null
				&& tameable.getOwnerUuid().equals(mc.player.getUuid())
			) return false;
		}
		if (settings.ignorePassive.get()) {
			if (entity instanceof EndermanEntity enderman && !enderman.isAngry()) return false;
			if (entity instanceof ZombifiedPiglinEntity piglin && !piglin.isAttacking()) return false;
			if (entity instanceof WolfEntity wolf && !wolf.isAttacking()) return false;
		}
		if (entity instanceof PlayerEntity player) {
			if (player.isCreative()) return false;
			if (!Friends.get().shouldAttack(player)) return false;
			if (settings.shieldMode.get() == KillAura.ShieldMode.Ignore && player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player))) return false;
		}
		return true;
	}

	private final StopWatch stopWatch = new StopWatch();
	private Vector2f rotateVector = new Vector2f(0, 0);
	private LivingEntity target;
	private Entity selected;
	float lastYaw, lastPitch;
	int ticks = 0;
	boolean isRotated;

	public enum Type {
		Smooth,
		Fast
	}



	private void updateRotation(boolean attack, float rotationYawSpeed, float rotationPitchSpeed) {
		Vec3d vec = target.getPos().add(0, clamp(mc.player.getEyeHeight(mc.player.getPose()) - target.getY(),
				0, target.getHeight() * (mc.player.distanceTo(target) / settings.range.get())), 0)
			.subtract(mc.player.getEyePos());

		isRotated = true;

		float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
		float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, hypot(vec.x, vec.z))));

		float yawDelta = (wrapDegrees(yawToTarget - rotateVector.getX()));
		float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.getY()));
		int roundedYaw = (int) yawDelta;

		switch (settings.rotationType.get()) {
			case Smooth -> {
				float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 1.0f), rotationYawSpeed);
				float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 1.0f), rotationPitchSpeed);

				if (attack && selected != target && settings.speedUpRotationWhenAttacking.get()) {
					clampedPitch = Math.max(Math.abs(pitchDelta), 1.0f);
				} else {
					clampedPitch /= 3f;
				}


				if (Math.abs(clampedYaw - this.lastYaw) <= 3.0f) {
					clampedYaw = this.lastYaw + 3.1f;
				}

				float yaw = rotateVector.getX() + (yawDelta > 0 ? clampedYaw : -clampedYaw);
				float pitch = clamp(rotateVector.getY() + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);


				float gcd = GameSensitivityUtils.getGCDValue();
				yaw -= (yaw - rotateVector.getX()) % gcd;
				pitch -= (pitch - rotateVector.getY()) % gcd;


				rotateVector = new Vector2f(yaw, pitch);
				lastYaw = clampedYaw;
				lastPitch = clampedPitch;
				//if (options.getValueByName("Коррекция движения").get()) {
					//mc.player.rotationYawOffset = yaw;
				//}
			}
			case Fast -> {
				float yaw = rotateVector.getX() + roundedYaw;
				float pitch = clamp(rotateVector.getY() + pitchDelta, -90, 90);

				float gcd = GameSensitivityUtils.getGCDValue();
				yaw -= (yaw - rotateVector.getX()) % gcd;
				pitch -= (pitch - rotateVector.getY()) % gcd;

				rotateVector = new Vector2f(yaw, pitch);

				//if (options.getValueByName("Коррекция движения").get()) {
				//	mc.player.rotationYawOffset = yaw;
				//}
			}
		}
	}

	private void reset() {
		rotateVector = new Vector2f(mc.player.getYaw(), mc.player.getPitch());
	}
}
