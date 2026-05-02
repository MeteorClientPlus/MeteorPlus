package nekiplay.main.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class ModItems {

	public static Item register(String path, Function<Item.Properties, Item> factory, Item.Properties settings) {
		Identifier id = Identifier.fromNamespaceAndPath("meteorplus", path);
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
		return Registry.register(
			BuiltInRegistries.ITEM,
			key,
			factory.apply(settings.setId(key))
		);
	}
	public static Item METEOR_PLUS_LOGO_ITEM = null;
	public static Item METEOR_PLUS_LOGO_MODS_ITEM = null;

	public static Item METEOR_PLUS_STAR_ITEM = null;
	public static Item METEOR_PLUS_DIAMOND_ITEM = null;
	public static Item METEOR_PLUS_MONEY_ITEM = null;

	public static void initialize() {
		METEOR_PLUS_LOGO_ITEM = register("logo", Item::new, new Item.Properties());
		METEOR_PLUS_LOGO_MODS_ITEM = register("logo_mods", Item::new, new Item.Properties());

		METEOR_PLUS_STAR_ITEM = register("star", Item::new, new Item.Properties());
		METEOR_PLUS_DIAMOND_ITEM = register("diamond", Item::new, new Item.Properties());
		METEOR_PLUS_MONEY_ITEM = register("money", Item::new, new Item.Properties());
	}
}
