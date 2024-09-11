package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.item.Item;

public interface IItemData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> {
	WidgetScreen createScreen(GuiTheme theme, Item block, ItemDataSetting<T> setting);
}
