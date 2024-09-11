package nekiplay.meteorplus.mixin.minecraft.entity;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
