package nekiplay.meteorplus.mixin.meteorclient.utils.misc;

import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(value = meteordevelopment.meteorclient.utils.misc.Keybind.class, remap = false)
public class KeybindMixin {
	// New Meteor 26.2+ signature: canBindTo(InputConstants.Key, Collection<Modifier>)
	@Inject(method = "canBindTo(Lcom/mojang/blaze3d/platform/InputConstants$Key;Ljava/util/Collection;)Z", at = @At("HEAD"), cancellable = true, require = 0)
	public void canBind(InputConstants.Key key, Collection<?> modifiers, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	// Legacy signature: canBindTo(boolean, int, int) - kept for backward compat, delegates to new one
	@Inject(method = "canBindTo(ZII)Z", at = @At("HEAD"), cancellable = true, require = 0)
	public void canBind(boolean isKey, int value, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
