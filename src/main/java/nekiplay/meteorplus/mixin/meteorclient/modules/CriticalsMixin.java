package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.needCrit;
import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.skipCrit;

@Mixin(value = Criticals.class, remap = false, priority = 1001)
public class CriticalsMixin extends Module {
	public CriticalsMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Unique
	private final SettingGroup sgMeteorPlus = settings.createGroup(MeteorPlusAddon.HUD_TITLE);

	@Unique
	private final Setting<Boolean> noWorkIfItsNotNeed = sgMeteorPlus.add(new BoolSetting.Builder()
		.name("Use-crit-only-if-necessary")
		.description("Hits with a crit if the enemy's health is less than the normal damage of your weapon.")
		.defaultValue(true)
		.build()
	);

	@Inject(method = "onSendPacket", at = @At("HEAD"), cancellable = true)
	private void onSendPacket(PacketEvent.Send event, CallbackInfo ci) {
		if (event.packet instanceof IPlayerInteractEntityC2SPacket packet && packet.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
			if (skipCrit() && noWorkIfItsNotNeed.get()) { ci.cancel(); return; }

			Entity entity = packet.getEntity();
			if (noWorkIfItsNotNeed.get() && (entity.getType() == EntityType.SHULKER_BULLET || entity.getType() == EntityType.FIREBALL)) {
				ci.cancel();
			}
			else if (entity instanceof LivingEntity livingEntity) {
				if (!needCrit(livingEntity) && noWorkIfItsNotNeed.get()) {
					ci.cancel();
				}
			}
		}
	}
}
