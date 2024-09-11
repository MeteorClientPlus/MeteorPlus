package nekiplay.meteorplus.mixin.whereisit;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.integrations.WhereIsIt;
import nekiplay.meteorplus.utils.ColorRemover;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.whereisit.client.render.Rendering;
import red.jackf.whereisit.config.WhereIsItConfig;

@Mixin(Rendering.class)
public class RenderingMixin {
	@Unique
	private static WhereIsIt whereIsIt;
	@Inject(method = "renderLabel", at = @At("HEAD"), cancellable = true)
	private static void renderLabel(Vec3d pos, Text name, MatrixStack pose, Camera camera, VertexConsumerProvider consumers, CallbackInfo ci) {
		if (whereIsIt == null) {
			whereIsIt = Modules.get().get(WhereIsIt.class);
		}
		if (whereIsIt != null && whereIsIt.isActive()) {
			pose.push();


			pos = pos.subtract(camera.getPos());


			pose.translate(pos.x, pos.y + whereIsIt.y_offset.get(), pos.z);
			pose.multiply(camera.getRotation());
			var factor = 0.025f * whereIsIt.text_scale.get().floatValue();
			pose.scale(-factor, -factor, factor);
			var matrix4f = pose.peek().getPositionMatrix();

			String text2 = name.getString();
			if (whereIsIt.suport_color_symbols.get()) {
				text2 = ColorRemover.GetVerbatimAll(text2);
			}

			var width = MinecraftClient.getInstance().textRenderer.getWidth(text2);
			float x = (float) -width / 2;

			if (whereIsIt.background.get()) {
				var bgBuffer = consumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough());
				var bgColour = ((int) (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255F)) << 24;
				bgBuffer.vertex(matrix4f, x - 1, -1f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
				bgBuffer.vertex(matrix4f, x - 1, 10f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
				bgBuffer.vertex(matrix4f, x + width, 10f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
				bgBuffer.vertex(matrix4f, x + width, -1f, 0).color(bgColour).light(LightmapTextureManager.MAX_LIGHT_COORDINATE);
			}

			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.depthFunc(GL11.GL_ALWAYS);
			RenderSystem.enableBlend();
			RenderSystem.depthMask(true);

			String text = name.getString();
			if (!whereIsIt.suport_color_symbols.get()) {
				MinecraftClient.getInstance().textRenderer.draw(text, x, 0, whereIsIt.notvisible_text_color.get().getPacked(), false,
					matrix4f, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				MinecraftClient.getInstance().textRenderer.draw(text, x, 0, whereIsIt.visible_text_color.get().getPacked(), false,
					matrix4f, consumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			} else {
				int color = getColor(text);

				text = ColorRemover.GetVerbatimAll(text);

				MinecraftClient.getInstance().textRenderer.draw(text, x, 0, color, false,
					matrix4f, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				MinecraftClient.getInstance().textRenderer.draw(text, x, 0, color, false,
					matrix4f, consumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

			}
			RenderSystem.depthFunc(GL11.GL_LEQUAL);
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();

			pose.pop();

			ci.cancel();
		}
	}

	@Unique
	private static int getColor(String text) {
		if (text.length() >= 2) {
			char first_char = text.charAt(0);
			char color_char = text.charAt(1);

			if (first_char == '&' || first_char == '§') {
				switch (color_char) {
					case '4' -> {
						return 11141120;
					}
					case 'c' -> {
						return 16733525;
					}
					case '6' -> {
						return 16755200;
					}
					case 'e' -> {
						return 16777045;
					}
					case '2' -> {
						return 43520;
					}
					case 'a' -> {
						return 5635925;
					}
					case 'b' -> {
						return 5636095;
					}
					case '3' -> {
						return 43690;
					}
					case '1' -> {
						return 170;
					}
					case '9' -> {
						return 5592575;
					}
					case 'd' -> {
						return 16733695;
					}
					case '5' -> {
						return 11141290;
					}
					case 'f' -> {
						return 16777215;
					}
					case '7' -> {
						return 11184810;
					}
					case '8' -> {
						return 5592405;
					}
					case '0' -> {
						return 0;
					}
					default -> {
						return 0xffffff;
					}
				}
			}
		}
		return 0xffffff;
	}
}
