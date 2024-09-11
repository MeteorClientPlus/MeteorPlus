package nekiplay.meteorplus.mixin.xaero.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.WorldMap;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointReader;

import java.util.ArrayList;

@Mixin(WaypointReader.class)
public class WaypointRendererMixin {
	@Inject(method = "getRightClickOptions(Lxaero/map/mods/gui/Waypoint;Lxaero/map/gui/IRightClickableElement;)Ljava/util/ArrayList;", at = @At("HEAD"), remap = false, cancellable = true)
	private void rightClickOptins(Waypoint element, IRightClickableElement target, CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		Modules modules = Modules.get();
		if (modules != null) {
			MapIntegration mapIntegration = modules.get(MapIntegration.class);
			if (mapIntegration != null && mapIntegration.isActive()) {
				ArrayList<RightClickOption> rightClickOptions = new ArrayList();
				rightClickOptions.add(new RightClickOption(element.getName(), rightClickOptions.size(), target) {
					public void onAction(Screen screen) {
						SupportMods.xaeroMinimap.openWaypoint((GuiMap) screen, element);
					}
				});
				if (WorldMap.settings.coordinates && !SupportMods.xaeroMinimap.hidingWaypointCoordinates()) {
					rightClickOptions.add(new RightClickOption(String.format("X: %d, Y: %s, Z: %d", element.getX(), element.isyIncluded() ? "" + element.getY() : "~", element.getZ()), rightClickOptions.size(), target) {
						public void onAction(Screen screen) {
							SupportMods.xaeroMinimap.openWaypoint((GuiMap) screen, element);
						}
					});
				}

				rightClickOptions.add((new RightClickOption("gui.xaero_right_click_waypoint_edit", rightClickOptions.size(), target) {
					public void onAction(Screen screen) {
						SupportMods.xaeroMinimap.openWaypoint((GuiMap) screen, element);
					}
				}).setNameFormatArgs(new Object[]{"E"}));

				if (mapIntegration != null && mapIntegration.baritoneGoto.get()) {
					rightClickOptions.add((new RightClickOption("gui.world_map.baritone_goal_here", rightClickOptions.size(), target) {
						public void onAction(Screen screen) {
							GoalBlock goal = new GoalBlock(new BlockPos(element.getX(), element.getY(), element.getZ()));
							BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
						}

						public boolean isActive() {
							return true;
						}
					}).setNameFormatArgs(new Object[]{"G"}));

					rightClickOptions.add((new RightClickOption("gui.world_map.baritone_path_here", rightClickOptions.size(), target) {
						public void onAction(Screen screen) {
							GoalBlock goal = new GoalBlock(new BlockPos(element.getX(), element.getY(), element.getZ()));
							BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
						}

						public boolean isActive() {
							return true;
						}
					}).setNameFormatArgs(new Object[]{"P"}));

					if (mapIntegration.baritoneElytra.get() && PlayerUtils.getDimension() == Dimension.Nether) {
						rightClickOptions.add((new RightClickOption("gui.world_map.baritone_elytra_here", rightClickOptions.size(), target) {
							public void onAction(Screen screen) {
								GoalBlock goal = new GoalBlock(new BlockPos(element.getX(), element.getY(), element.getZ()));
								if (goal.getGoalPos().getY() > 0 && goal.getGoalPos().getY() < 128) {
									BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
									for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
										if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
											baritone.getCommandManager().execute("elytra");
											break;
										}
									}
								}
							}

							public boolean isActive() {
								return true;
							}
						}).setNameFormatArgs(new Object[]{"P"}));
					}
				}

				rightClickOptions.add((new RightClickOption("gui.xaero_right_click_waypoint_teleport", rightClickOptions.size(), target) {
					public void onAction(Screen screen) {
						SupportMods.xaeroMinimap.teleportToWaypoint(screen, element);
					}

					public boolean isActive() {
						return SupportMods.xaeroMinimap.canTeleport(SupportMods.xaeroMinimap.getWaypointWorld());
					}
				}).setNameFormatArgs(new Object[]{"T"}));
				rightClickOptions.add(new RightClickOption("gui.xaero_right_click_waypoint_share", rightClickOptions.size(), target) {
					public void onAction(Screen screen) {
						SupportMods.xaeroMinimap.shareWaypoint(element, (GuiMap) screen, SupportMods.xaeroMinimap.getWaypointWorld());
					}
				});
				rightClickOptions.add((new RightClickOption("", rightClickOptions.size(), target) {
					public String getName() {
						return element.isTemporary() ? "gui.xaero_right_click_waypoint_restore" : (element.isDisabled() ? "gui.xaero_right_click_waypoint_enable" : "gui.xaero_right_click_waypoint_disable");
					}

					public void onAction(Screen screen) {
						if (element.isTemporary()) {
							SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
						} else {
							SupportMods.xaeroMinimap.disableWaypoint(element);
						}

					}
				}).setNameFormatArgs(new Object[]{"H"}));
				rightClickOptions.add((new RightClickOption("", rightClickOptions.size(), target) {
					public String getName() {
						return element.isTemporary() ? "gui.xaero_right_click_waypoint_delete_confirm" : "gui.xaero_right_click_waypoint_delete";
					}

					public void onAction(Screen screen) {
						if (element.isTemporary()) {
							SupportMods.xaeroMinimap.deleteWaypoint(element);
						} else {
							SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
						}

					}
				}).setNameFormatArgs(new Object[]{"DEL"}));
				cir.setReturnValue(rightClickOptions);
			}
		}
	}
}
