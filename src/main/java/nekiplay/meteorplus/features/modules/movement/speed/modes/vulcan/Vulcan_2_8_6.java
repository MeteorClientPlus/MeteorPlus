package nekiplay.meteorplus.features.modules.movement.speed.modes.vulcan;

import meteordevelopment.meteorclient.events.entity.player.JumpVelocityMultiplierEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedMode;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedModes;
import nekiplay.meteorplus.utils.MovementUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class Vulcan_2_8_6 extends SpeedMode {
	public Vulcan_2_8_6() {
		super(SpeedModes.Vulcan_2dot8dot6);
	}

	private int ticks = 0;
	private int speedLevel = 0;
	private boolean jumped = false;

	@Override
	public void onJump(JumpVelocityMultiplierEvent event) {
		ticks = 0;
		speedLevel = 0;
		jumped = true;
		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			speedLevel = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
		}
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (jumped) {
			ticks++;
			if (ticks == 1) {
				MovementUtils.strafe(0.3355 * (1 + speedLevel * 0.3819));
			}
			if (ticks == 2) {
				if (mc.player.isSprinting()) {
					MovementUtils.strafe(0.3284 * (1 + speedLevel * 0.355));
				}
			}
			if (ticks == 4) {
				Vec3d vel = mc.player.getPos();
				mc.player.setPos(vel.x, vel.y - 0.376, vel.z);
			}

			if (ticks == 6) {
				if (mc.player.speed > 0.298) {
					MovementUtils.strafe(0.298);
				}
				jumped = false;
			}
		}
	}
}
