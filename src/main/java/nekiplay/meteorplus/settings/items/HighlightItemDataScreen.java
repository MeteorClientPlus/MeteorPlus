package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class HighlightItemDataScreen extends WindowScreen {
	private final HighlightItemData blockData;
	private final Setting<?> setting;
	private final @Nullable Runnable firstChangeConsumer;

	public HighlightItemDataScreen(GuiTheme theme, HighlightItemData blockData, Item block, ItemDataSetting<HighlightItemData> setting) {
		this(theme, blockData, setting, () -> setting.get().put(block, blockData));
	}

	public HighlightItemDataScreen(GuiTheme theme, HighlightItemData blockData, GenericSetting<HighlightItemData> setting) {
		this(theme, blockData, setting, null);
	}

	private HighlightItemDataScreen(GuiTheme theme, HighlightItemData blockData, Setting<?> setting, @Nullable Runnable firstChangeConsumer) {
		super(theme, "Configure Items");

		this.blockData = blockData;
		this.setting = setting;
		this.firstChangeConsumer = firstChangeConsumer;
	}

	@Override
	public void initWidgets() {
		Settings settings = new Settings();
		SettingGroup sgGeneral = settings.getDefaultGroup();

		sgGeneral.add(new ColorSetting.Builder()
			.name("color")
			.description("Color of item.")
			.defaultValue(new SettingColor(0, 255, 200))
			.onModuleActivated(settingColorSetting -> settingColorSetting.get().set(blockData.Color))
			.onChanged(settingColor -> {
				if (!blockData.Color.equals(settingColor)) {
					blockData.Color.set(settingColor);
					onChanged();
				}
			})
			.build()
		);

		settings.onActivated();
		add(theme.settings(settings)).expandX();
	}

	private void onChanged() {
		if (!blockData.isChanged() && firstChangeConsumer != null) {
			firstChangeConsumer.run();
		}

		setting.onChanged();
		blockData.changed();
	}
}
