package nekiplay.meteorplus.mixin.minecraft.entity;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.movement.NoJumpDelay;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin {
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
