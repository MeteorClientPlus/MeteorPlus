package nekiplay;

import nekiplay.bozeplus.BozePlusAddon;
import nekiplay.main.items.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public static final String BOZE_LOGPREFIX = "[Boze+]";
	public static final String METEOR_LOGPREFIX = "[Meteor+]";
	@Override
	public void onInitialize() {
		LOG.info(METEOR_LOGPREFIX + " Initializing items...");
		ModItems.initializeMeteorPlus();
		LOG.info(METEOR_LOGPREFIX + " Loaded items");

		if (MixinPlugin.isBozeAPI && !MixinPlugin.isMeteorClient) {
			LOG.info(METEOR_LOGPREFIX + " & " + BOZE_LOGPREFIX + " Initialization of Boze Client integration because Meteor Client is not found...");
			BozePlusAddon bozePlusMain = new BozePlusAddon();
			bozePlusMain.onInitialize();
			LOG.info(METEOR_LOGPREFIX + " & " + BOZE_LOGPREFIX + " Loaded of Boze Client integration");
		}
		else if (MixinPlugin.isBozeAPI) {
			LOG.info(METEOR_LOGPREFIX + " & " + BOZE_LOGPREFIX + " You have Meteor Client installed, integration with Boze Client is disabled");
		}
	}
}
