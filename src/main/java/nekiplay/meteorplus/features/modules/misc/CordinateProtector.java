package nekiplay.meteorplus.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import nekiplay.meteorplus.mixinclasses.SpoofMode;
import nekiplay.meteorplus.settings.ConfigModifier;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.List;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CordinateProtector {
	@EventHandler
	private void onDebugF3RenderText(DebugDrawTextEvent event) {
		List<String> lines = event.getLines();

		if (ConfigModifier.get().positionProtection.get()) {
			if (event.isLeft()) {
				int index = 0;
				for (Object obj : lines.toArray()) {
					String str = obj.toString();

					if (str.startsWith("XYZ:")) {
						String xyz = String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", mc.getCameraEntity().getX() + ConfigModifier.get().x_spoof.get(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ() + ConfigModifier.get().z_spoof.get());
						if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
							lines.set(index, xyz);
						}
						else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
							lines.set(index, "XYZ: *** / *** / ***");
						}
					} else if (str.startsWith("Block: ")) {
						BlockPos blockPos = mc.getCameraEntity().getBlockPos();
						blockPos = blockPos.add(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());

						String block = String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
						if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
							lines.set(index, block);
						}
						else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
							lines.set(index, "Block: *** *** *** [*** *** ***]");
						}
					} else if (str.startsWith("Chunk:")) {
						BlockPos blockPos = mc.getCameraEntity().getBlockPos();
						blockPos = blockPos.add(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
						ChunkPos chunkPos = new ChunkPos(blockPos);

						String chunk = String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z, chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
						if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
							lines.set(index, chunk);
						}
						else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
							lines.set(index, "Chunk: *** *** *** [*** *** in ***.***.mca]");
						}
					}
					index++;
				}
			} else {
				int index = 0;
				for (Object obj : lines.toArray()) {
					String str = obj.toString();

					if (str.contains("Targeted Block:")) {
						HitResult blockHitResult = event.blockHit();
						if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
							Formatting var10001 = Formatting.UNDERLINE;

							BlockPos blockPos = ((BlockHitResult) blockHitResult).getBlockPos();
							blockPos = blockPos.add(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
							if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
								lines.set(index, "" + var10001 + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
							}
							else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
								lines.set(index, var10001 + "Targeted Block: *** *** ***");
							}
						}
					} else if (str.contains("Targeted Fluid:")) {
						HitResult blockHitResult = event.fluidHit();
						if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
							Formatting var10001 = Formatting.UNDERLINE;

							BlockPos blockPos = ((BlockHitResult) blockHitResult).getBlockPos();
							blockPos = blockPos.add(ConfigModifier.get().x_spoof.get(), 0, ConfigModifier.get().z_spoof.get());
							if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
								lines.set(index, "" + var10001 + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
							}
							else if (ConfigModifier.get().spoofMode.get() == SpoofMode.Sensor) {
								lines.set(index, var10001 + "Targeted Fluid: *** *** ***");
							}
						}
					}
					index++;
				}
			}
		}
	}
}
