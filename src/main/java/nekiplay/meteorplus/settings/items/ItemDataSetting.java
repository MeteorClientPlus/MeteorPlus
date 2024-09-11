package nekiplay.meteorplus.settings.items;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.IGetter;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ItemDataSetting<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> extends Setting<Map<Item, T>> {
 	public final IGetter<T> defaultData;

	public ItemDataSetting(String name, String description, Map<Item, T> defaultValue, Consumer<Map<Item, T>> onChanged, Consumer<Setting<Map<Item, T>>> onModuleActivated, IGetter<T> defaultData, IVisible visible) {
		super(name, description, defaultValue, onChanged, onModuleActivated, visible);

		this.defaultData = defaultData;
	}

	@Override
	public void resetImpl() {
		value = new HashMap<>(defaultValue);
	}

	@Override
	protected Map<Item, T> parseImpl(String str) {
		return new HashMap<>(0);
	}

	@Override
	protected boolean isValueValid(Map<Item, T> value) {
		return true;
	}

	@Override
	protected NbtCompound save(NbtCompound tag) {
		NbtCompound valueTag = new NbtCompound();
		for (Item block : get().keySet()) {
			valueTag.put(Registries.ITEM.getId(block).toString(), get().get(block).toTag());
		}
		tag.put("value", valueTag);

		return tag;
	}

	@Override
	protected Map<Item, T> load(NbtCompound tag) {
		get().clear();

		NbtCompound valueTag = tag.getCompound("value");
		for (String key : valueTag.getKeys()) {
			get().put(Registries.ITEM.get(new Identifier(key)), defaultData.get().copy().fromTag(valueTag.getCompound(key)));
		}

		return get();
	}

	public static class Builder<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> extends SettingBuilder<Builder<T>, Map<Item, T>, ItemDataSetting<T>> {
		private IGetter<T> defaultData;

		public Builder() {
			super(new HashMap<>(0));
		}

		public Builder<T> defaultData(IGetter<T> defaultData) {
			this.defaultData = defaultData;
			return this;
		}

		@Override
		public ItemDataSetting<T> build() {
			return new ItemDataSetting<>(name, description, defaultValue, onChanged, onModuleActivated, defaultData, visible);
		}
	}
}
