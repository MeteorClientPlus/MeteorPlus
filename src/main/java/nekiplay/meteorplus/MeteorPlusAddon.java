package nekiplay.meteorplus;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import nekiplay.MixinPlugin;
import nekiplay.meteorplus.features.commands.*;
import nekiplay.meteorplus.features.modules.combat.*;
import nekiplay.meteorplus.features.modules.combat.velocity.VelocityPlus;
import nekiplay.meteorplus.features.modules.integrations.WhereIsIt;
import nekiplay.meteorplus.features.modules.misc.*;
import nekiplay.meteorplus.features.modules.movement.*;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyPlus;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowPlus;
import nekiplay.meteorplus.features.modules.player.*;
import nekiplay.meteorplus.features.modules.render.*;
import nekiplay.meteorplus.features.modules.render.holograms.*;
import nekiplay.meteorplus.features.modules.world.*;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.AutoObsidianFarm;
import nekiplay.meteorplus.gui.tabs.HiddenModulesTab;
import nekiplay.meteorplus.hud.TimerPlusCharge;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import nekiplay.meteorplus.features.modules.world.timer.TimerPlus;
import nekiplay.main.items.ModItems;
import nekiplay.meteorplus.settings.ConfigModifier;
import net.fabricmc.loader.api.FabricLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.item.ItemStack;
import nekiplay.meteorplus.features.modules.movement.fastladder.FastLadderPlus;
import nekiplay.meteorplus.features.modules.movement.fly.FlyPlus;
import nekiplay.meteorplus.features.modules.movement.jesus.JesusPlus;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallPlus;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedPlus;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderPlus;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static nekiplay.Main.METEOR_LOGPREFIX;
import static nekiplay.MixinPlugin.*;

