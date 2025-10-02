package nekiplay.meteorplus.mixin.minecraft.hud;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = DebugHud.class, priority = 1001)
public class DebugHudMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(
		method = "drawText",
		at = @At(
			value = "HEAD"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void modifyDrawLeftText(DrawContext context, List<String> text, boolean left, CallbackInfo ci) {
		DebugDrawTextEvent debugDrawTextEvent = DebugDrawTextEvent.get(text, left);
		MeteorClient.EVENT_BUS.post(debugDrawTextEvent);
	}
}
