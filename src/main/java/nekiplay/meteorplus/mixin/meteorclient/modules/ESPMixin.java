package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import nekiplay.meteorplus.features.modules.combat.Teams;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ESP.class)
public class ESPMixin {
	@Inject(method = "shouldSkip", at = @At("RETURN"), cancellable = true)
	protected void shouldSkip(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		Teams teams = Modules.get().get(Teams.class);
		if (antiBotPlus != null && teams != null && !cir.getReturnValue() && entity instanceof PlayerEntity) {
			boolean ignore = antiBotPlus.isBot(entity);
			if (!ignore) {
				ignore = teams.isInYourTeam(entity);
			}
			cir.setReturnValue(ignore);
		}
	}
}
