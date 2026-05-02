package nekiplay.meteorplus.features.modules.world.zones;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.world.phys.AABB;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZonesModule extends Module {
	public ZonesModule() {
		super(Categories.World, "Zones", "Create custom zones for blocking baritone settings in zone");
	}
	public Gson gson = new Gson();
	public List<ZoneData> allZones = new ArrayList<ZoneData>();
	public List<ZoneData> inWorldZones = new ArrayList<ZoneData>();

	@Override
	public void onActivate() {
		super.onActivate();

		load();
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		inWorldZones.clear();
		Dimension dim = PlayerUtils.getDimension();
		for (ZoneData hologramData : allZones) {
			if (hologramData.world.equals(Utils.getWorldName()) && hologramData.dimension.equals(dim.name())) {
				inWorldZones.add(hologramData);
			}
		}
	}

	private void render(AABB box, Color sides, Color lines, ShapeMode shapeMode, Render3DEvent event) {
		event.renderer.box(
			box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, sides, lines, shapeMode, 0);
	}
	@EventHandler
	private void onRender(Render3DEvent event) {
		for (ZoneData zoneData : inWorldZones) {
			if (zoneData.showBoundingBox) {
				render(new AABB(zoneData.x_start, zoneData.y_start, zoneData.z_start, zoneData.x_end, zoneData.y_end, zoneData.z_end), Color.WHITE, Color.WHITE, ShapeMode.Both, event);
			}
		}
	}

	@EventHandler
	private void onStartBlockBreaking(StartBreakingBlockEvent event) {

	}

	private void load() {
		File dir = new File(MeteorClient.FOLDER, "zones");
		if (dir.exists()) {
			String world_name = Utils.getWorldName();
			File dir2 = new File(dir, world_name);
			if (dir2.exists()) {
				allZones.clear();
				File[] files = dir2.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.exists()) {
							MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Loading hologram: " + file.getName());
							try {
								BufferedReader reader = Files.newBufferedReader(Path.of(file.toURI()), StandardCharsets.UTF_8);
								try {
									String json = reader.lines().collect(Collectors.joining());
									ZoneData hologramData = gson.fromJson(json, ZoneData.class);
									if (hologramData != null) {
										allZones.add(hologramData);
										MeteorPlusAddon.LOG.info(MeteorPlusAddon.METEOR_LOGPREFIX + " Success loaded zone: " + file.getName());
									}

								} catch (JsonSyntaxException e) {
									MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in zone: " + e);

								}
							} catch (IOException e) {
								MeteorPlusAddon.LOG.error(MeteorPlusAddon.METEOR_LOGPREFIX + " Error in zone: " + e);
							}
						}
					}
				}
			}
		}
	}
}
