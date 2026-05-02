package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import nekiplay.main.events.ClickWindowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
	private void windowClick(int syncId, int slotId, int button, ContainerInput actionType, Player player, CallbackInfo callbackInfo) {
		final ClickWindowEvent event = ClickWindowEvent.get(syncId, slotId, button, actionType);
		MeteorClient.EVENT_BUS.post(event);

		if (event.isCancelled())
			callbackInfo.cancel();
	}
}
