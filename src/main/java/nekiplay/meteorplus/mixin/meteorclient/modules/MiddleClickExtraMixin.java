package nekiplay.meteorplus.mixin.meteorclient.modules;


import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static nekiplay.meteorplus.MeteorPlusAddon.HUD_TITLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

@Mixin(value = MiddleClickExtra.class, remap = false)
public class MiddleClickExtraMixin extends Module {
	public MiddleClickExtraMixin() {
        super(Categories.Player, "middle-click-extra", "Perform various actions when you middle click.");
	}

	@Final
	@Shadow
	private final SettingGroup sgGeneral = settings.getDefaultGroup();


	@Unique
	private final Setting<Boolean> noInventory = sgGeneral.add(new BoolSetting.Builder()
		.name("Anti-inventory")
		.description("Not work in inventory.")
		.defaultValue(true)
		.build()
	);

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	private void onMouseButton(MouseButtonEvent event, CallbackInfo ci) {
		if (event.action == KeyAction.Press && event.button == 2 && mc.currentScreen == null) {
			if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_MIDDLE) return;
			if (noInventory.get() && mc.currentScreen != null) {
				ci.cancel();
			}
		}
	}
}
