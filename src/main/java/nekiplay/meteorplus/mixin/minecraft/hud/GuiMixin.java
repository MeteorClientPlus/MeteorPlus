package nekiplay.meteorplus.mixin.minecraft.hud;
import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
	private void onRenderHealthBar(GuiGraphics context, Player player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
		RenderHealthBarEvent healthBarRenderEvent = RenderHealthBarEvent.get(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
		MeteorClient.EVENT_BUS.post(healthBarRenderEvent);
		if (healthBarRenderEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void onRenderFoodBar(GuiGraphics context, Player player, int top, int right, CallbackInfo ci) {
		RenderFoodBarEvent renderFoodBarEvent = RenderFoodBarEvent.get(context, player, top, right);
		MeteorClient.EVENT_BUS.post(renderFoodBarEvent);
		if (renderFoodBarEvent.isCancelled()) {
			ci.cancel();
		}
	}
	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void onRenderArmor(GuiGraphics context, Player player, int i, int j, int k, int x, CallbackInfo ci) {
		RenderArmorBarEvent renderArmorBarEvent = RenderArmorBarEvent.get(context, player, i, j, k, x);
		MeteorClient.EVENT_BUS.post(renderArmorBarEvent);
		if (renderArmorBarEvent.isCancelled()) {
			ci.cancel();
		}
	}
	@Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(GuiGraphics context, CallbackInfo ci) {
		RenderMountHealthBarEvent renderMountHealthBarEvent = RenderMountHealthBarEvent.get(context);
		MeteorClient.EVENT_BUS.post(renderMountHealthBarEvent);
		if (renderMountHealthBarEvent.isCancelled()) {
			ci.cancel();
		}
	}
}
