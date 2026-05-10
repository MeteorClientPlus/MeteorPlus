package nekiplay.meteorplus.features.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import nekiplay.meteorplus.mixinclasses.SpoofMode;
import nekiplay.meteorplus.settings.ConfigModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CoordinateProtector {
	@EventHandler
	private void onDebugF3RenderText(DebugDrawTextEvent event) {
		List<String> lines = event.getLines();

		if (ConfigModifier.get().positionProtection.get()) {
			int index = 0;
			for (Object obj : lines.toArray()) {
				String str = obj.toString();

				if (str.startsWith("XYZ:")) {
					String xyz = String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", mc.getCameraEntity().getX() + ConfigModifier.get().x_spoof.get(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ() + ConfigModifier.get().z_spoof.get());
					if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
						lines.set(index, xyz);
					} else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
						lines.set(index, "XYZ: *** / *** / ***");
					}
				} else if (str.startsWith("Block: ")) {
					BlockPos blockPos = mc.getCameraEntity().blockPosition();
					blockPos = blockPos.offset(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());

					String block = String.format(Locale.ROOT, "Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
					if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
						lines.set(index, block);
					} else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
						lines.set(index, "Block: *** *** ***");
					}
				} else if (str.startsWith("Chunk:")) {
					BlockPos blockPos = mc.getCameraEntity().blockPosition();
					blockPos = blockPos.offset(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
					ChunkPos chunkPos = ChunkPos.containing(blockPos);

					if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
						String chunk = String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x(), SectionPos.blockToSectionCoord(blockPos.getY()), chunkPos.z(), chunkPos.getRegionLocalX(), chunkPos.getRegionLocalZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
						lines.set(index, chunk);
					} else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
						lines.set(index, "Chunk: *** *** *** [*** *** in ***.***.mca]");
					}
				} else if (str.contains("Targeted Block:")) {
					HitResult blockHitResult = mc.player.pick(Modules.get().get(Reach.class).blockReach(), 1f, false);
					if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
						ChatFormatting var10001 = ChatFormatting.UNDERLINE;

						BlockPos blockPos = ((BlockHitResult) blockHitResult).getBlockPos();
						blockPos = blockPos.offset(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
						if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
							lines.set(index, var10001 + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
						} else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
							lines.set(index, var10001 + "Targeted Block: *** *** ***");
						}
					}
				} else if (str.contains("Targeted Fluid:")) {
					HitResult blockHitResult = mc.player.pick(Modules.get().get(Reach.class).blockReach(), 1f, true);
					if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
						ChatFormatting var10001 = ChatFormatting.UNDERLINE;

						BlockPos blockPos = ((BlockHitResult) blockHitResult).getBlockPos();
						blockPos = blockPos.offset(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
						if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
							lines.set(index, var10001 + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
						} else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
							lines.set(index, var10001 + "Targeted Fluid: *** *** ***");
						}
					}
				}
				index++;
			}
		}
	}
}
