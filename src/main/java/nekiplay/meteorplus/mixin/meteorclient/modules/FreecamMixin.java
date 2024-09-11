package nekiplay.meteorplus.mixin.meteorclient.modules;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.utils.RaycastUtils;
import net.minecraft.block.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.misc.input.Input.isPressed;
import static nekiplay.meteorplus.MeteorPlusAddon.HUD_TITLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

@Mixin(Freecam.class)
public class FreecamMixin {
	@Unique
	private final Freecam freecam = (Freecam) (Object) this;
	@Unique
	private final SettingGroup freecamMeteorPlusSetting = freecam.settings.createGroup(HUD_TITLE);
	@Unique
	private final Setting<Boolean> moveBaritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-move-control")
		.description("click bind to set the destination on the selected block. Right mouse click to cancel.")
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneMoveKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-move-keybind")
		.description("The bind for move.")
		.visible(moveBaritoneControl::get)
		.defaultValue(Keybind.fromKey(GLFW_MOUSE_BUTTON_LEFT))
		.build()
	);

	@Unique
	private final Setting<Boolean> blinkBaritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-blink-move-control")
		.description("Click bind to move to point in blink.")
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneMoveBlinkKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-blink-move-keybind")
		.description("The bind for move in blink.")
		.visible(blinkBaritoneControl::get)
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneStopKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-stop-keybind")
		.description("The bind for stop baritone actions.")
		.visible(() -> blinkBaritoneControl.get() || moveBaritoneControl.get())
		.defaultValue(Keybind.fromKey(GLFW_MOUSE_BUTTON_RIGHT))
		.build()
	);

	@Unique
	private final Blink blink = Modules.get().get(Blink.class);
	@Unique
	private boolean isBlinkMoving = false;

	@Unique
	private BlockPos tryGetValidPos(BlockPos pos) {
        assert mc.world != null;
        BlockState state = mc.world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.FERN ||
			block == Blocks.SHORT_GRASS ||
			block == Blocks.TALL_GRASS ||
			block == Blocks.GLOW_LICHEN ||
			block == Blocks.DEAD_BUSH ||
			block == Blocks.SNOW ||
			block == Blocks.MOSS_CARPET ||
			// Decorations
			block == Blocks.TORCH ||
			block == Blocks.WALL_TORCH ||
			// Signs
			block == Blocks.SPRUCE_SIGN ||
			block == Blocks.ACACIA_SIGN ||
			block == Blocks.BIRCH_SIGN ||
			block == Blocks.CHERRY_SIGN ||
			block == Blocks.BAMBOO_SIGN ||
			block == Blocks.OAK_SIGN ||
			block == Blocks.CRIMSON_SIGN ||
			block == Blocks.DARK_OAK_SIGN ||
			block == Blocks.JUNGLE_SIGN ||
			block == Blocks.MANGROVE_SIGN ||
			block == Blocks.WARPED_SIGN ||
			// Wall signs
			block == Blocks.SPRUCE_WALL_SIGN ||
			block == Blocks.ACACIA_WALL_SIGN ||
			block == Blocks.BIRCH_WALL_SIGN ||
			block == Blocks.CHERRY_WALL_SIGN ||
			block == Blocks.BAMBOO_WALL_SIGN ||
			block == Blocks.OAK_WALL_SIGN ||
			block == Blocks.CRIMSON_WALL_SIGN ||
			block == Blocks.DARK_OAK_WALL_SIGN ||
			block == Blocks.JUNGLE_WALL_SIGN ||
			block == Blocks.MANGROVE_WALL_SIGN ||
			block == Blocks.WARPED_WALL_SIGN ||
			// Mushroms
			block == Blocks.BROWN_MUSHROOM ||
			block == Blocks.RED_MUSHROOM ||
			block == Blocks.CRIMSON_FUNGUS ||
			block == Blocks.WARPED_FUNGUS ||
			// Small flowers
			block == Blocks.DANDELION ||
			block == Blocks.POPPY ||
			block == Blocks.BLUE_ORCHID ||
			block == Blocks.ALLIUM ||
			block == Blocks.AZURE_BLUET ||
			block == Blocks.RED_TULIP ||
			block == Blocks.ORANGE_TULIP ||
			block == Blocks.WHITE_TULIP ||
			block == Blocks.PINK_TULIP ||
			block == Blocks.OXEYE_DAISY ||
			block == Blocks.CORNFLOWER ||
			block == Blocks.LILY_OF_THE_VALLEY ||
			block == Blocks.TORCHFLOWER ||
			block == Blocks.PINK_PETALS ||
			block == Blocks.SUGAR_CANE ||
			// Crops
			block == Blocks.NETHER_WART ||
			block == Blocks.PITCHER_CROP ||
			block == Blocks.TORCHFLOWER_CROP ||
			block == Blocks.BEETROOTS ||
			block == Blocks.WHEAT ||
			block == Blocks.CARROTS ||
			block == Blocks.POTATOES ||
			block == Blocks.MELON_STEM ||
			block == Blocks.PUMPKIN_STEM ||
			// Saplings
			block == Blocks.SPRUCE_SAPLING ||
			block == Blocks.ACACIA_SAPLING ||
			block == Blocks.BIRCH_SAPLING ||
			block == Blocks.BAMBOO_SAPLING ||
			block == Blocks.CHERRY_SAPLING ||
			block == Blocks.DARK_OAK_SAPLING ||
			block == Blocks.JUNGLE_SAPLING ||
			block == Blocks.OAK_SAPLING ||
			// Rails
			block == Blocks.RAIL ||
			block == Blocks.ACTIVATOR_RAIL ||
			block == Blocks.POWERED_RAIL ||
			block == Blocks.DETECTOR_RAIL

		) {
			return pos;
		}
		else {
			return pos.up();
		}
	}

	@Unique
	private BlockPos rayCastClicked() {
		BlockPos blockPos = null;
		Vec3d rotationVector = RaycastUtils.getRotationVector((float) freecam.getPitch(mc.getRenderTickCounter().getTickDelta(true)), (float) freecam.getYaw(mc.getRenderTickCounter().getTickDelta(true)));
		Vec3d pos = new Vec3d(freecam.pos.x, freecam.pos.y, freecam.pos.z);
		HitResult result = RaycastUtils.raycast(pos, rotationVector, 64 * 4, mc.getRenderTickCounter().getTickDelta(true), true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult) result;
			blockPos = blockHitResult.getBlockPos();
		}
		return blockPos;
	}

	@Unique
	private void Work(Cancellable event) {
		if (baritoneMoveBlinkKey.get().isPressed()) {
			BlockPos clicked = rayCastClicked();

			if (blinkBaritoneControl.get()) {

				if (clicked == null) return;

				if (mc.world == null) return;

				BlockState state = mc.world.getBlockState(clicked);

				if (state.isAir()) return;
				isBlinkMoving = true;
				GoalBlock goal = new GoalBlock(tryGetValidPos(clicked));
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(null);
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);

				event.cancel();
			}
		}
		if (baritoneMoveKey.get().isPressed()) {
			BlockPos clicked = rayCastClicked();
			if (clicked == null) return;

			if (mc.world == null) return;

			BlockState state = mc.world.getBlockState(clicked);

			if (state.isAir()) return;

			if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing())
				BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().forceCancel();


			GoalBlock goal = new GoalBlock(tryGetValidPos(clicked));
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(null);
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);

			event.cancel();
		}

		if (baritoneStopKey.get().isPressed()) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().forceCancel();
			if (blink != null) {
				if (blink.isActive()) {
					blink.toggle();
					isBlinkMoving = false;
				}
			}
			event.cancel();
		}
	}

	@Unique
	@EventHandler
	private void onKeyEvent(KeyEvent event)
	{
		if (mc.world != null && event.action == KeyAction.Press) {
			Work(event);
		}
	}
	@Unique
	@EventHandler
	private void onMouseButtonEvent(MouseButtonEvent event) {
		if (mc.world != null && event.action == KeyAction.Press) {
			Work(event);
		}
	}

	@Unique
	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		if (mc.world != null && blinkBaritoneControl.get() && blink != null) {
			if (isBlinkMoving && (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (!blink.isActive()) {
					blink.toggle();
				}
			}
			if (isBlinkMoving && (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (blink.isActive()) {
					blink.toggle();
					isBlinkMoving = false;
				}
			}
		}
	}
}
