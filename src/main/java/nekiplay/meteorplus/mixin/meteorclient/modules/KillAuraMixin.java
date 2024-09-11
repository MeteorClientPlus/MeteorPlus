package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.features.modules.combat.Teams;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.allowCrit;
import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.needCrit;

@Mixin(value = KillAura.class, remap = false, priority = 1001)
public class KillAuraMixin extends Module {
	@Unique
	private final SettingGroup sgTimingPlus = settings.createGroup(MeteorPlusAddon.HUD_TITLE + " Timing");

	@Final
	@Shadow
	private final SettingGroup sgTargeting = settings.getGroup("Targeting");

	@Final
	@Shadow
	private final SettingGroup sgTiming = settings.getGroup("Timing");

	@Final
	@Shadow
	private final Setting<Set<EntityType<?>>> entities = (Setting<Set<EntityType<?>>>) sgTargeting.get("entities");

	@Final
	@Shadow
	private final List<Entity> targets = new ArrayList<>();

	@Shadow
	public Entity getTarget() {
		if (!targets.isEmpty()) return targets.get(0);
		return null;
	}

	@Shadow
	@Final
	private final Setting<Boolean> customDelay = (Setting<Boolean>) sgTiming.get("custom-delay");

	@Unique
	private final Setting<Boolean> smartDelayv2 = sgTimingPlus.add(new BoolSetting.Builder()
		.name("smart-delay-v2")
		.description("Calculate sword damage to enemy.")
		.defaultValue(true)
		.visible(() -> !customDelay.get())
		.build()
	);
	@Unique
	private final Setting<Integer> maxHurtTime = sgTimingPlus.add(new IntSetting.Builder()
		.name("max-enemy-hurt-time")
		.defaultValue(0)
		.min(0)
		.sliderMax(20)
		.visible(() -> smartDelayv2.get())
		.build()
	);

	@Unique
	private final Setting<Boolean> onlyCrits = sgTimingPlus.add(new BoolSetting.Builder()
		.name("only-crits")
		.description("Attack enemy only if this attack crit after jump.")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreOnlyCritsOnLevitation = sgTimingPlus.add(new BoolSetting.Builder()
		.name("ignore-only-crits-on-levetation")
		.defaultValue(true)
		.visible(() -> onlyCrits.get())
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreSmartDelayForShulkerBulletAndGhastCharge = sgTimingPlus.add(new BoolSetting.Builder()
		.name("ignore-delay-for-one-hit-entities")
		.description("Ignore attack delay for shulker bullet and fireball.")
		.defaultValue(true)
		.visible(() -> entities.get().contains(EntityType.SHULKER_BULLET) || entities.get().contains(EntityType.FIREBALL))
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreOnlyCritsForOneHitEntity = sgTimingPlus.add(new BoolSetting.Builder()
		.name("ignore-only-crits-for-one-hit-entities")
		.description("Ignore only crits delay for shulker bullet and fireball.")
		.defaultValue(true)
		.visible(() -> ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && onlyCrits.get())
		.build()
	);

	@Unique
	private final Setting<Boolean> customDelayOneHit = sgTimingPlus.add(new BoolSetting.Builder()
		.name("custom-delay-for-one-hit-entities")
		.defaultValue(true)
		.visible(() -> entities.get().contains(EntityType.SHULKER_BULLET) || entities.get().contains(EntityType.FIREBALL))
		.build()
	);

	@Unique
	private final Setting<Integer> hitDelay = sgTimingPlus.add(new IntSetting.Builder()
		.name("delay-for-one-hit-entities")
		.description("How fast you hit the entity in ticks.")
		.defaultValue(2)
		.min(0)
		.sliderMax(60)
		.visible(customDelay::get)
		.build()
	);

	@Unique
	private int hitTimer;

	public KillAuraMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Inject(method = "delayCheck", at = @At("HEAD"), cancellable = true)
	private void delayCheck(CallbackInfoReturnable<Boolean> cir) {
		if (onlyCrits.get() && !allowCrit() && needCrit(getTarget())) {
			if (ignoreOnlyCritsOnLevitation.get() && !Objects.requireNonNull(mc.player).hasStatusEffect(StatusEffects.LEVITATION)) {
				cir.setReturnValue(false);
				return;
			}
			else if (!ignoreOnlyCritsOnLevitation.get()) {
				cir.setReturnValue(false);
				return;
			}
		}

		float delay = (customDelayOneHit.get()) ? hitDelay.get() : 0.5f;

		if (oneHitEntity()) {
			if (customDelayOneHit.get()) {
				if (hitTimer < delay) {
					hitTimer++;
					cir.setReturnValue(false);
					return;
				} else  {
					if (ignoreOnlyCritsForOneHitEntity.get()) {
						cir.setReturnValue(true);
						return;
					} else if (oneHitEntity() && ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && !onlyCrits.get()) {
						cir.setReturnValue(true);
						return;
					}
				}
			}
			else {
				if (ignoreOnlyCritsForOneHitEntity.get()) {
					cir.setReturnValue(true);
					return;
				} else if (oneHitEntity() && ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && !onlyCrits.get()) {
					cir.setReturnValue(true);
					return;
				}
			}
		}

		if (smartDelayv2.get() && getTarget() instanceof LivingEntity livingEntity) {
			if (DamageUtils.getAttackDamage(mc.player, livingEntity) >= livingEntity.getHealth() + 1.5 &&  mc.player.getAttackCooldownProgress(0.5f) >= 0.25 && livingEntity.hurtTime <= maxHurtTime.get()) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	private boolean oneHitEntity() {
		if (getTarget() != null) {
			if (ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && (getTarget().getType() == EntityType.FIREBALL || getTarget().getType() == EntityType.SHULKER_BULLET)) {
				return true;
			}
		}
		return false;
	}

	@Inject(method = "attack", at = @At("RETURN"))
	private void onAttackEntity(Entity target, CallbackInfo ci) {
		hitTimer = 0;
	}


	@Inject(method = "entityCheck", at = @At("RETURN"), cancellable = true)
	protected void entityCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		Teams teams = Modules.get().get(Teams.class);
		if (antiBotPlus != null && teams != null && entity instanceof PlayerEntity) {
			if (cir.getReturnValueZ()) {
				boolean ignore = !antiBotPlus.isBot(entity);
				if (ignore) {
					ignore = !teams.isInYourTeam(entity);
				}
				cir.setReturnValue(ignore);
			}
		}
	}
}
