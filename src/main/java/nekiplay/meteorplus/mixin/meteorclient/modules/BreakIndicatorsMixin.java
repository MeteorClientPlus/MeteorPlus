package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.mixin.MultiPlayerGameModeAccessor;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.systems.modules.world.PacketMine;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(BreakIndicators.class)
public class BreakIndicatorsMixin extends Module {

	@Unique
	private final SettingGroup sgPercentageRenderPlus = settings.createGroup(MeteorPlusAddon.HUD_TITLE + " Percentage Render");

	@Unique
	public final Setting<Boolean> percentageRender = sgPercentageRenderPlus.add(new BoolSetting.Builder()
		.name("enable-percentage-render")
		.description("Enable percentage text render.")
		.defaultValue(true)
		.build()
	);

	@Unique
	public final Setting<Boolean> packetMinePlus = sgPercentageRenderPlus.add(new BoolSetting.Builder()
		.name("packet-mine")
		.description("Render packet mine blocks.")
		.defaultValue(true)
		.visible(percentageRender::get)
		.build()
	);

	@Unique
	private final Setting<SettingColor> percentageColor = sgPercentageRenderPlus.add(new ColorSetting.Builder()
		.name("percentage-color")
		.description("The color for the percentage text.")
		.defaultValue(new SettingColor(25, 252, 25, 150))
		.visible(percentageRender::get)
		.build()
	);


	public BreakIndicatorsMixin(Category category, String name, String description, String... aliases) {
		super(category, name, description, aliases);
	}

	@Unique
	@EventHandler
	private void on2DRender(Render2DEvent event) {
		float ownBreakingStage = ((MultiPlayerGameModeAccessor) mc.gameMode).meteor$getBreakingProgress();
		BlockPos ownBreakingPos = ((MultiPlayerGameModeAccessor) mc.gameMode).meteor$getCurrentBreakingBlockPos();


		if (ownBreakingPos != null && ownBreakingStage > 0) {

			double shrinkFactor = 1d - ownBreakingStage;


			BlockState state = mc.level.getBlockState(ownBreakingPos);
			VoxelShape shape = state.getShape(mc.level, ownBreakingPos);
			if (shape.isEmpty()) return;

			AABB orig = shape.bounds();

			renderBlock(event, ownBreakingPos, shrinkFactor, orig);

		}
		if (packetMinePlus.get() && !Modules.get().get(PacketMine.class).blocks.isEmpty()) {
			renderPacket(event, Modules.get().get(PacketMine.class).blocks);
		}
	}

	@Unique
	private void renderBlock(Render2DEvent event, BlockPos pos, double shrinkFactor, AABB orig) {
		Vector3d vector3d = new Vector3d(pos.getX() + orig.getCenter().x, pos.getY() + orig.getCenter().y, pos.getZ() + orig.getCenter().z);
		if (percentageRender.get()) {
			if (NametagUtils.to2D(vector3d, 1, true)) {
				TextRenderer text = TextRenderer.get();
				NametagUtils.begin(vector3d, event.graphics);
				text.beginBig(event.graphics);
				String label = String.format("%1$,.0f", shrinkFactor * 100) + "%";

				double hologramWidth = text.getWidth(label, true);
				double heightDown = text.getHeight(true);

				double widthHalf = hologramWidth / 2;


				double hX = -widthHalf;
				double hY = -heightDown;

				text.render(label, hX, hY, percentageColor.get(), true);

				text.end();
				NametagUtils.end(event.graphics);
			}
		}
	}

	@Unique
	private void renderPacket(Render2DEvent event, List<PacketMine.MyBlock> blocks) {
		for (PacketMine.MyBlock block : blocks) {
			if (block.mining && block.progress() != Double.POSITIVE_INFINITY) {
				VoxelShape shape = block.blockState.getShape(mc.level, block.blockPos);
				if (shape == null || shape.isEmpty()) return;

				AABB orig = shape.bounds();

				double progressNormalised = Math.min(1, block.progress());
				double shrinkFactor = 1d - progressNormalised;
				BlockPos pos = block.blockPos;

				renderBlock(event, pos, shrinkFactor, orig);
			}
		}
	}
}
