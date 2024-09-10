package nekiplay.meteorplus.mixin.meteorclient.utils.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = meteordevelopment.meteorclient.utils.misc.Keybind.class, remap = false)
public class KeybindMixin {
	@Inject(method = "canBindTo", at = @At("HEAD"), cancellable = true)
	public void canBind(boolean isKey, int value, int modifers, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
