package nekiplay.meteorplus.mixin.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.ButtonList;
import journeymap.client.ui.waypoint.WaypointManagerItem;
import journeymap.client.waypoint.Waypoint;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.integrations.MapIntegration;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaypointManagerItem.class)
public class WaypointManagerItemMixin {

	@Shadow(remap = false)
	ButtonList buttonListLeft;
	@Shadow(remap = false)
	Waypoint waypoint;
	@Shadow(remap = false)
	boolean displayHover = true;
	@Unique
	Button gotoButton;
	@Unique
	Button pathButton;

	@Inject(method = "<init>", at = @At("JUMP"))
	private void onInit(CallbackInfo info) {
		gotoButton = new Button(Text.translatable("gui.world_map.baritone_goal_here").getString());
		pathButton = new Button(Text.translatable("gui.world_map.baritone_path_here").getString());
		MapIntegration mapIntegration = Modules.get().get(MapIntegration.class);
		if (mapIntegration != null && mapIntegration.isActive() && mapIntegration.baritoneGoto.get()) {
			buttonListLeft.add(gotoButton);
			buttonListLeft.add(pathButton);
		}
	}

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void onRender(DrawContext graphics, int slotIndex, int y, int x, int rowWidth, int itemHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo ci) {
		boolean drawHovered = isMouseOver && this.displayHover;
		MapIntegration mapIntegration = Modules.get().get(MapIntegration.class);
		if (mapIntegration != null && mapIntegration.isActive() && mapIntegration.baritoneGoto.get()) {
			gotoButton.drawHovered(drawHovered);
			pathButton.drawHovered(drawHovered);
		}
	}
	@Inject(method = "clickScrollable", at = @At(value = "HEAD"), remap = false, cancellable = true)
	private void onClickScrollable(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
		if (gotoButton.mouseOver(mouseX, mouseY)) {
			GoalBlock goal = new GoalBlock(waypoint.getBlockPos());
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(goal);
			cir.setReturnValue(true);
		}
		if (pathButton.mouseOver(mouseX, mouseY)) {
			GoalBlock goal = new GoalBlock(waypoint.getBlockPos());
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			cir.setReturnValue(true);
		}
	}
}
