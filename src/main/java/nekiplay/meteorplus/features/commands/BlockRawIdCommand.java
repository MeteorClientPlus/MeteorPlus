package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class BlockRawIdCommand extends Command {
	public BlockRawIdCommand() {
		super("rawblockid", "Get raw block id under mouse");
	}
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
				BlockState state = mc.world.getBlockState(pos);
				int raw_id = Block.getRawIdFromState(state);
				info(String.valueOf(raw_id));
			}
			return SINGLE_SUCCESS;
		});
	}
}
