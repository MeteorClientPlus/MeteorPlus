package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;

public class BlockRawIdCommand extends Command {
	public BlockRawIdCommand() {
		super("rawblockid", "Get raw block id under mouse");
	}
	public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
		builder.executes(context -> {
			if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
				BlockState state = mc.level.getBlockState(pos);
				int raw_id = Block.getId(state);
				info(String.valueOf(raw_id));
			}
			return SINGLE_SUCCESS;
		});
	}
}
