package nekiplay.bozeplus;

import com.google.gson.JsonObject;
import dev.boze.api.BozeInstance;
import dev.boze.api.Globals;
import dev.boze.api.addon.Addon;
import dev.boze.api.addon.AddonMetadata;
import dev.boze.api.addon.AddonVersion;
import dev.boze.api.addon.command.AddonDispatcher;
import dev.boze.api.addon.module.AddonModule;
import dev.boze.api.config.Serializable;
import dev.boze.api.exception.AddonInitializationException;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import nekiplay.bozeplus.features.modules.movement.spider.SpiderPlus;
import nekiplay.bozeplus.impl.BozePlusDispatcher;
import nekiplay.bozeplus.impl.BozePlusModule;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class BozePlusAddon implements Addon, Serializable<BozePlusAddon> {
	private static final IEventBus EVENT_BUS = new EventBus();

	public static IEventBus getEventBus() {
		return EVENT_BUS;
	}

	private static BozePlusAddon instance;

	public static BozePlusAddon getInstance() {
		return instance;
	}

	public final AddonMetadata metadata = new AddonMetadata(
		"boze-plus",
		"Boze Plus",
		"Meteor Plus modules for Boze",
		new AddonVersion(1, 0, 0));

	private final ArrayList<AddonModule> modules = new ArrayList<>();
	private BozePlusDispatcher dispatcher;

	public void onInitialize() {
		try {
			BozeInstance.INSTANCE.registerAddon(this);
		} catch (AddonInitializationException e) {
			Log.error(LogCategory.LOG, "Failed to initialize addon: " + getMetadata().id(), e);
		}
	}

	private void addModule(AddonModule module) {
		modules.add(module);
		EVENT_BUS.subscribe(module);
	}

	@Override
	public AddonMetadata getMetadata() {
		return metadata;
	}

	@Override
	public boolean initialize() {
		instance = this;
		BozeInstance.INSTANCE.registerPackage("nekiplay.bozeplus");
		EVENT_BUS.registerLambdaFactory("nekiplay.bozeplus" , (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

		// Load config
		Globals.getJsonTools().loadObject(this, "config", this);

		SpiderPlus spiderPlus = new SpiderPlus();

		addModule(spiderPlus);

		return true;
	}

	@Override
	public void shutdown() {
		Globals.getJsonTools().saveObject(this, "config", this);
	}

	@Override
	public List<AddonModule> getModules() {
		return modules;
	}

	@Override
	public AddonDispatcher getDispatcher() {
		return dispatcher;
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		for (AddonModule module : modules) {
			object.add(module.getInfo().getName(), ((BozePlusModule) module).toJson());
		}
		return object;
	}

	@Override
	public BozePlusAddon fromJson(JsonObject jsonObject) {
		for (AddonModule module : modules) {
			if (jsonObject.has(module.getInfo().getName())) {
				((BozePlusModule) module).fromJson(jsonObject.getAsJsonObject(module.getInfo().getName()));
			}
		}
		return this;
	}
}
