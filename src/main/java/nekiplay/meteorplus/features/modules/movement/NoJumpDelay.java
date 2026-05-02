package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoJumpDelay extends Module {
	public NoJumpDelay() {
		super(Categories.Movement, "no-jump-delay", "Remove jump delay.");
	}
}
