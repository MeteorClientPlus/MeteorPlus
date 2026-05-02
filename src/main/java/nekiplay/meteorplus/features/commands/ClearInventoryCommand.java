package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static nekiplay.meteorplus.features.modules.player.AutoDropPlus.invIndexToSlotId;

public class ClearInventoryCommand extends Command {
	public ClearInventoryCommand() {
		super("clearinv", "Clear inventory");
	}
	public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
		builder.executes(context -> {
			for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
				ItemStack itemStack = mc.player.getInventory().getItem(i);
				if (itemStack != null) {
					mc.gameMode.handleInventoryMouseClick(0, invIndexToSlotId(i), 300, ClickType.SWAP, mc.player);
				}
			}
			return SINGLE_SUCCESS;
		});


	}
}
