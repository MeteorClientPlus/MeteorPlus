package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import java.util.Set;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.needCrit;

public class TriggerBot extends Module {
	public TriggerBot() {
		super(Categories.Combat, "Trigger-bot", "Attacks specified entities around you.");
	}

	@Override
	public void onDeactivate() {
		hitDelayTimer = 0;
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgTiming = settings.createGroup("Timing");

	private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.build()
	);

	private final Setting<Boolean> babies = sgGeneral.add(new BoolSetting.Builder()
		.name("babies")
		.description("Whether or not to attack baby variants of the entity.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> smartDelay = sgTiming.add(new BoolSetting.Builder()
		.name("smart-delay")
		.description("Uses the vanilla cooldown to attack entities.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Integer> hitDelay = sgTiming.add(new IntSetting.Builder()
		.name("hit-delay")
		.description("How fast you hit the entity in ticks.")
		.defaultValue(0)
		.min(0)
		.sliderMax(60)
		.visible(() -> !smartDelay.get())
		.build()
	);

	private final Setting<Boolean> randomDelayEnabled = sgTiming.add(new BoolSetting.Builder()
		.name("random-delay-enabled")
		.description("Adds a random delay between hits to attempt to bypass anti-cheats.")
		.defaultValue(false)
		.visible(() -> !smartDelay.get())
		.build()
	);

	private final Setting<Integer> randomDelayMax = sgTiming.add(new IntSetting.Builder()
		.name("random-delay-max")
		.description("The maximum value for random delay.")
		.defaultValue(4)
		.min(0)
		.sliderMax(20)
		.visible(() -> randomDelayEnabled.get() && !smartDelay.get())
		.build()
	);

	private final Setting<Boolean> onlyCrits = sgTiming.add(new BoolSetting.Builder()
		.name("only-crits")
		.description("Attack enemy only if this attack crit after jump.")
		.defaultValue(true)
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreOnlyCritsOnLevitation = sgTiming.add(new BoolSetting.Builder()
		.name("ignore-only-crits-on-levetation")
		.defaultValue(true)
		.visible(() -> onlyCrits.get())
		.build()
	);
	private int hitDelayTimer;

	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.getCameraEntity())) return false;
		if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDeadOrDying()) || !entity.isAlive()) return false;
		if (!entities.get().contains(entity.getType())) return false;
		if (entity instanceof OwnableEntity tameable
			&& tameable.getOwner().getUUID() != null
			&& tameable.getOwner().getUUID().equals(mc.player.getUUID())) return false;
		if (entity instanceof Player player) {
			if (player.isCreative()) return false;
			if (!Friends.get().shouldAttack(player)) return false;
			AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
			Teams teams = Modules.get().get(Teams.class);
			if (antiBotPlus != null && antiBotPlus.isBot(player)) {
				return false;
			}
			if (teams != null && teams.isInYourTeam(player)) {
				return false;
			}
		}

		return !(entity instanceof Animal) || babies.get() || !((Animal) entity).isBaby();
	}

	private boolean delayCheck() {
		if (onlyCrits.get() && !CriticalsPlus.allowCrit() && needCrit(mc.crosshairPickEntity)) {
			if (ignoreOnlyCritsOnLevitation.get() && !Objects.requireNonNull(mc.player).hasEffect(MobEffects.LEVITATION)) {
				return false;
			}
			else if (!ignoreOnlyCritsOnLevitation.get()) {
				return false;
			}
		}

		if (smartDelay.get()) return mc.player.getAttackStrengthScale(0.5f) >= 1;

		if (hitDelayTimer > 0) {
			hitDelayTimer--;
			return false;
		} else {
			hitDelayTimer = hitDelay.get();
			if (randomDelayEnabled.get()) hitDelayTimer += Math.round(Math.random() * randomDelayMax.get());
			return true;
		}
	}

	@EventHandler
	private void onTick(Render3DEvent event) {
		if (!mc.player.isAlive() || PlayerUtils.getGameMode() == GameType.SPECTATOR) return;
		if (mc.crosshairPickEntity == null) return;

		if (delayCheck() && entityCheck(mc.crosshairPickEntity)) hitEntity(mc.crosshairPickEntity);
	}

	private void hitEntity(Entity target) {
		mc.gameMode.attack(mc.player, target);
		mc.player.swing(InteractionHand.MAIN_HAND);
	}
}
