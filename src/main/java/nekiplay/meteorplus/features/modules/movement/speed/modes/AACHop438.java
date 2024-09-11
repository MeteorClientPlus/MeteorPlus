package nekiplay.meteorplus.features.modules.movement.speed.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedMode;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedModes;

public class AACHop438 extends SpeedMode {
	public AACHop438() {
		super(SpeedModes.AAC_Hop_4dot3dot8);
	}

	@Override
	public void onDeactivate() {
		Modules.get().get(Timer.class).setOverride(Timer.OFF);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		Timer timer = Modules.get().get(Timer.class);
		timer.setOverride(Timer.OFF);
		if (!PlayerUtils.isMoving() || mc.player.isTouchingWater() || mc.player.isInLava() ||
			mc.player.isClimbing() || mc.player.isRiding()) return;

		if (mc.player.isOnGround())
			mc.player.jump();
		else {
			if (mc.player.fallDistance <= 0.1)
				timer.setOverride(1.5);
			else if (mc.player.fallDistance < 1.3)
				timer.setOverride(0.7);
			else
				timer.setOverride(Timer.OFF);
		}
	}
}
