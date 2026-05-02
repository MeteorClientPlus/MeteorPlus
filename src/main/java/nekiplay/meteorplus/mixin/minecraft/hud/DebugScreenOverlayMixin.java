package nekiplay.meteorplus.mixin.minecraft.hud;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = DebugScreenOverlay.class, priority = 1001)
public class DebugScreenOverlayMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(
		method = "extractLines",
		at = @At(
			value = "HEAD"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void modifyDrawLeftText(GuiGraphicsExtractor context, List<String> text, boolean left, CallbackInfo ci) {
		DebugDrawTextEvent debugDrawTextEvent = DebugDrawTextEvent.get(text, left);
		MeteorClient.EVENT_BUS.post(debugDrawTextEvent);
	}
}
