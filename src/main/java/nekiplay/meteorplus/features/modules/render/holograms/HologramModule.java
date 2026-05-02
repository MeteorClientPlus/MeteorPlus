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
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import org.meteordev.starscript.Script;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HologramModule extends Module {
	public HologramModule() {
		super(Categories.Render, "holograms", "Create own holograms");
	}
	public Gson gson = new Gson();

	public List<HologramDataListed> allHolograms = new ArrayList<HologramDataListed>();
	public List<HologramDataListed> inWorldHolograms = new ArrayList<HologramDataListed>();
	public HashMap<String, Script> scripts = new HashMap<String, Script>();


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
			if (hologramData.dimension.equals(dim.name())) {
				inWorldHolograms.add(hologramData);
			}
		}
	}

	@EventHandler
	private void on2DRender(Render2DEvent event) {
		Vec3 camera_pos = mc.gameRenderer.getMainCamera().position();
		for (HologramDataListed hologramData : inWorldHolograms) {
			Vector3d pos = new Vector3d(hologramData.x, hologramData.y, hologramData.z);
			if (pos.distance(camera_pos.x, camera_pos.y, camera_pos.z) <= hologramData.max_render_distance) {
				if (NametagUtils.to2D(pos, hologramData.scale)) {
					TextRenderer text = TextRenderer.get();
					NametagUtils.begin(pos);

					String hologram_text = hologramData.text;
					double hologramWidth = text.getWidth(hologram_text, true);
					double heightDown = text.getHeight(true);

					double widthHalf = hologramWidth / 2;


					double hX = -widthHalf;
					double hY = -heightDown;

					var script = scripts.get(hologram_text);
					text.beginBig();
					if (script != null) {
						text.render(MeteorStarscript.run(script), hX, hY, hologramData.color, true);
						for (HologramData hologramData1 : hologramData.other_holograms) {
							text.render(MeteorStarscript.run(scripts.get(hologramData1.text)), hX - hologramData1.x, hY - hologramData1.y, hologramData1.color, true);
							if (hologramData1.item_id != 0) {
								Item item = Item.byId(hologramData1.item_id);
								RenderUtils.drawItem(event.drawContext, item.getDefaultInstance(), (int) ((int) hX - hologramData1.x), (int) ((int) 0 - hologramData1.y), hologramData1.item_scale, true);
							}
						}
						if (hologramData.item_id != 0) {
							Item item = Item.byId(hologramData.item_id);
							RenderUtils.drawItem(event.drawContext, item.getDefaultInstance(), (int) hX, (int) 0, hologramData.item_scale, true);
						}
					}
					text.end();
					NametagUtils.end();
				}
			}
		}
	}

	private void loadScripts(HologramDataListed hologramDataListed) {
		if (!scripts.containsKey(hologramDataListed.text)) {
			scripts.put(hologramDataListed.text, MeteorStarscript.compile(hologramDataListed.text));
		}
		for (HologramData hologramData1 : hologramDataListed.other_holograms) {
			if (!scripts.containsKey(hologramData1.text)) {
				scripts.put(hologramData1.text, MeteorStarscript.compile(hologramData1.text));
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
				scripts.clear();
				File[] files = dir2.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.exists()) {
							MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Loading hologram: " + file.getName());
							try {
								BufferedReader reader = Files.newBufferedReader(Path.of(file.toURI()), StandardCharsets.UTF_8);
								try {
									String json = reader.lines().collect(Collectors.joining());
									HologramDataListed hologramData = gson.fromJson(json, HologramDataListed.class);
									if (hologramData != null) {
										loadScripts(hologramData);
										allHolograms.add(hologramData);
										MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Success loaded hologram: " + file.getName());
									}
								}
								catch (Exception e) {
									MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in hologram: " + e);
								}
							} catch (IOException e) {
								MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in hologram: " + e);
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

			HologramDataListed hologramData = new HologramDataListed(new BlockPos(0, 64, 0), "Spawn", PlayerUtils.getDimension(), Color.RED, 16);
			HologramData hologramData2 = new HologramData(new BlockPos(0, 15, 0), PlayerUtils.getDimension().name(), PlayerUtils.getDimension(), Color.RED, 16);
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
