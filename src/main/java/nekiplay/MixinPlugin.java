package nekiplay;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOG = LogUtils.getLogger();
	public static final String METEOR_LOGPREFIX_MIXIN = "[Meteor+ Mixins]";

	private static final String mixinPackageMeteorPlus = "nekiplay.meteorplus.mixin";

	public static boolean isMeteorClient = false;
	public static boolean isMeteorRejects = false; // Meteor Client Addon

	public static boolean isBaritonePresent = false; // Baritone for auto walking
	public static boolean isXaeroWorldMapPresent = false; // Extension for map and baritone
	public static boolean isXaeroPlusMapPresent = false; // other extension for map and baritone
	public static boolean isWhereIsIt = false; // Utility for ChestTracker for render 3d text

	@Override
	public void onLoad(String mixinPackage) {
		FabricLoader loader = FabricLoader.getInstance();

		isMeteorClient = loader.isModLoaded("meteor-client");
		isMeteorRejects = loader.isModLoaded("meteor-rejects");

		isBaritonePresent = loader.isModLoaded("baritone-meteor") || loader.isModLoaded("baritone");
		isXaeroWorldMapPresent = loader.isModLoaded("xaeroworldmap");
		isXaeroPlusMapPresent = loader.isModLoaded("xaeroplus");
		isWhereIsIt = loader.isModLoaded("whereisit");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!mixinClassName.startsWith(mixinPackageMeteorPlus)) {
			throw new RuntimeException(METEOR_LOGPREFIX_MIXIN + " " + mixinClassName + " is not in the mixin package");
		} else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".meteorclient")) {
			if (mixinClassName.contains("FreecamMixin") || mixinClassName.contains("WaypointsModuleMixin")) {
				return isBaritonePresent && isMeteorClient;
			}
			return isMeteorClient;
		} else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".xaero.worldmap")) {
			return isBaritonePresent && isXaeroWorldMapPresent && isMeteorClient;
		} else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".whereisit")) {
			return isWhereIsIt && isMeteorClient;
		} else if (mixinClassName.startsWith(mixinPackageMeteorPlus + ".minecraft")) {
			return isMeteorClient;
		}
		return false;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

}
