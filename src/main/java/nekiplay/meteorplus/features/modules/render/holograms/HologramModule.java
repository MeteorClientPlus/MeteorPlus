package nekiplay.meteorplus.features.modules.render.holograms;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import nekiplay.Main;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HologramModule extends Module {
	public HologramModule() {
		super(Categories.Render, "holograms", "Create own holograms");
	}
	public Gson gson = new Gson();

	public List<HologramDataListed> allHolograms = new ArrayList<HologramDataListed>();
	public List<HologramDataListed> inWorldHolograms = new ArrayList<HologramDataListed>();

	@Override
	public void onActivate() {
		super.onActivate();

		createDefault();
		load();
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		inWorldHolograms.clear();
		Dimension dim = PlayerUtils.getDimension();
		for (HologramDataListed hologramData : allHolograms) {
			if (hologramData.world.equals(Utils.getWorldName()) && hologramData.dimension.equals(dim.name())) {
				inWorldHolograms.add(hologramData);
			}
		}
	}

	@EventHandler
	private void on2DRender(Render2DEvent event) {
		Vec3d camera_pos = mc.gameRenderer.getCamera().getPos();
		for (HologramDataListed hologramData : inWorldHolograms) {
			Vector3d pos = new Vector3d(hologramData.x, hologramData.y, hologramData.z);
			if (pos.distance(camera_pos.x, camera_pos.y, camera_pos.z) <= hologramData.max_render_distance) {
				if (NametagUtils.to2D(pos, hologramData.scale, hologramData.distanceScaling)) {
					TextRenderer text = TextRenderer.get();
					NametagUtils.begin(pos, event.drawContext);
					text.beginBig();

					String hologram_text = hologramData.text;
					double hologramWidth = text.getWidth(hologram_text, true);
					double heightDown = text.getHeight(true);

					double widthHalf = hologramWidth / 2;


					double hX = -widthHalf;
					double hY = -heightDown;

					text.render(hologram_text, hX, hY, hologramData.color, true);
					for (HologramData hologramData1 : hologramData.other_holograms) {
						text.render(hologramData1.text, hX - hologramData1.x, hY - hologramData1.y, hologramData1.color, true);
						if (hologramData1.item_id != 0) {
							Item item = Item.byRawId(hologramData1.item_id);
							RenderUtils.drawItem(event.drawContext, item.getDefaultStack(), (int) ((int) hX - hologramData1.x), (int) ((int) 0 - hologramData1.y), hologramData1.item_scale, true);
						}
					}

					text.end();
					if (hologramData.item_id != 0) {
						Item item = Item.byRawId(hologramData.item_id);
						RenderUtils.drawItem(event.drawContext, item.getDefaultStack(), (int) hX, (int) 0, hologramData.item_scale, true);
					}
					NametagUtils.end(event.drawContext);
				}
			}
		}
	}

	private void load() {
		File dir = new File(MeteorClient.FOLDER, "holograms");
		if (dir.exists()) {
			String world_name = Utils.getWorldName();
			File dir2 = new File(dir, world_name);
			if (dir2.exists()) {
				allHolograms.clear();
				File[] files = dir2.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.exists()) {
							MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " Loading hologram: " + file.getName());
							try {
								BufferedReader reader = Files.newBufferedReader(Path.of(file.toURI()), StandardCharsets.UTF_8);
								try {
									String json = reader.lines().collect(Collectors.joining());
									HologramDataListed hologramData = gson.fromJson(json, HologramDataListed.class);
									if (hologramData != null) {
										allHolograms.add(hologramData);
										MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " Success loaded hologram: " + file.getName());
									}

								} catch (JsonSyntaxException e) {
									MeteorPlusAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in hologram: " + e);

								}
							} catch (IOException e) {
								MeteorPlusAddon.LOG.error(Main.METEOR_LOGPREFIX + " Error in hologram: " + e);
							}
						}
					}
				}
			}
		}
	}

	private void createDefault() {
		File dir = new File(MeteorClient.FOLDER, "holograms");
		if (!dir.exists()) {
			dir.mkdir();
		}
		String world_name = Utils.getWorldName();
		File dir2 = new File(dir, world_name);
		if (!dir2.exists()) {
			dir2.mkdir();

			HologramDataListed hologramData = new HologramDataListed(new BlockPos(0, 64, 0), "Spawn", world_name, PlayerUtils.getDimension(), Color.RED, 16);
			HologramData hologramData2 = new HologramData(new BlockPos(0, 15, 0), PlayerUtils.getDimension().name(), world_name, PlayerUtils.getDimension(), Color.RED, 16);
			hologramData.other_holograms.add(hologramData2);
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
