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
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.HashMap;
import java.util.Iterator;

public class EyeFinder extends Module {
	public EyeFinder() {
		super(Categories.Render, "Eye Finder", "Find block player look.");
	}
	private final HashMap<Entity, HitResult> resultMap = new HashMap<Entity, HitResult>();

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgBlock = settings.createGroup("Block");

	public void drawLine(Render3DEvent event, Entity entity, HitResult result) {
		event.renderer.line(entity.getEyePos().x, entity.getEyePos().y, entity.getEyePos().z, result.getPos().x, result.getPos().y, result.getPos().z, lineColor.get());
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
				BlockState state = mc.world.getBlockState(bp);
				Direction side = blockHitResult.getSide();
				VoxelShape shape = state.getOutlineShape(mc.world, bp);

				if (shape.isEmpty()) return;
				Box box = shape.getBoundingBox();

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
		if (mc.world != null) {
			Iterator<Entity> entityIterator = mc.world.getEntities().iterator();
			HashMap<Entity, HitResult> cachMap = new HashMap<Entity, HitResult>();
			while (entityIterator.hasNext()) {
				Entity entity = entityIterator.next();
				if (entity instanceof PlayerEntity && entity != mc.player) {
					HitResult result = entity.raycast(5, mc.getRenderTickCounter().getTickDelta(true), false);
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