public class MeteorPlusAddon extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(MeteorPlusAddon.class);
	public static final ItemStack logo_mods_item = ModItems.METEOR_PLUS_LOGO_MODS_ITEM.getDefaultStack();

	public static final Category CATEGORYMODS = new Category("Integrations", logo_mods_item);
	public static final String HUD_TITLE = "Meteor+";
	public static final HudGroup HUD_GROUP = new HudGroup(HUD_TITLE);

	private static MeteorPlusAddon instance;

	public static MeteorPlusAddon getInstance() {
		return instance;
	}

	@Override
	public void onInitialize() {
		instance = this;

		LOG.info(METEOR_LOGPREFIX + " Initializing...");

		ArrayList<String> notFoundIntegrations = new ArrayList<>();
		ArrayList<String> notFoundBaritoneIntegrations = new ArrayList<>();
		ArrayList<String> enabledIntegrations = new ArrayList<>();

		if (isXaeroWorldMapresent) {
			if (!isBaritonePresent) {
				notFoundBaritoneIntegrations.add("Xaero's World Map");
			}
			else {
				enabledIntegrations.add("Xaero's World Map");
			}
		}
		else {
			notFoundIntegrations.add("Xaero's World Map");
		}
		if (isJourneyMapPresent) {
			if (!isBaritonePresent) {
				notFoundBaritoneIntegrations.add("Journey Map");
			}
			else {
				enabledIntegrations.add("Journey Map");
			}
		}
		else {
			notFoundIntegrations.add("Journey Map");
		}

		if (!isWhereIsIt) {
			notFoundIntegrations.add("Where is it");
		}
		else {
			enabledIntegrations.add("Where is it");
		}

		if (!isBaritonePresent) {
			notFoundBaritoneIntegrations.add("Hunt");
			notFoundBaritoneIntegrations.add("Freecam");
			notFoundBaritoneIntegrations.add("Waypoints");
			notFoundBaritoneIntegrations.add("Goto+");
		}
		else {
			enabledIntegrations.add("Hunt");
			enabledIntegrations.add("Freecam");
			enabledIntegrations.add("Waypoints");
			enabledIntegrations.add("Goto+");
		}

		if (!enabledIntegrations.isEmpty()) {
			LOG.info(METEOR_LOGPREFIX + " Enabling integrations for: " + String.join(", ", enabledIntegrations));
		}
		if (!notFoundBaritoneIntegrations.isEmpty()) {
			LOG.warn(METEOR_LOGPREFIX + " Not found Baritone for integrations: " + String.join(", ", notFoundBaritoneIntegrations));
		}
		if (!notFoundIntegrations.isEmpty()) {
			LOG.warn(METEOR_LOGPREFIX + " Not found mods for integrations: " + String.join(", ", notFoundIntegrations));
		}

		MeteorClient.EVENT_BUS.subscribe(new CordinateProtector());
		ConfigModifier.get();

		//region Commands
		LOG.info(METEOR_LOGPREFIX + " Initializing commands...");

		Commands.add(new ItemRawIdCommand());
		Commands.add(new EclipCommand());
		Commands.add(new ClearInventoryCommand());
		if (isBaritonePresent) {
			Commands.add(new GotoPlusCommand());
		}
		Commands.add(new GPTCommand());

		LOG.info(METEOR_LOGPREFIX + " Loaded commands");
		//endregion

		LOG.info(METEOR_LOGPREFIX + " Initializing better chat custom head...");
		BetterChat.registerCustomHead("[Meteor+]", new Identifier("meteorplus", "chat/icon.png"));
		LOG.info(METEOR_LOGPREFIX + " Loaded better chat");


		//region Modules
		LOG.info(METEOR_LOGPREFIX + " Initializing modules...");
		Modules modules = Modules.get();
		if (isBaritonePresent) {
			modules.add(new Hunt());
		}
		//modules.add(new KillAuraPlus());
		modules.add(new Teams());
		modules.add(new HologramModule());
		modules.add(new ChatPrefix());
		modules.add(new ChatGPT());
		modules.add(new ItemHighlightPlus());
		modules.add(new FastLadderPlus());
		modules.add(new TriggerBot());
		modules.add(new EyeFinder());
		modules.add(new InventoryMovePlus());
		modules.add(new AutoDropPlus());
		modules.add(new NoFallPlus());
		modules.add(new TimerPlus());
		modules.add(new SpeedPlus());
		modules.add(new FlyPlus());
		modules.add(new SpiderPlus());
		modules.add(new JesusPlus());
		modules.add(new BedrockStorageBruteforce());
		modules.add(new AutoCraftPlus());
		modules.add(new AutoObsidianFarm());
		modules.add(new XrayBruteforce());
		modules.add(new AutoLeave());
		modules.add(new AutoAccept());
		modules.add(new GhostBlockFixer());
		modules.add(new SafeMine());
		modules.add(new Freeze());
		modules.add(new AntiBotPlus());
		modules.add(new MultiTasks());
		modules.add(new ItemFrameEsp());
		//modules.add(new KillAuraPlus());
		modules.add(new ElytraFlyPlus());
		modules.add(new VelocityPlus());
		if (!MixinPlugin.isMeteorRejects) {
			modules.add(new NoJumpDelay());
		}
		else {
			LOG.warn(METEOR_LOGPREFIX + " Meteor Rejects detected, removing No Jump Delay");
		}
		modules.add(new NoSlowPlus());
		if (isBaritonePresent) {
			if (isXaeroWorldMapresent || isJourneyMapPresent) {
				modules.add(new MapIntegration());
			}
		}
		if (isWhereIsIt) {
			modules.add(new WhereIsIt());
		}
		LOG.info(METEOR_LOGPREFIX + " Loaded modules");
		//endregion

		//region Hud
		LOG.info(METEOR_LOGPREFIX + " Initializing hud...");

		Hud.get().register(TimerPlusCharge.INFO);

		LOG.info(METEOR_LOGPREFIX + " Loaded hud");
		//endregion

		//region Tabs
		LOG.info(METEOR_LOGPREFIX + " Initializing tabs...");

		Tabs.add(new HiddenModulesTab());

		LOG.info(METEOR_LOGPREFIX + " Loaded tabs");
		//endregion
		LOG.info(METEOR_LOGPREFIX + " Full loaded");
	}

	@Override
	public void onRegisterCategories() {
		LOG.info(METEOR_LOGPREFIX + " registering categories...");
		if (isXaeroWorldMapresent ||
			isJourneyMapPresent ||
			MixinPlugin.isLitematicaMapresent ||
			MixinPlugin.isWhereIsIt
		) {
			Modules.registerCategory(CATEGORYMODS);
		}
		//Modules.registerCategory(CATEGORY);
		LOG.info(METEOR_LOGPREFIX + " register categories");
	}

	@Override
	public String getWebsite() {
		return "https://meteorclientplus.github.io";
	}

	@Override
	public GithubRepo getRepo() {
		return new GithubRepo("MeteorClientPlus", "MeteorPlus",  "1.20.6", null);
	}

	@Override
	public String getCommit() {
		String commit = FabricLoader
			.getInstance()
			.getModContainer("meteorplus")
			.get().getMetadata()
			.getCustomValue("github:sha")
			.getAsString();
		return commit.isEmpty() ? null : commit.trim();
	}

	@Override
	public String getPackage() {
		return "nekiplay.meteorplus";
	}
}
