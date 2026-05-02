package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.IGeneric;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class ESPItemData implements IGeneric<ESPItemData>, IChangeable, IItemData<ESPItemData> {
	public ShapeMode shapeMode;
	public SettingColor lineColor;
	public SettingColor sideColor;

	public boolean tracer;
	public SettingColor tracerColor;

	private boolean changed;

	public ESPItemData(ShapeMode shapeMode, SettingColor lineColor, SettingColor sideColor, boolean tracer, SettingColor tracerColor) {
		this.shapeMode = shapeMode;
		this.lineColor = lineColor;
		this.sideColor = sideColor;

		this.tracer = tracer;
		this.tracerColor = tracerColor;
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme, Item block, ItemDataSetting<ESPItemData> setting) {
		return new ESPItemDataScreen(theme, this, block, setting);
	}

	@Override
	public WidgetScreen createScreen(GuiTheme theme, GenericSetting<ESPItemData> setting) {
		return new ESPItemDataScreen(theme, this, setting);
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	public void changed() {
		changed = true;
	}

	public void tickRainbow() {
		lineColor.update();
		sideColor.update();
		tracerColor.update();
	}

	@Override
	public ESPItemData set(ESPItemData value) {
		shapeMode = value.shapeMode;
		lineColor.set(value.lineColor);
		sideColor.set(value.sideColor);

		tracer = value.tracer;
		tracerColor.set(value.tracerColor);

		changed = value.changed;

		return this;
	}

	@Override
	public ESPItemData copy() {
		return new ESPItemData(shapeMode, new SettingColor(lineColor), new SettingColor(sideColor), tracer, new SettingColor(tracerColor));
	}

	@Override
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();

		tag.putString("shapeMode", shapeMode.name());
		tag.put("lineColor", lineColor.toTag());
		tag.put("sideColor", sideColor.toTag());

		tag.putBoolean("tracer", tracer);
		tag.put("tracerColor", tracerColor.toTag());

		tag.putBoolean("changed", changed);

		return tag;
	}

	@Override
	public ESPItemData fromTag(CompoundTag tag) {
		shapeMode = ShapeMode.valueOf(tag.getString("shapeMode").get());
		lineColor.fromTag(tag.getCompound("lineColor").get());
		sideColor.fromTag(tag.getCompound("sideColor").get());

		tracer = tag.getBoolean("tracer").get();
		tracerColor.fromTag(tag.getCompound("tracerColor").get());

		changed = tag.getBoolean("changed").get();

		return this;
	}
}
