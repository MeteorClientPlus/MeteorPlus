package nekiplay.meteorplus.utils.xraybruteforce;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import nekiplay.meteorplus.features.modules.world.XrayBruteforce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.getRenderDistance;

public class XChunk {
	private static final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

	private final int x, z;
	public Long2ObjectMap<XBlock> blocks;

	public XChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public XBlock get(int x, int y, int z) {
		return blocks == null ? null : blocks.get(XBlock.getKey(x, y, z));
	}

	public void add(BlockPos blockPos, boolean update) {
		XBlock block = new XBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ());

		if (blocks == null) blocks = new Long2ObjectOpenHashMap<>(64);
		blocks.put(XBlock.getKey(blockPos), block);

		if (update) block.update();
	}

	public void add(BlockPos blockPos) {
		add(blockPos, true);
	}

	public void remove(BlockPos blockPos) {
		if (blocks != null) {
			XBlock block = blocks.remove(XBlock.getKey(blockPos));
			if (block != null) block.group.remove(block);
		}
	}

	public void update() {
		if (blocks != null) {
			for (XBlock block : blocks.values()) block.update();
		}
	}

	public void update(int x, int y, int z) {
		if (blocks != null) {
			XBlock block = blocks.get(XBlock.getKey(x, y, z));
			if (block != null) block.update();
		}
	}

	public int size() {
		return blocks == null ? 0 : blocks.size();
	}

	public boolean shouldBeDeleted() {
		int viewDist = getRenderDistance() + 1;
		int chunkX = SectionPos.blockToSectionCoord(mc.player.blockPosition().getX());
		int chunkZ = SectionPos.blockToSectionCoord(mc.player.blockPosition().getZ());

		return x > chunkX + viewDist || x < chunkX - viewDist || z > chunkZ + viewDist || z < chunkZ - viewDist;
	}

	public void render(Render3DEvent event, XrayBruteforce.RenderOre ore) {
		if (blocks != null) {
			for (XBlock block : blocks.values()) block.render(event, ore);
		}
	}


	public static XChunk searchChunk(ChunkAccess chunk, List<Block> blocks) {
		XChunk schunk = new XChunk(chunk.getPos().x(), chunk.getPos().z());
		if (schunk.shouldBeDeleted()) return schunk;

		for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
			for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
				int height = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getFirstAvailable(x - chunk.getPos().getMinBlockX(), z - chunk.getPos().getMinBlockZ());

				for (int y = mc.level.getMinY(); y < height; y++) {
					blockPos.set(x, y, z);
					BlockState bs = chunk.getBlockState(blockPos);

					if (blocks.contains(bs.getBlock())) schunk.add(blockPos, false);
				}
			}
		}

		return schunk;
	}
}
