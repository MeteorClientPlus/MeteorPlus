package nekiplay.meteorplus.features.modules.world;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayDeque;

public class GhostBlockFixer extends Module {
	public GhostBlockFixer() {
		super(Categories.World, "auto-ghost-block-fixer", "Automatically fix ghost blocks.");
	}
	private final SettingGroup GBSettings = settings.getDefaultGroup();

	private final Setting<Integer> delay = GBSettings.add(new IntSetting.Builder()
		.name("Fixer delay")
		.description("Delay for block fixing.")
		.defaultValue(50)
		.range(1, 250)
		.sliderRange(1, 250)
		.build()
	);

	private final Setting<Integer> range = GBSettings.add(new IntSetting.Builder()
		.name("Fixer range")
		.description("Range to block.")
		.defaultValue(6)
		.range(1, 80)
		.sliderRange(1, 80)
		.build()
	);

	private final ArrayDeque<BlockPos> blocks = new ArrayDeque<>();
	@EventHandler
	public void onBlockBreak(BreakBlockEvent block)
	{
		blocks.add(block.blockPos);
	}

	@Override
	public void onActivate() {
		millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	@Override
	public void onDeactivate() {
		blocks.clear();
	}
	@EventHandler
	public void onLeave(GameLeftEvent event)
	{
		blocks.clear();
	}
	private long millis = 0;
	@EventHandler
	public void onTick(TickEvent.Post event)
	{
		if (!blocks.isEmpty()) {
			ClientPlayNetworkHandler conn = mc.getNetworkHandler();
			ClientPlayerEntity player = mc.player;
			if (conn != null && player != null) {
				if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= millis) {
					BlockPos block = blocks.peek();
					assert block != null;
					assert mc.world != null;
					double distance = mc.player.squaredDistanceTo(block.getX(), block.getY(), block.getZ());
					BlockState state = mc.world.getBlockState(block);
					if (distance <= range.get() && state.isAir()) {
						millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + delay.get();
						PlayerActionC2SPacket packet = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, block, Direction.UP, 0);
						conn.sendPacket(packet);
						blocks.remove();
					}
					else if (!state.isAir()) {
						blocks.remove();
					}
				}
			}
		}
	}
}
