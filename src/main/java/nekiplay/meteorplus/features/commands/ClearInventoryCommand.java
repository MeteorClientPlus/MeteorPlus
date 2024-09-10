package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static nekiplay.meteorplus.features.modules.player.AutoDropPlus.invIndexToSlotId;

public class ClearInventoryCommand extends Command {
	public ClearInventoryCommand() {
		super("clearinv", "Clear inventory");
	}
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			for (int i = 0; i < mc.player.getInventory().size(); i++) {
				ItemStack itemStack = mc.player.getInventory().getStack(i);
				if (itemStack != null) {
					mc.interactionManager.clickSlot(0, invIndexToSlotId(i), 300, SlotActionType.SWAP, mc.player);
				}
			}
			return SINGLE_SUCCESS;
		});


	}
}
