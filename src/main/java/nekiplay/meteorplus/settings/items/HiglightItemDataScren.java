package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;

public class HiglightItemDataScren extends WindowScreen {
	private final HiglightItemData blockData;
	private final Item block;
	private final ItemDataSetting<HiglightItemData> setting;

	public HiglightItemDataScren(GuiTheme theme, HiglightItemData blockData, Item block, ItemDataSetting<HiglightItemData> setting) {
		super(theme, "Configure Items");

		this.blockData = blockData;
		this.block = block;
		this.setting = setting;
	}

	@Override
	public void initWidgets() {
		Settings settings = new Settings();
		SettingGroup sgGeneral = settings.getDefaultGroup();

		sgGeneral.add(new ColorSetting.Builder()
			.name("color")
			.description("Color of item.")
			.defaultValue(new SettingColor(0, 255, 200))
			.onModuleActivated(settingColorSetting -> settingColorSetting.set(blockData.Color))
			.onChanged(settingColor -> {
				blockData.Color.set(settingColor);
				changed(blockData, block, setting);
			})
			.build()
		);

		settings.onActivated();
		add(theme.settings(settings)).expandX();
	}

	private void changed(HiglightItemData blockData, Item block, ItemDataSetting<HiglightItemData> setting) {
		if (!blockData.isChanged() && block != null && setting != null) {
			setting.get().put(block, blockData);
			setting.onChanged();
		}

		blockData.changed();
	}
}
