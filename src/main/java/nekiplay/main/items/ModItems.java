package nekiplay.main.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item METEOR_PLUS_LOGO_ITEM = new Item(new Item.Settings());
	public static final Item METEOR_PLUS_LOGO_MODS_ITEM = new Item(new Item.Settings());

	public static void initializeMeteorPlus() {
		Registry.register(Registries.ITEM, Identifier.of("meteorplus", "logo"), METEOR_PLUS_LOGO_ITEM);
		Registry.register(Registries.ITEM, Identifier.of("meteorplus", "logo_mods"), METEOR_PLUS_LOGO_MODS_ITEM);
	}
}
