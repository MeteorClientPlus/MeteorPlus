package nekiplay.bozeplus.impl;

import com.google.gson.JsonObject;
import dev.boze.api.BozeInstance;
import dev.boze.api.addon.gui.AddonElement;
import dev.boze.api.addon.module.AddonModule;
import dev.boze.api.config.Serializable;
import dev.boze.api.input.Bind;
import dev.boze.api.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class BozePlusModule implements AddonModule, ModuleInfo, Serializable<BozePlusModule> {

	private boolean state;
	private Bind bind;

	private final String name;
	private String title;
	private final String description;

	protected final ArrayList<AddonElement> elements = new ArrayList<>();

	public BozePlusModule(String name, String title, String description) {
		this.name = name;
		this.title = title;
		this.description = description;

		this.state = false;
		this.bind = new BozePlusBind(-1, false);
	}

	@Override
	public ModuleInfo getInfo() {
		return this;
	}

	@Override
	public List<AddonElement> getElements() {
		return elements;
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("title", name);

		for (AddonElement element : elements) {
			if (element instanceof Serializable) {
				object.add(element.getName(), ((Serializable<?>) element).toJson());
			}
		}

		return object;
	}

	@Override
	public BozePlusModule fromJson(JsonObject jsonObject) {
		title = jsonObject.get("title").getAsString();

		for (AddonElement element : elements) {
			if (element instanceof Serializable) {
				((Serializable<?>) element).fromJson(jsonObject.get(element.getName()).getAsJsonObject());
			}
		}

		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean getState() {
		return state;
	}

	@Override
	public boolean setState(boolean newState) {
		if (state != newState) {
			state = newState;

			if (state) {
				BozeInstance.INSTANCE.subscribe(this);
			} else {
				BozeInstance.INSTANCE.unsubscribe(this);
			}

			return true;
		}
		return false;
	}

	@Override
	public Bind getBind() {
		return bind;
	}

	@Override
	public void setBind(Bind bind) {
		this.bind = bind;
	}
}
