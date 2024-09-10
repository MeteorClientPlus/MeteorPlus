package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Phase extends Module {
	public Phase() {
		super(Categories.Movement, "phase", "Block glitch.");
	}

	@Override
	public void onActivate() {
		double cos = Math.cos(Math.toRadians(mc.player.getYaw() + 90.0F));
		double sin = Math.sin(Math.toRadians(mc.player.getYaw() + 90.0F));

			mc.player.setPos(mc.player.getX() + 1.0 * 1 * cos + 1.0 * sin, mc.player.getY(), 1 + mc.player.getZ() * sin - 0 * 1 * cos);
	}
}
