package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import nekiplay.main.events.ClickWindowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
	private void windowClick(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo callbackInfo) {
		final ClickWindowEvent event = ClickWindowEvent.get(syncId, slotId, button, actionType);
		MeteorClient.EVENT_BUS.post(event);

		if (event.isCancelled())
			callbackInfo.cancel();
	}
}
