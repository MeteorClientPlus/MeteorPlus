package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import nekiplay.meteorplus.utils.ElytraUtils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EclipCommand extends Command {
	public EclipCommand() {
		super("eclip", "Elyta clip need elytra bypass most anticheats");
	}

	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
			ClientPlayerEntity player = mc.player;
			assert player != null;
			double blocks2 = context.getArgument("blocks", Double.class);
			if (work()) {
				blocks = blocks2;
				MeteorClient.EVENT_BUS.subscribe(this);
			}
			else {
				ticks = 0;
			}
			return SINGLE_SUCCESS;
		}));
		builder.then(literal("up").executes(c -> {
			if (work()) {
				blocks = findBlock(true, 15);
				MeteorClient.EVENT_BUS.subscribe(this);
			}
			else {
				ticks = 0;
			}
			return SINGLE_SUCCESS;
		}));
		builder.then(literal("down").executes(c -> {
			if (work()) {
				blocks = findBlock(false, 15);
				MeteorClient.EVENT_BUS.subscribe(this);
			}
			else {
				ticks = 0;
			}
			return SINGLE_SUCCESS;
		}));

	}
	private boolean work() {
		ClientPlayerEntity player = mc.player;
		assert player != null;
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (elytra.found()) {
			ticks = 0;
			return true;
		}
		else {
			error(Names.get(Items.ELYTRA) + " not found");
			return false;
		}
	}
	private Block getBlock(BlockPos pos) {
		return mc.player.getWorld().getBlockState(pos).getBlock();
	}

	private double findBlock(boolean up, int maximum) {
		if (up) {
			BlockPos pos = mc.player.getBlockPos();
			for (int i = maximum; i >= 0; i--) {
				if (getBlock(pos.add(0, i, 0)) == Blocks.AIR
					&& getBlock(pos.add(0, i + 1, 0)) == Blocks.AIR
					&& getBlock(pos.add(0, i - 1, 0)) != Blocks.AIR
				) {
					return i;
				}
			}
		}
		else {
			BlockPos pos = mc.player.getBlockPos();
			for (int i = -maximum; i <= 0; i++) {
				if (getBlock(pos.add(0, i, 0)) == Blocks.AIR
					&& getBlock(pos.add(0, i + 1, 0)) == Blocks.AIR
					&& getBlock(pos.add(0, i - 1, 0)) != Blocks.AIR
				) {
					return i;
				}
			}
		}
		return 0;
	}

	private int ticks = 0;
	private int slot = -1;
	private double blocks = 0;

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		clip(blocks);
	}

	private void clip(double blocks) {
		if (blocks != 0) {
			ClientPlayerEntity player = mc.player;
			assert player != null;
			switch (ticks) {
				case 0: {
					FindItemResult elytra = InvUtils.find(Items.ELYTRA);
					slot = elytra.slot();
					InvUtils.move().from(slot).toArmor(2);
					ticks++;
				}
				case 1: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 2: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 3: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 4: {
					player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + blocks, player.getZ(), false));
					ticks++;
				}
				case 5: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 6: {
					ticks = 0;
					InvUtils.move().fromArmor(2).to(slot);
					MeteorClient.EVENT_BUS.unsubscribe(this);
				}
			}
		}
		else {
			MeteorClient.EVENT_BUS.unsubscribe(this);
		}
	}
}
