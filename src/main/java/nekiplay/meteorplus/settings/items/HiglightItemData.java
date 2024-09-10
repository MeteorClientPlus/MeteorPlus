package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.utils.IScreenFactory;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public class HiglightItemData implements ICopyable<HiglightItemData>, ISerializable<HiglightItemData>, IChangeable, IItemData<HiglightItemData>, IScreenFactory {
	public SettingColor Color;
	private boolean changed;

	public HiglightItemData(SettingColor color) {
		this.Color = color;
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme, Item block, ItemDataSetting<HiglightItemData> setting) {
		return new HiglightItemDataScren(theme, this, block, setting);
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme) {
		return new HiglightItemDataScren(theme, this, null, null);
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
	public HiglightItemData set(HiglightItemData value) {
		Color.set(value.Color);
		changed = value.changed;

		return this;
	}

	@Override
	public HiglightItemData copy() {
		return new HiglightItemData(new SettingColor(Color));
	}

	@Override
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();

		tag.put("color", Color.toTag());
		tag.putBoolean("changed", changed);

		return tag;
	}

	@Override
	public HiglightItemData fromTag(NbtCompound tag) {
		Color.fromTag(tag.getCompound("color"));
		changed = tag.getBoolean("changed");

		return this;
	}
}
