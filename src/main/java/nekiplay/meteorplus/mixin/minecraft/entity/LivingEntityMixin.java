package nekiplay.meteorplus.mixin.minecraft.entity;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.movement.NoJumpDelay;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1001)
public class LivingEntityMixin {
	@Shadow
	protected boolean jumping;

	@Shadow
	private int noJumpDelay;

	@Inject(method = "aiStep", at = @At("HEAD"))
	private void hookTickMovement(CallbackInfo ci) {
		Modules modules = Modules.get();
		if (modules != null) {
			NoJumpDelay module = modules.get(NoJumpDelay.class);
			if (module != null && module.isActive()) {
				this.noJumpDelay = 0;
			}
		}
	}
}
