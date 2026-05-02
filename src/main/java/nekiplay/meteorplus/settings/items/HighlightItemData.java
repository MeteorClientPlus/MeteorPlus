package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.IGeneric;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;

public class HighlightItemData implements IGeneric<HighlightItemData>, IChangeable, IItemData<HighlightItemData> {
	public SettingColor Color;
	private boolean changed;

	public HighlightItemData(SettingColor color) {
		this.Color = color;
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme, Item block, ItemDataSetting<HighlightItemData> setting) {
		return new HighlightItemDataScreen(theme, this, block, setting);
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme, GenericSetting<HighlightItemData> setting) {
		return new HighlightItemDataScreen(theme, this, setting);
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	public void changed() {
		changed = true;
	}

	public void tickRainbow() {
		Color.update();
	}

	@Override
	public HighlightItemData set(HighlightItemData value) {
		Color.set(value.Color);
		changed = value.changed;

		return this;
	}

	@Override
	public HighlightItemData copy() {
		return new HighlightItemData(new SettingColor(Color));
	}

	@Override
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();

		tag.put("color", Color.toTag());
		tag.putBoolean("changed", changed);

		return tag;
	}

	@Override
	public HighlightItemData fromTag(CompoundTag tag) {
		Color.fromTag(tag.getCompound("color").get());
		changed = tag.getBoolean("changed").get();

		return this;
	}
}
