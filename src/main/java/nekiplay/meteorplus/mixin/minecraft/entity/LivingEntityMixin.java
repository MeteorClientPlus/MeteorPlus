package nekiplay.meteorplus.mixin.minecraft.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import nekiplay.meteorplus.features.modules.movement.NoJumpDelay;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyPlus;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = LivingEntity.class, priority = 1001)
public class LivingEntityMixin {
	@Shadow
	protected boolean jumping;

	@Shadow
	private int jumpingCooldown;

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void hookTickMovement(CallbackInfo ci) {
		Modules modules = Modules.get();
		NoJumpDelay noJumpDelay = modules.get(NoJumpDelay.class);
		if (noJumpDelay == null) { return; }
		if (noJumpDelay.isActive()) {
			jumpingCooldown = 0;
		}
	}
}
