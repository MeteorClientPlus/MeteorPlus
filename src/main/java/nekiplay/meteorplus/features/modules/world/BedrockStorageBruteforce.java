package nekiplay.meteorplus.features.modules.world;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class BedrockStorageBruteforce extends Module {
	public BedrockStorageBruteforce() {
		super(Categories.World, "bedrock-storage-BF", "Scan storage's in bedrock from -1 to -4 height for 1.12.2.");
	}

	public ArrayList<BlockPos> scanned = new ArrayList<>();

	private final SettingGroup SCSettings = settings.createGroup("Scanner Settings");

	public final Setting<Boolean> clear_cache_blocks = SCSettings.add(new BoolSetting.Builder()
		.name("clear-cache")
		.description("clear-saved-cache.")
		.defaultValue(false)
		.onChanged(a -> scanned.clear())
		.build()
	);

	private final Setting<Integer> range = SCSettings.add(new IntSetting.Builder()
		.name("Scan range")
		.description("Bruteforce delay min .")
		.defaultValue(8)
		.min(3)
		.sliderRange(8, 64)
		.build()
	);

	private final Setting<Integer> delaymin = SCSettings.add(new IntSetting.Builder()
		.name("Scan delay min")
		.description("Bruteforce delay min .")
		.defaultValue(24)
		.min(0)
		.sliderRange(0, 80)
		.build()
	);

	private final Setting<Integer> delaymax = SCSettings.add(new IntSetting.Builder()
		.name("Scan delay max")
		.description("Bruteforce delay max .")
		.defaultValue(25)
		.min(0)
		.sliderRange(0, 80)
		.build()
	);
	Thread clickerThread = null;
	private boolean scan = true;
	private void stop() {
		if (clickerThread != null && clickerThread.isAlive())
		{

		}
		scan = false;
	}
	@Override
	public void onDeactivate() {
		stop();
	}
	private boolean isAllowScan(BlockPos pos) {
		if (mc.world != null) {
			if (!scanned.contains(pos)) {
				BlockState state = mc.world.getBlockState(pos);
				Block block = state.getBlock();
				return block != Blocks.AIR && block != Blocks.BEDROCK && block != Blocks.LAVA && block != Blocks.WATER;
			}
		}
		return false;
	}
	Dimension dim;
	@Override
	public void onActivate() {
		dim = PlayerUtils.getDimension();
		scan = true;
		clickerThread = new Thread(() -> {
			while (scan)
			{
				assert mc.player != null;
				BlockPos playerPos = mc.player.getBlockPos();
				int ranger = range.get();
				int x = Utils.random(playerPos.getX() - ranger, playerPos.getX() + ranger);
				int y = Utils.random(1, 4);
				int z = Utils.random(playerPos.getZ() - ranger, playerPos.getZ() + ranger);
				BlockPos posible = new BlockPos(x, y, z);
				if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= millis) {
					if (isAllowScan(posible)) {
						ClientPlayNetworkHandler conn = mc.getNetworkHandler();
						if (conn != null) {
							last = posible;
							scanned.add(posible);
							PlayerActionC2SPacket abortPacket = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, posible, Direction.UP, 0);
							conn.sendPacket(abortPacket);
							PlayerActionC2SPacket abortPacket2 = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, posible, Direction.UP, 0);
							conn.sendPacket(abortPacket2);
							millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + Utils.random(delaymin.get(), delaymax.get());
						}
					}
				}
			}
		});
		clickerThread.start();
		millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	BlockPos last = null;
	long millis = 0;
	@EventHandler
	private void onRender(Render3DEvent event) {
		if (last != null) {
			BlockPos bp = last;
			assert mc.world != null;
			BlockState state = mc.world.getBlockState(bp);
			VoxelShape shape = state.getOutlineShape(mc.world, bp);
			SettingColor color = new SettingColor(255, 255, 255);
			if (shape.isEmpty()) return;
			for (Box b : shape.getBoundingBoxes()) {
				event.renderer.box(bp.getX() + b.minX, bp.getY() + b.minY, bp.getZ() + b.minZ, bp.getX() + b.maxX, bp.getY() + b.maxY, bp.getZ() + b.maxZ, new SettingColor(255, 255, 255, 255), color, ShapeMode.Lines, 0);
			}
			event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, bp.getX(), bp.getY(), bp.getZ(), color);
		}
	}
	@EventHandler
	private void onGameLeft(GameLeftEvent event) {
		scanned.clear();
		stop();
	}
	@EventHandler
	private void onTickPre(TickEvent.Pre event) {
		if (PlayerUtils.getDimension() != dim) {
			dim = PlayerUtils.getDimension();
			scanned.clear();
		}
	}
}
