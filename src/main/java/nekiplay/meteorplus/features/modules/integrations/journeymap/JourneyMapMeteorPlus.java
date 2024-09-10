package nekiplay.meteorplus.features.modules.integrations.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import nekiplay.Main;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import net.minecraft.text.Text;

public class JourneyMapMeteorPlus implements IClientPlugin {
	public final String JourneyMapLOGPREFIX = "[Journey Map]";
	@Override
	public void initialize(final IClientAPI jmClientApi) {
		MapIntegration mapIntegration = Modules.get().get(MapIntegration.class);


		MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " loading Journey Map integrate");
		FabricEvents.FULLSCREEN_POPUP_MENU_EVENT.register(event -> {
			MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_goal_here").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
				});
				popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_path_here").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
				});
				if (mapIntegration.baritoneElytra.get() && PlayerUtils.getDimension() == Dimension.Nether) {
					popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_elytra_here").getString(), p -> {
						GoalBlock goal = new GoalBlock(p.up());
						if (goal.y > 0 && goal.y < 128) {
							BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
							for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
								if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
									baritone.getCommandManager().execute("elytra");
									break;
								}
							}
						}
						else {
							ChatUtils.error("The y of the goal is not between 0 and 128");
						}
					});
				}
			}

			MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " register fullscreen Journey Map done");
		});

		FabricEvents.WAYPOINT_POPUP_MENU_EVENT.register(event -> {
			MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map");
			ModPopupMenu popupMenu = event.getPopupMenu();

			if (mapIntegration != null && mapIntegration.baritoneGoto.get()) {
				popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_goal_here").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
				});
				popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_path_here").getString(), p -> {
					GoalBlock goal = new GoalBlock(p.up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
				});
				if (mapIntegration.baritoneElytra.get() && PlayerUtils.getDimension() == Dimension.Nether) {
					popupMenu.addMenuItem(Text.translatable("gui.world_map.baritone_elytra_here").getString(), p -> {
						GoalBlock goal = new GoalBlock(p.up());
						if (goal.y > 0 && goal.y < 128) {
							BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
							for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
								if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
									baritone.getCommandManager().execute("elytra");
									break;
								}
							}
						}
						else {
							ChatUtils.error("The y of the goal is not between 0 and 128");
						}
					});
				}
			}

			MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " register waypoints Journey Map done");
		});

		MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " initializing tab...");


		MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " loaded tab");


		MeteorPlusAddon.LOG.info(Main.METEOR_LOGPREFIX + " " + JourneyMapLOGPREFIX + " Journey Map integrate loaded");
	}

	@Override
	public String getModId() {
		return "meteorplus";
	}

	@Override
	public void onEvent(ClientEvent event) {

	}
}
