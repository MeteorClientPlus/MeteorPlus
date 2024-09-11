package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemDataSettingScreen extends WindowScreen {
	private static final List<Item> BLOCKS = new ArrayList<>(100);

	private final ItemDataSetting<?> setting;

	private WTable table;
	private String filterText = "";

	public ItemDataSettingScreen(GuiTheme theme, ItemDataSetting<?> setting) {
		super(theme, "Configure Items");

		this.setting = setting;
	}

	@Override
	public void initWidgets() {
		WTextBox filter = add(theme.textBox("")).minWidth(400).expandX().widget();
		filter.setFocused(true);
		filter.action = () -> {
			filterText = filter.get().trim();

			table.clear();
			initTable();
		};

		table = add(theme.table()).expandX().widget();

		initTable();
	}

	public <T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> void initTable() {
		for (Item Item : Registries.ITEM) {
			T blockData = (T) setting.get().get(Item);

			if (blockData != null && blockData.isChanged()) BLOCKS.add(0, Item);
			else BLOCKS.add(Item);
		}

		for (Item block : BLOCKS) {
			String name = Names.get(block);
			if (!StringUtils.containsIgnoreCase(name, filterText)) continue;

			T blockData = (T) setting.get().get(block);

			table.add(theme.itemWithLabel(block.asItem().getDefaultStack(), Names.get(block))).expandCellX();
			table.add(theme.label((blockData != null && blockData.isChanged()) ? "*" : " "));

			WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
			edit.action = () -> {
				T data = blockData;
				if (data == null) data = (T) setting.defaultData.get().copy();

				mc.setScreen(data.createScreen(theme, block, (ItemDataSetting<T>) setting));
			};

			WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
			reset.action = () -> {
				setting.get().remove(block);
				setting.onChanged();

				if (blockData != null && blockData.isChanged()) {
					table.clear();
					initTable();
				}
			};

			table.row();
		}

		BLOCKS.clear();
	}
}
