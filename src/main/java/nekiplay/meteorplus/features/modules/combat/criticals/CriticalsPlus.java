package nekiplay.meteorplus.features.modules.combat.criticals;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CriticalsPlus extends Module {
	public CriticalsPlus() {
		super(Categories.Combat, "Criticals+", "Better criticals module");
	}

	private static final Minecraft mc = Minecraft.getInstance();

	public static boolean canCrit() {
		return !mc.player.onGround() && mc.player.fallDistance > 0;
	}

	public static boolean skipCrit() {
		return !mc.player.onGround() || mc.player.isUnderWater() || mc.player.isInLava() || mc.player.onClimbable();
	}

	public static boolean allowCrit() {
		if (canCrit()) {
			return true;
		} else if (Modules.get().isActive(Criticals.class)) {
			return !skipCrit();
		}
		return false;
	}

	public static boolean needCrit(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			return livingEntity.getHealth() >= DamageUtils.getAttackDamage(mc.player, livingEntity);
		}
		return false;
	}
}
