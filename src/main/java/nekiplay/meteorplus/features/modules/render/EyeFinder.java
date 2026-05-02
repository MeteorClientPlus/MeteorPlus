package nekiplay.meteorplus.features.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Iterator;

public class EyeFinder extends Module {
	public EyeFinder() {
		super(Categories.Render, "eye-finder", "Find block player look.");
	}

	private final HashMap<Entity, HitResult> resultMap = new HashMap<Entity, HitResult>();

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgBlock = settings.createGroup("Block");

	public void drawLine(Render3DEvent event, Entity entity, HitResult result) {
		event.renderer.line(entity.getEyePosition().x, entity.getEyePosition().y, entity.getEyePosition().z, result.getLocation().x, result.getLocation().y, result.getLocation().z, lineColor.get());
	}


	private final Setting<ShapeMode> shapeMode = sgBlock.add(new EnumSetting.Builder<ShapeMode>()
		.name("shape-mode")
		.description("How the shapes are rendered.")
		.defaultValue(ShapeMode.Both)
		.build()
	);

	private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
		.name("line-color")
		.description("The line color.")
		.defaultValue(new SettingColor(255, 255, 255, 255))
		.build()
	);


	private final Setting<SettingColor> sideColor = sgBlock.add(new ColorSetting.Builder()
		.name("side-color")
		.description("The side color.")
		.defaultValue(new SettingColor(255, 255, 255, 50))
		.build()
	);


	public void renderBlock(Render3DEvent event, Entity entity, HitResult result) {
		if (result instanceof BlockHitResult blockHitResult) {
			if (blockHitResult.getType() == HitResult.Type.BLOCK || blockHitResult.getType() == HitResult.Type.MISS) {
				BlockPos bp = new BlockPos(blockHitResult.getBlockPos());
				BlockState state = mc.level.getBlockState(bp);
				Direction side = blockHitResult.getDirection();
				VoxelShape shape = state.getShape(mc.level, bp);

				if (shape.isEmpty()) return;
				AABB box = shape.bounds();

				if (side == Direction.UP || side == Direction.DOWN) {
					event.renderer.sideHorizontal(bp.getX() + box.minX, bp.getY() + (side == Direction.DOWN ? box.minY : box.maxY), bp.getZ() + box.minZ, bp.getX() + box.maxX, bp.getZ() + box.maxZ, sideColor.get(), lineColor.get(), shapeMode.get());
				} else if (side == Direction.SOUTH || side == Direction.NORTH) {
					double z = side == Direction.NORTH ? box.minZ : box.maxZ;
					event.renderer.sideVertical(bp.getX() + box.minX, bp.getY() + box.minY, bp.getZ() + z, bp.getX() + box.maxX, bp.getY() + box.maxY, bp.getZ() + z, sideColor.get(), lineColor.get(), shapeMode.get());
				} else {
					double x = side == Direction.WEST ? box.minX : box.maxX;
					event.renderer.sideVertical(bp.getX() + x, bp.getY() + box.minY, bp.getZ() + box.minZ, bp.getX() + x, bp.getY() + box.maxY, bp.getZ() + box.maxZ, sideColor.get(), lineColor.get(), shapeMode.get());
				}
			}
		}
	}

	@EventHandler
	public void tickEvent(TickEvent.Pre event) {
		if (mc.level != null) {
			Iterator<Entity> entityIterator = mc.level.entitiesForRendering().iterator();
			HashMap<Entity, HitResult> cachMap = new HashMap<Entity, HitResult>();
			while (entityIterator.hasNext()) {
				Entity entity = entityIterator.next();
				if (entity instanceof Player && entity != mc.player) {
					HitResult result = entity.pick(5, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), false);
					cachMap.put(entity, result);
				}
			}
			resultMap.clear();
			resultMap.putAll(cachMap);
		}
	}

	@EventHandler
	public void on3dRender(Render3DEvent event) {
		for (Entity entity : resultMap.keySet()) {
			HitResult r = resultMap.get(entity);
			drawLine(event, entity, r);
			renderBlock(event, entity, r);
		}
	}
}
