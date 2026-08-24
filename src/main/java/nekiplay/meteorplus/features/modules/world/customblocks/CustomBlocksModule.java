package nekiplay.meteorplus.features.modules.world.customblocks;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CustomBlocksModule extends Module {
	public CustomBlocksModule() {
		super(Categories.World, "custom-blocks", "Place custom blocks in world, extension for Holograms");
	}

	public Gson gson = new Gson();
	public HashMap<PosData, CustomBlockData> allBlocks = new HashMap<PosData, CustomBlockData>();

	private final SettingGroup settingsGroup = settings.getDefaultGroup();

	@Override
	public void onActivate() {
		super.onActivate();

		createDefault();
		load();

		for (ChunkAccess chunk : Utils.chunks()) {
			updateChunkData(chunk);
		}
	}

	public final Setting<Boolean> noSetIfAir = settingsGroup.add(new BoolSetting.Builder()
		.name("no-set-if-air")
		.description("No set custom block if original block equals air.")
		.defaultValue(true)
		.build()
	);

	private void updateChunkData(ChunkAccess chunk) {
		String dimension = PlayerUtils.getDimension().name();
		for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
			for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
				int height = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getFirstAvailable(x - chunk.getPos().getMinBlockX(), z - chunk.getPos().getMinBlockZ());

				for (int y = mc.level.getMinY(); y < height; y++) {

					BlockPos pos = new BlockPos(x, y, z);

					BlockState bs = chunk.getBlockState(pos);
					Block original = bs.getBlock();

					CustomBlockData customBlockData = allBlocks.get(new PosData(pos));
					if (customBlockData != null) {
						if (customBlockData.dimension.equals(dimension)) {
							if (isValidBlockForReplace(original)) {
								setBlock(pos, customBlockData);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onChunkData(ChunkDataEvent event) {
		LevelChunk chunk = event.chunk();
		updateChunkData(chunk);
	}

	private boolean isValidBlockForReplace(Block block) {
		if (block != Blocks.AIR && noSetIfAir.get()) {
			return true;
		} else return !noSetIfAir.get();
	}

	private void setBlock(BlockPos pos, CustomBlockData data) {
		BlockState block = Block.stateById(data.block_id);

		mc.level.setBlockAndUpdate(pos, block);
	}

	@EventHandler
	private void onBlockUpdate(BlockUpdateEvent event) {
		String dimension = PlayerUtils.getDimension().name();
		CustomBlockData customBlockData = allBlocks.get(new PosData(event.pos));
		if (customBlockData != null) {
			if (customBlockData.dimension.equals(dimension)) {
				Block original = event.newState.getBlock();
				if (isValidBlockForReplace(original)) {
					setBlock(event.pos, customBlockData);
				}
			}
		}
	}

	private void load() {
		File dir = new File(MeteorClient.FOLDER, "customblocks");
		if (dir.exists()) {
			String world_name = Utils.getWorldName();
			File dir2 = new File(dir, world_name);
			if (dir2.exists()) {
				allBlocks.clear();
				File[] files = dir2.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.exists()) {
							MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Loading hologram: " + file.getName());
							try {
								BufferedReader reader = Files.newBufferedReader(Path.of(file.toURI()), StandardCharsets.UTF_8);
								try {
									String json = reader.lines().collect(Collectors.joining());
									CustomBlockData hologramData = gson.fromJson(json, CustomBlockData.class);
									if (hologramData != null) {
										for (PosData posData : hologramData.positions) {
											allBlocks.put(posData, hologramData);
										}
										MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Success loaded custom block: " + file.getName());
									}

								} catch (JsonSyntaxException e) {
									MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in custom block: " + e);

								}
							} catch (IOException e) {
								MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in custom block: " + e);
							}
						}
					}
				}
			}
		}
	}

	private void createDefault() {
		File dir = new File(MeteorClient.FOLDER, "customblocks");
		if (!dir.exists()) {
			dir.mkdir();
		}
		String world_name = Utils.getWorldName();
		File dir2 = new File(dir, world_name);
		if (!dir2.exists()) {
			dir2.mkdir();

			CustomBlockData hologramData = new CustomBlockData(new BlockPos(0, 64, 0), 1);
			hologramData.dimension = PlayerUtils.getDimension().name();
			hologramData.world = world_name;
			String json = gson.toJson(hologramData);

			File file = new File(dir2.getPath(), "0.json");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fileWriter = new FileWriter(file);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.print(json);
				printWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
