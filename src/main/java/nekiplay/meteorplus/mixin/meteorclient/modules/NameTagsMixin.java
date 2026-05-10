package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import nekiplay.meteorplus.features.modules.combat.Teams;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Nametags.class)
public class NameTagsMixin {
	@Inject(method = "renderNametagPlayer", at = @At("HEAD"), cancellable = true)
	private void onRenderNametagPlayer(Render2DEvent event, Player player, boolean shadow, CallbackInfo ci) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		Teams teams = Modules.get().get(Teams.class);
		if (antiBotPlus != null && teams != null) {
			boolean ignore = antiBotPlus.isBot(player);
			if (!ignore) {
				ignore = teams.isInYourTeam(player);
			}
			if (ignore) {
				ci.cancel();
			}
		}
	}
}
