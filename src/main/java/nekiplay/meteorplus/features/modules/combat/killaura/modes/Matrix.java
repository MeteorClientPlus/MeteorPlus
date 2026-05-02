package nekiplay.meteorplus.features.modules.combat.killaura.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusMode;
import nekiplay.meteorplus.features.modules.combat.killaura.KillAuraPlusModes;
import nekiplay.meteorplus.utils.GameSensitivityUtils;
import nekiplay.meteorplus.utils.math.StopWatch;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.allowCrit;
import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.needCrit;
import static nekiplay.meteorplus.utils.RaycastUtils.raycastEntity;
import static net.minecraft.util.Mth.*;

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

			EntityHitResult result = raycastEntity(settings.range.get(), rotateVector.u(), rotateVector.v(), 0f);
			if (result != null) {
				ChatUtils.info(result.getType().name());
			}
			if (settings.onlyCrits.get() && !allowCrit() && needCrit(target)) {

			} else if (delayCheck() && result != null && result.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
				attack(target);
				ticks = 2;
			}


			if (settings.rotationType.get() == Type.Fast) {
				if (ticks > 0) {
					updateRotation(true, 180, 90);
					Rotations.rotate(rotateVector.u(), rotateVector.v());
					ticks--;
				} else {
					reset();
				}
			} else {
				if (!isRotated) {
					updateRotation(false, 80, 35);
					Rotations.rotate(rotateVector.u(), rotateVector.v());
				}
			}
		} else {
			reset();
		}
	}

	private boolean delayCheck() {
		return mc.player.getAttackStrengthScale(0.5f) >= 1;
	}

	private void attack(Entity target) {

		mc.gameMode.attack(mc.player, target);
		mc.player.swing(InteractionHand.MAIN_HAND);
	}

	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.getCameraEntity())) return false;
		if ((entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) || !entity.isAlive())
			return false;

		AABB hitbox = entity.getBoundingBox();
		if (!PlayerUtils.isWithin(
			clamp(mc.player.getX(), hitbox.minX, hitbox.maxX),
			clamp(mc.player.getY(), hitbox.minY, hitbox.maxY),
			clamp(mc.player.getZ(), hitbox.minZ, hitbox.maxZ),
			settings.range.get()
		)) return false;

		if (!settings.entities.get().contains(entity.getType())) return false;
		if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, settings.wallsRange.get())) return false;
		if (settings.ignoreTamed.get()) {
			if (entity instanceof OwnableEntity tameable
				&& tameable.getOwner().getUUID() != null
				&& tameable.getOwner().getUUID().equals(mc.player.getUUID())
			) return false;
		}
		if (settings.ignorePassive.get()) {
			if (entity instanceof EnderMan enderman && !enderman.isCreepy()) return false;
			if (entity instanceof ZombifiedPiglin piglin && !piglin.isAggressive()) return false;
			if (entity instanceof Wolf wolf && !wolf.isAggressive()) return false;
		}
		if (entity instanceof Player player) {
			if (player.isCreative()) return false;
			if (!Friends.get().shouldAttack(player)) return false;
			if (settings.shieldMode.get() == KillAura.ShieldMode.Ignore && player.isBlocking()) return false;
		}
		return true;
	}

	private final StopWatch stopWatch = new StopWatch();
	private UVPair rotateVector = new UVPair(0, 0);
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
		Vec3 vec = target.position().add(0, clamp(mc.player.getEyeHeight(mc.player.getPose()) - target.getY(),
				0, target.getBbHeight() * (mc.player.distanceTo(target) / settings.range.get())), 0)
			.subtract(mc.player.getEyePosition());

		isRotated = true;

		float yawToTarget = (float) wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90);
		float pitchToTarget = (float) (-Math.toDegrees(Math.atan2(vec.y, length(vec.x, vec.z))));

		float yawDelta = (wrapDegrees(yawToTarget - rotateVector.u()));
		float pitchDelta = (wrapDegrees(pitchToTarget - rotateVector.v()));
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

				float yaw = rotateVector.u() + (yawDelta > 0 ? clampedYaw : -clampedYaw);
				float pitch = clamp(rotateVector.v() + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -89.0F, 89.0F);


				float gcd = GameSensitivityUtils.getGCDValue();
				yaw -= (yaw - rotateVector.u()) % gcd;
				pitch -= (pitch - rotateVector.v()) % gcd;


				rotateVector = new UVPair(yaw, pitch);
				lastYaw = clampedYaw;
				lastPitch = clampedPitch;
				//if (options.getValueByName("Коррекция движения").get()) {
				//mc.player.rotationYawOffset = yaw;
				//}
			}
			case Fast -> {
				float yaw = rotateVector.u() + roundedYaw;
				float pitch = clamp(rotateVector.v() + pitchDelta, -90, 90);

				float gcd = GameSensitivityUtils.getGCDValue();
				yaw -= (yaw - rotateVector.u()) % gcd;
				pitch -= (pitch - rotateVector.v()) % gcd;

				rotateVector = new UVPair(yaw, pitch);

				//if (options.getValueByName("Коррекция движения").get()) {
				//	mc.player.rotationYawOffset = yaw;
				//}
			}
		}
	}

	private void reset() {
		rotateVector = new UVPair(mc.player.getYRot(), mc.player.getXRot());
	}
}
