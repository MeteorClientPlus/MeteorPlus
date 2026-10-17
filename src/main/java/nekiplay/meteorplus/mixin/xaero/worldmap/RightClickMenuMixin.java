package nekiplay.meteorplus.mixin.xaero.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import nekiplay.MixinPlugin;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.GuiRightClickMenu;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = GuiRightClickMenu.class, remap = false)
public abstract class RightClickMenuMixin {
	@Unique
	private static final Field METEORPLUS_SCALE = meteorplus$findField("scale");
	@Unique
	private static final Field METEORPLUS_CAMERA_X = meteorplus$findField("cameraX");
	@Unique
	private static final Field METEORPLUS_CAMERA_Z = meteorplus$findField("cameraZ");
	@Unique
	private static final Field METEORPLUS_RIGHT_CLICK_X = meteorplus$findField("rightClickX");
	@Unique
	private static final Field METEORPLUS_RIGHT_CLICK_Y = meteorplus$findField("rightClickY");
	@Unique
	private static final Field METEORPLUS_RIGHT_CLICK_Z = meteorplus$findField("rightClickZ");

	@Redirect(
		method = "getMenu(Lxaero/map/gui/IRightClickableElement;Lxaero/map/gui/GuiMap;III)Lxaero/map/gui/dropdown/rightclick/GuiRightClickMenu;",
		at = @At(value = "INVOKE", target = "Lxaero/map/gui/IRightClickableElement;getRightClickOptions()Ljava/util/ArrayList;"),
		remap = false
	)
	private static ArrayList<RightClickOption> meteorplus$addOptions(IRightClickableElement target) {
		ArrayList<RightClickOption> options = target.getRightClickOptions();
		if (!(target instanceof GuiMap guiMap)) return options;

		Modules modules = Modules.get();
		if (modules == null) return options;
		MapIntegration integration = modules.get(MapIntegration.class);
		if (integration == null || !integration.isActive()) return options;

		MapProcessor mapProcessor = guiMap.getMapProcessor();
		if (Boolean.TRUE.equals(integration.showBlock.get())) {
			meteorplus$addBlockOption(guiMap, mapProcessor, integration, options);
		}
		meteorplus$addBaritoneOptions(guiMap, integration, options);
		return options;
	}

	@Unique
	private static void meteorplus$addBlockOption(GuiMap guiMap, MapProcessor mapProcessor, MapIntegration integration, ArrayList<RightClickOption> options) {
		double scale = meteorplus$readDouble(METEORPLUS_SCALE, guiMap);
		if (scale == 0.0) return;

		int mouseX = (int) Misc.getMouseX(mc, false) - mc.getWindow().getWidth() / 2;
		int mouseY = (int) Misc.getMouseY(mc, false) - mc.getWindow().getHeight() / 2;
		int blockX = (int) Math.floor((double) mouseX / scale + meteorplus$readDouble(METEORPLUS_CAMERA_X, guiMap));
		int blockZ = (int) Math.floor((double) mouseY / scale + meteorplus$readDouble(METEORPLUS_CAMERA_Z, guiMap));

		int caveLayer = mapProcessor.getCurrentCaveLayer();
		MapRegion region = mapProcessor.getLeafMapRegion(caveLayer, blockX >> 9, blockZ >> 9, false);
		MapTileChunk chunk = region == null ? null : region.getChunk(blockX >> 6 & 7, blockZ >> 6 & 7);
		MapTile tile = chunk == null ? null : chunk.getTile(blockX >> 4 & 3, blockZ >> 4 & 3);
		if (tile == null) return;

		String blockName = Names.get(tile.getBlock(blockX & 15, blockZ & 15).getState().getBlock());
		options.add(2, new RightClickOption(blockName, options.size(), guiMap) {
			@Override
			public void onAction(Screen screen) {
			}
		});
	}

	@Unique
	private static void meteorplus$addBaritoneOptions(GuiMap guiMap, MapIntegration integration, ArrayList<RightClickOption> options) {
		int x = meteorplus$readInt(METEORPLUS_RIGHT_CLICK_X, guiMap);
		int y = meteorplus$readInt(METEORPLUS_RIGHT_CLICK_Y, guiMap);
		int z = meteorplus$readInt(METEORPLUS_RIGHT_CLICK_Z, guiMap);
		boolean canElytra = integration.baritoneElytra.get() && integration.baritoneGoto.get()
			&& PlayerUtils.getDimension() == Dimension.Nether && y - 1 > 0 && y < 128;

		if (!MixinPlugin.isXaeroPlusMapPresent && integration.baritoneGoto.get()) {
			options.add(3, meteorplus$goalOption(options.size(), guiMap, x, y, z, false));
			options.add(4, meteorplus$goalOption(options.size(), guiMap, x, y, z, true));
		}
		if (canElytra) options.add(3, meteorplus$elytraOption(options.size(), guiMap, x, y, z));
	}

	@Unique
	private static RightClickOption meteorplus$goalOption(int index, GuiMap guiMap, int x, int y, int z, boolean path) {
		String key = path ? "gui.world_map.baritone_path_here" : "gui.world_map.baritone_goal_here";
		return new RightClickOption(I18n.get(key), index, guiMap) {
			@Override
			public void onAction(Screen screen) {
				GoalBlock goal = new GoalBlock(new BlockPos(x, y, z).above());
				if (path) BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				else BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
			}
		};
	}

	@Unique
	private static RightClickOption meteorplus$elytraOption(int index, GuiMap guiMap, int x, int y, int z) {
		return new RightClickOption(I18n.get("gui.world_map.baritone_elytra_here"), index, guiMap) {
			@Override
			public void onAction(Screen screen) {
				GoalBlock goal = new GoalBlock(new BlockPos(x, y, z).above());
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
				BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("elytra");
			}
		};
	}

	@Unique
	private static Field meteorplus$findField(String name) {
		try {
			Field field = GuiMap.class.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Unique
	private static double meteorplus$readDouble(Field field, GuiMap guiMap) {
		try {
			return field.getDouble(guiMap);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to read Xaero map state", e);
		}
	}

	@Unique
	private static int meteorplus$readInt(Field field, GuiMap guiMap) {
		try {
			return field.getInt(guiMap);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to read Xaero map state", e);
		}
	}
}
