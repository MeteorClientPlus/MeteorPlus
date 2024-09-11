package nekiplay.meteorplus.mixin.meteorclient.modules;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.BlockESP;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPBlock;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BlockESP.class, remap = false)
public class BlockESPMixin extends Module {

	@Final
	@Shadow
	private final Long2ObjectMap<ESPChunk> chunks = new Long2ObjectOpenHashMap<>();

	public BlockESPMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Unique
	private long renders = 0;

	@Unique
	@Override
	public String getInfoString() {
		renders = 0;
		synchronized (chunks) {
            for (ESPChunk chunk : chunks.values()) {
                if (!chunk.shouldBeDeleted()) {
					ESPBlock block;
					for (ObjectIterator var1 = chunk.blocks.values().iterator(); var1.hasNext(); block.loaded = false) {
						block = (ESPBlock) var1.next();
						renders++;
					}
                }
            }
		}

		return "" + renders;
	}
}
