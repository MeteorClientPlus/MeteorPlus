package nekiplay;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOG = LoggerFactory.getLogger(MixinPlugin.class);
	public static final String METEOR_LOGPREFIX_MIXIN = "[Meteor+ Mixins]";
	public static final String BOZE_LOGPREFIX_MIXIN = "[Boze+ Mixins]";

	private static final String mixinPackageMeteorPlus = "nekiplay.meteorplus.mixin";
	private static final String mixinPackageBozePlus = "nekiplay.bozeplus.mixin";

	public static boolean isMeteorClient = false;
	public static boolean isMeteorRejects = false; // Meteor Client Addon
	public static boolean isNumbyHack = false; // Meteor Client Addon
	public static boolean isZewo2 = false; // Meteor Client Addon

	public static boolean isBozeAPI = false;
	public static boolean isFutureClient = false;

	public static boolean isBaritonePresent = false;
	public static boolean isJourneyMapPresent = false;
	public static boolean isXaeroWorldMapresent = false;
	public static boolean isXaeroMiniMapresent = false;
	public static boolean isXaeroPlusMapresent = false;
	public static boolean isLitematicaMapresent = false;
	public static boolean isWhereIsIt = false;

	@Override
	public void onLoad(String mixinPackage) {
		FabricLoader loader = FabricLoader.getInstance();

		isMeteorClient = loader.isModLoaded("meteor-client");
		isBozeAPI = loader.isModLoaded("boze-api");
		isFutureClient = loader.isModLoaded("future");

		isMeteorRejects = loader.isModLoaded("meteor-rejects");
		isNumbyHack = loader.isModLoaded("numbyhack");
		isZewo2 = loader.isModLoaded("zewo2");

		isBaritonePresent = loader.isModLoaded("baritone");
		isJourneyMapPresent = loader.isModLoaded("journeymap");
		isXaeroWorldMapresent = loader.isModLoaded("xaeroworldmap");
		isXaeroMiniMapresent = loader.isModLoaded("xaerominimap");
		isXaeroPlusMapresent = loader.isModLoaded("xaeroplus");
		isLitematicaMapresent = loader.isModLoaded("litematica");
		isWhereIsIt = loader.isModLoaded("whereisit");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!mixinClassName.startsWith(mixinPackageMeteorPlus) && !mixinClassName.startsWith(mixinPackageBozePlus)) {
			throw new RuntimeException(METEOR_LOGPREFIX_MIXIN + " " + mixinClassName + " is not in the mixin package");
		}
		else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".meteorclient")) {
			if (mixinClassName.contains("FreecamMixin") || mixinClassName.contains("WaypointsModuleMixin")) {
				return isBaritonePresent && isMeteorClient;
			}
            return isMeteorClient;
		}
		else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".journeymap")) {
			return isBaritonePresent && isJourneyMapPresent && isMeteorClient;
		}
		else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".xaero.worldmap")) {
			return isBaritonePresent && isXaeroWorldMapresent && isMeteorClient;
		}
		else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".whereisit")) {
            return isWhereIsIt && isMeteorClient;
		}
		else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".minecraft")) {
			return isMeteorClient;
		}

		else if (mixinClassName.startsWith(mixinPackageBozePlus + ".minecraft")) {
			return isBozeAPI && !isMeteorClient;
		}

		return false;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
