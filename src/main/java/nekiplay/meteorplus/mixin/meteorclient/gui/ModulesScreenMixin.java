package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.mixinclasses.IModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ModulesScreen.class)
public abstract class ModulesScreenMixin {
	private GuiTheme _theme;

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void ModulesScreen(GuiTheme theme, CallbackInfo ci) {
		_theme = theme;
	}

	@Inject(method = "initWidgets", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	private void initWidgets(CallbackInfo ci, WVerticalList help) {
		help.add(_theme.label("Middle click - Hide module"));
	}

	@Inject(method = "createCategory", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), remap = false, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void createCategory(WContainer c, Category category, CallbackInfoReturnable<WWindow> cir, WWindow w) {
		for (Module module : Modules.get().getGroup(category)) {
			boolean isVisible = !((IModule) module).isHidden();
			if (isVisible) {
				w.add(_theme.module(module)).expandX();
			}
		}
		cir.setReturnValue(w);
	}
}
