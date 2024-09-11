package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import nekiplay.meteorplus.settings.MeteorPlusSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultSettingsWidgetFactory.class)
public abstract class DefaultSettingsWidgetFactoryMixin extends SettingsWidgetFactory {
	public DefaultSettingsWidgetFactoryMixin(GuiTheme theme) {
		super(theme);
	}

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void onInit(GuiTheme theme, CallbackInfo ci) {
		new MeteorPlusSettings(factories, this.theme).addSettings();
	}
}
