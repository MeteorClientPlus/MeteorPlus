package nekiplay.meteorplus.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import nekiplay.meteorplus.settings.items.ItemDataSetting;
import nekiplay.meteorplus.settings.items.ItemDataSettingScreen;

import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MeteorPlusSettings {
	private final Map<Class<?>, SettingsWidgetFactory.Factory> factories;

	private final GuiTheme theme;

	public MeteorPlusSettings(Map<Class<?>, SettingsWidgetFactory.Factory> factories, GuiTheme theme) {
		this.factories = factories;
		this.theme = theme;
	}

	public void addSettings() {
		factories.put(ItemDataSetting.class, (table, setting) -> stringMapW(table, (ItemDataSetting<?>) setting));
	}


	private void stringMapW(WTable table, ItemDataSetting<?> setting) {
		WButton button = table.add(theme.button(GuiRenderer.EDIT)).expandCellX().widget();
		button.action = () -> mc.setScreen(new ItemDataSettingScreen(theme, setting));
	}
}
