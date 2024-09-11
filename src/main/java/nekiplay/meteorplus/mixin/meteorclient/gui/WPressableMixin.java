package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(WPressable.class)
public abstract class WPressableMixin extends WWidget {
	@Shadow(remap = false)
	protected boolean pressed = false;

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
		if (isValidClick(button, used))
			pressed = true;
		return pressed;
	}

	@Unique
	private boolean isValidClick(int button, boolean used) {
		boolean isValidMouseButton = (
			button == GLFW_MOUSE_BUTTON_LEFT ||
				button == GLFW_MOUSE_BUTTON_RIGHT ||
				button == GLFW_MOUSE_BUTTON_MIDDLE
		);
		return mouseOver && isValidMouseButton && !used;
	}
}
