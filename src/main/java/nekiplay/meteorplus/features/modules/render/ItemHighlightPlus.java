package nekiplay.meteorplus.features.modules.render;

import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.settings.items.HighlightItemData;
import nekiplay.meteorplus.settings.items.ItemDataSetting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class ItemHighlightPlus extends Module {
	public ItemHighlightPlus() {
		super(Categories.Render, "Item-Highlight+", "Highlights selected items when in inventorys and guis.");
	}

	public final SettingGroup sgGeneral = settings.getDefaultGroup();
	public final Setting<List<Item>> whitelist = sgGeneral.add(new ItemListSetting.Builder()
		.name("whitelist")
		.description("Items to highlight.")
		.defaultValue(
			Items.ELYTRA
		)
		.build()
	);

	public final Setting<HighlightItemData> defaultBlockConfig = sgGeneral.add(new GenericSetting.Builder<HighlightItemData>()
		.name("whitelist-default-config")
		.description("Default item config.")
		.defaultValue(
			new HighlightItemData(
				new SettingColor(0, 255, 200, 25)
			)
		)
		.build()
	);
	public final Setting<Map<Item, HighlightItemData>> itemsConfigs = sgGeneral.add(new ItemDataSetting.Builder<HighlightItemData>()
		.name("whitelist-items-configs")
		.description("Config for each highlight.")
		.defaultData(defaultBlockConfig)
		.build()
	);
}
