package nekiplay.meteorplus.mixin.minecraft.hud;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.RenderArmorBarEvent;
import nekiplay.main.events.hud.RenderFoodBarEvent;
import nekiplay.main.events.hud.RenderHealthBarEvent;
import nekiplay.main.events.hud.RenderMountHealthBarEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
	@Inject(method = "extractHearts", at = @At("HEAD"), cancellable = true)
	private void onRenderHealthBar(GuiGraphicsExtractor graphics, Player player, int xLeft, int yLineBase, int healthRowHeight, int heartOffsetIndex, float maxHealth, int currentHealth, int oldHealth, int absorption, boolean blinking, CallbackInfo ci) {
		RenderHealthBarEvent healthBarRenderEvent = RenderHealthBarEvent.get(graphics, player, xLeft, yLineBase, healthRowHeight, heartOffsetIndex, maxHealth, currentHealth, oldHealth, absorption, blinking);
		MeteorClient.EVENT_BUS.post(healthBarRenderEvent);
		if (healthBarRenderEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
	private void onRenderFoodBar(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo ci) {
		RenderFoodBarEvent renderFoodBarEvent = RenderFoodBarEvent.get(graphics, player, yLineBase, xRight);
		MeteorClient.EVENT_BUS.post(renderFoodBarEvent);
		if (renderFoodBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
	private static void onRenderArmor(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo ci) {
		RenderArmorBarEvent renderArmorBarEvent = RenderArmorBarEvent.get(graphics, player, yLineBase, numHealthRows, healthRowHeight, xLeft);
		MeteorClient.EVENT_BUS.post(renderArmorBarEvent);
		if (renderArmorBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(GuiGraphicsExtractor context, CallbackInfo ci) {
		RenderMountHealthBarEvent renderMountHealthBarEvent = RenderMountHealthBarEvent.get(context);
		MeteorClient.EVENT_BUS.post(renderMountHealthBarEvent);
		if (renderMountHealthBarEvent.isCancelled()) {
			ci.cancel();
		}
	}
}
