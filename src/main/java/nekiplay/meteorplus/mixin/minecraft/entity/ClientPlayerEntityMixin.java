package nekiplay.meteorplus.mixin.minecraft.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.SprintPlus;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowPlus;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 1002)
public abstract class ClientPlayerEntityMixin {
	@Shadow
	public Input input;
	@Shadow
	public abstract boolean isSubmergedInWater();
	@Shadow
	protected abstract boolean isWalking();

	@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
	private void hookCustomMultiplier(CallbackInfo ci) {
		final PlayerUseMultiplierEvent playerUseMultiplier = new PlayerUseMultiplierEvent(0.2f, 0.2f);
		MeteorClient.EVENT_BUS.post(playerUseMultiplier);
		if (playerUseMultiplier.getForward() == 0.2f && playerUseMultiplier.getSideways() == 0.2f) {
			return;
		}

		final Input input = this.input;
		// reverse
		input.movementForward /= 0.2f;
		input.movementSideways /= 0.2f;

		// then
		input.movementForward *= playerUseMultiplier.getForward();
		input.movementSideways *= playerUseMultiplier.getSideways();
	}

	/**
	 * Hook sprint effect from NoSlow module
	 */
	@Inject(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), cancellable = true)
	private void hookSprintAffectStartF(CallbackInfoReturnable<Boolean> cir) {
		if (Modules.get().get(NoSlowPlus.class).isActive()) {
			cir.setReturnValue(true);
		}
	}
	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"))
	private boolean hookOmnidirectionalSprintB(ClientPlayerEntity instance) {
		return isOmniWalking(instance);
	}

	@ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
	private boolean hookSprintIgnoreBlindnessF(boolean original) {
		SprintPlus sprintPlus = Modules.get().get(SprintPlus.class);
		if (sprintPlus == null) { return false; }
		return !sprintPlus.shouldIgnoreBlindness() && original;
	}

	@Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"))
	private boolean hookOmnidirectionalSprintC(ClientPlayerEntity instance) {
		return isOmniWalking(instance);
	}

	@Unique
	private boolean isOmniWalking(ClientPlayerEntity instance) {
		boolean hasMovement = Math.abs(instance.input.movementForward) > 1.0E-5F || Math.abs(instance.input.movementSideways) > 1.0E-5F;
		boolean isWalking = (double) Math.abs(instance.input.movementForward) >= 0.8 || (double) Math.abs(instance.input.movementSideways) >= 0.8;
		boolean modifiedIsWalking = this.isSubmergedInWater() ? hasMovement : isWalking;
		SprintPlus sprintPlus = Modules.get().get(SprintPlus.class);
		if (sprintPlus == null) { return false; }
		return sprintPlus.shouldSprintOmnidirectionally() ? modifiedIsWalking : this.isWalking();
	}
}
