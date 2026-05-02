package nekiplay.meteorplus.features.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemRawIdCommand extends Command {
	public ItemRawIdCommand() {
		super("rawitemid", "Get raw item id");
	}

	public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
		builder.executes(context -> {
			ItemStack itemStack = mc.player.getMainHandItem();
			if (itemStack != null) {
				int raw_id = Item.getId(itemStack.getItem());
				ChatUtils.sendMsg(Component.nullToEmpty("Raw Item ID: " + raw_id));
			}
			return SINGLE_SUCCESS;
		});


	}
}
