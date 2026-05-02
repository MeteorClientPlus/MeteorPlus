package nekiplay.meteorplus.features.commands;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GotoPlusCommand extends Command {
	public GotoPlusCommand() {
		super("gotoplus", "Baritone goto to selected type");
	}

	private final Pool<Cross> crossPool = new Pool<>(Cross::new);
	private final List<Cross> crosses = new ArrayList<>();

	public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
		builder.then(literal("nolight").executes(c -> {
			MeteorExecutor.execute(() -> {
				for (Cross cross : crosses) crossPool.free(cross);
				crosses.clear();



				BlockIterator.register(64, mc.level.getHeight() / 2, (blockPos, blockState) -> {
					switch (BlockUtils.isValidMobSpawn(blockPos, mc.level.getBlockState(blockPos), 0)) {
						case Never:
							break;
						case Potential, Always:
							crosses.add(crossPool.get().set(blockPos));
							break;
                    }

				});

				BlockIterator.after(() -> {
					BlockPos near = null;
					double dst = Double.MAX_VALUE;
					for (Cross cross : crosses) {
						BlockState ground = mc.level.getBlockState(new BlockPos(cross.x, cross.y - 1, cross.z));
						Block ground_block = ground.getBlock();
                        if (!(ground_block instanceof LeavesBlock) && ground_block != Blocks.CHORUS_FLOWER) {
							double dist = mc.player.distanceToSqr(new Vec3(cross.x, cross.y, cross.z));
							if (dist < dst) {
								dst = dist;
								near = new BlockPos(cross.x, cross.y, cross.z);
							}
						}
                    }
					for (Cross cross : crosses) { crossPool.free(cross); }
					crosses.clear();

					if (near != null) {
						BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + near.getX() + " " + near.getY() + " " + near.getZ());
					}
					else {
						ChatUtils.sendMsg(Component.nullToEmpty("Not founded near light"));
					}
				});
			});

			return SINGLE_SUCCESS;
		}));
	}

	private class Cross {
		private int x, y, z;

		public Cross set(BlockPos blockPos) {
			x = blockPos.getX();
			y = blockPos.getY();
			z = blockPos.getZ();

			return this;
		}
	}

	public enum Spawn {
		Never,
		Potential,
		Always
	}
}
