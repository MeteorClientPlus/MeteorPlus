package nekiplay.meteorplus.mixin.xaero.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import nekiplay.MixinPlugin;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.region.*;
import meteordevelopment.meteorclient.utils.misc.Names;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin {
	@Unique
	private final GuiMap guiMap = (GuiMap)(Object) this;
	@Shadow(remap = false)
	private double scale;
	@Shadow(remap = false)
	private int rightClickX;
	@Shadow(remap = false)
	private int rightClickY;
	@Shadow(remap = false)
	private int rightClickZ;
	@Shadow(remap = false)
	private MapProcessor mapProcessor;
	@Shadow(remap = false)
	private double cameraX = 0.0;
	@Shadow(remap = false)
	private double cameraZ = 0.0;
	@Inject(method = "<init>", at = @At("JUMP"))
	private void onInit(CallbackInfo info) {
		cameraX = ((float) mc.player.getX());
		cameraZ = ((float) mc.player.getZ());
	}

	@Inject(method = "getRightClickOptions", at = @At(value = "RETURN"), remap = false)
	private void rightClickOptins(CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		Modules modules = Modules.get();
		if (modules != null) {
			MapIntegration mapIntegration = modules.get(MapIntegration.class);
			if (mapIntegration != null && mapIntegration.isActive()) {
				final ArrayList<RightClickOption> options = cir.getReturnValue();

				int mouseXPos = (int) Misc.getMouseX(mc, false);
				int mouseYPos = (int) Misc.getMouseY(mc, false);

				int mouseFromCentreX = mouseXPos - mc.getWindow().getFramebufferWidth() / 2;
				int mouseFromCentreY = mouseYPos - mc.getWindow().getFramebufferHeight() / 2;

				double mousePosX = (double) mouseFromCentreX / this.scale + this.cameraX;
				double mousePosZ = (double) mouseFromCentreY / this.scale + this.cameraZ;
				int mouseBlockPosX = (int) Math.floor(mousePosX);
				int mouseBlockPosZ = (int) Math.floor(mousePosZ);

				int renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
				MapRegion leafRegion = this.mapProcessor.getMapRegion(renderedCaveLayer, mouseBlockPosX >> 9, mouseBlockPosZ >> 9, false);
				MapTileChunk chunk = leafRegion == null ? null : leafRegion.getChunk(mouseBlockPosX >> 6 & 7, mouseBlockPosZ >> 6 & 7);
				MapTile mouseTile = chunk == null ? null : chunk.getTile(mouseBlockPosX >> 4 & 3, mouseBlockPosZ >> 4 & 3);

				if (mapIntegration.showBlock.get() != null && mouseTile != null) {
					MapBlock block = mouseTile.getBlock(mouseBlockPosX & 15, mouseBlockPosZ & 15);
					MapPixelAccessor pixel = (MapPixelAccessor) block;

					options.addAll(2, List.of(
						new RightClickOption(Names.get(pixel.getBlockState().getBlock()), options.size(), guiMap) {
							@Override
							public void onAction(Screen screen) {

							}
						}
					));
				}

				if (!MixinPlugin.isXaeroPlusMapresent) {
					if (mapIntegration.baritoneGoto.get()) {
						options.addAll(3, List.of(
							new RightClickOption(I18n.translate("gui.world_map.baritone_goal_here"), options.size(), guiMap) {
								@Override
								public void onAction(Screen screen) {
									GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
									BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
								}
							},
							new RightClickOption(I18n.translate("gui.world_map.baritone_path_here"), options.size(), guiMap) {
								@Override
								public void onAction(Screen screen) {
									GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
									BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
								}
							}
						));
						if (mapIntegration.baritoneElytra.get() && mapIntegration.baritoneGoto.get() && PlayerUtils.getDimension() == Dimension.Nether) {
							if (rightClickY - 1 > 0 && rightClickY < 128) {
								options.addAll(3, List.of(
									new RightClickOption(I18n.translate("gui.world_map.baritone_elytra_here"), options.size(), guiMap) {
										@Override
										public void onAction(Screen screen) {
											GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
											BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
											for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
												if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
													baritone.getCommandManager().execute("elytra");
													break;
												}
											}
										}
									}
								));
							}
						}
					}
				} else {
					if (mapIntegration.baritoneElytra.get() && mapIntegration.baritoneGoto.get() && PlayerUtils.getDimension() == Dimension.Nether) {
						if (rightClickY - 1 > 0 && rightClickY < 128) {
							options.addAll(3, List.of(
								new RightClickOption(I18n.translate("gui.world_map.baritone_elytra_here"), options.size(), guiMap) {
									@Override
									public void onAction(Screen screen) {
										GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
										BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
										for (IBaritone baritone : BaritoneAPI.getProvider().getAllBaritones()) {
											if (!baritone.getCommandManager().getRegistry().stream().filter((a) -> a.getNames().get(0).equalsIgnoreCase("elytra")).findAny().isEmpty()) {
												baritone.getCommandManager().execute("elytra");
												break;
											}
										}
									}
								}
							));
						}
					}
				}
			}
		}
	}
}
