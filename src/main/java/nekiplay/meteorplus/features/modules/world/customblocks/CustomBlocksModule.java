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
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import nekiplay.Main;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.features.modules.render.holograms.HologramData;
import nekiplay.meteorplus.features.modules.render.holograms.HologramDataListed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiling.jfr.event.ChunkRegionEvent;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CustomBlocksModule extends Module {
	public CustomBlocksModule() {
		super(Categories.World, "Custom Blocks", "Place custom blocks in world, extension for Holograms");
	}
	public Gson gson = new Gson();
	public HashMap<PosData, CustomBlockData> allBlocks = new HashMap<PosData, CustomBlockData>();

	private final SettingGroup settingsGroup = settings.getDefaultGroup();

	@Override
	public void onActivate() {
		super.onActivate();

		createDefault();
		load();
	}

	public final Setting<Boolean> noSetIfAir = settingsGroup.add(new BoolSetting.Builder()
		.name("no-set-if-air")
		.description("No set custom block if original block equals air.")
		.defaultValue(true)
		.build()
	);

	@EventHandler
	private void onChunkData(ChunkDataEvent event) {
		WorldChunk chunk = event.chunk();
		String dimension = PlayerUtils.getDimension().name();
		for (int x = chunk.getPos().getStartX(); x <= chunk.getPos().getEndX(); x++) {
			for (int z = chunk.getPos().getStartZ(); z <= chunk.getPos().getEndZ(); z++) {
				int height = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).get(x - chunk.getPos().getStartX(), z - chunk.getPos().getStartZ());

				for (int y = mc.world.getBottomY(); y < height; y++) {

					BlockPos pos = new BlockPos(x, y, z);

					BlockState bs = chunk.getBlockState(pos);
					Block original = bs.getBlock();

					CustomBlockData customBlockData = allBlocks.get(new PosData(pos));
					if (customBlockData != null) {
						if (customBlockData.dimension.equals(dimension)) {
							Item item = Item.byRawId(customBlockData.block_id);
							Block block = Block.getBlockFromItem(item);
							if (isValidBlockForReplace(original)) {
								mc.world.setBlockState(pos, block.getDefaultState());
							}
						}
					}
				}
			}
		}

	}

	private boolean isValidBlockForReplace(Block block) {
		if (block != Blocks.AIR && noSetIfAir.get()) {
			return true;
		} else if (!noSetIfAir.get()) {
			return true;
		}

		return false;
	}

	@EventHandler
	private void onBlockUpdate(BlockUpdateEvent event) {
		String dimension = PlayerUtils.getDimension().name();
		CustomBlockData customBlockData = allBlocks.get(new PosData(event.pos));
		if (customBlockData != null) {
			if (customBlockData.dimension.equals(dimension)) {
				Item item = Item.byRawId(customBlockData.block_id);
				Block original = event.newState.getBlock();
				Block block = Block.getBlockFromItem(item);
				if (isValidBlockForReplace(original)) {
					mc.world.setBlockState(event.pos, block.getDefaultState());
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
							MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " Loading hologram: " + file.getName());
							try {
								BufferedReader reader = Files.newBufferedReader(Path.of(file.toURI()), StandardCharsets.UTF_8);
								try {
									String json = reader.lines().collect(Collectors.joining());
									CustomBlockData hologramData = gson.fromJson(json, CustomBlockData.class);
									if (hologramData != null) {
										for (PosData posData : hologramData.positions) {
											allBlocks.put(posData, hologramData);
										}
										MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " Success loaded custom block: " + file.getName());
									}

								} catch (JsonSyntaxException e) {
									MeteorPlusAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in custom block: " + e);

								}
							} catch (IOException e) {
								MeteorPlusAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in custom block: " + e);
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
