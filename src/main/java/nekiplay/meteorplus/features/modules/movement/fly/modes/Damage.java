package nekiplay.meteorplus.features.modules.movement.fly.modes;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import net.minecraft.util.math.Vec3d;
import nekiplay.meteorplus.features.modules.movement.fly.FlyMode;
import nekiplay.meteorplus.features.modules.movement.fly.FlyModes;

public class Damage extends FlyMode {
	public Damage() {
		super(FlyModes.Damage);
	}

	public static int workingTicks = 15;
	public static int workingUpTicks = 0;
	public static double speed = 0;
	public static double speedUp = 0;

	@Override
	public void onActivate() {
		damaged = false;
		ticks = 0;
		ticks_up = 0;
	}
	private int ticks = 0;
	private int ticks_up = 0;
	private boolean damaged = false;

	public void onTickEventPre(TickEvent.Pre event) {
		if (damaged && ticks != workingTicks) {
			float yaw = mc.player.getYaw();
			Vec3d forward = Vec3d.fromPolar(0, yaw);
			double velX = 0;
			double velZ = 0;
			double s = speed;
			if (mc.options.forwardKey.isPressed()) {
				velX += forward.x * s;
				velZ += forward.z * s;
			}
			if (mc.options.backKey.isPressed()) {
				velX -= forward.x * s;
				velZ -= forward.z * s;
			}

			if (ticks_up < workingUpTicks) {
				((IVec3d) mc.player.getVelocity()).set(velX, speedUp, velZ);
			}
			else {
				((IVec3d) mc.player.getVelocity()).set(velX, 0, velZ);
			}
			ticks++;
			ticks_up++;
		}
		else if (damaged) {
			damaged = false;
			ticks = 0;
			ticks_up = 0;
		}
	}

	@Override
	public void onDamage(DamageEvent event) {
		if (event.entity == mc.player) {
			damaged = true;
			ticks = 0;
			ticks_up = 0;
		}
	}
}
