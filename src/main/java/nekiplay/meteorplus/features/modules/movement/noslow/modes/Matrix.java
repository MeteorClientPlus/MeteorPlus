package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowPlus;
import net.minecraft.world.phys.Vec3;

public class Matrix extends NoSlowMode {
	public Matrix() {
		super(NoSlowModes.Matrix);
	}
	private int ticks = 0;

	@Override
	public void onActivate() {
		ticks = 0;
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (mc.player.isUsingItem()) {
			if (mc.player.onGround()) {
				if (ticks % 2 == 0) {
					float speed = 0.4f;
					Vec3 vel = mc.player.getDeltaMovement();
					double x = vel.x() * speed;
					double z = vel.z() * speed;

					mc.player.setDeltaMovement(x, vel.y(), z);
				}
			}
		}
		ticks++;
	}
}
