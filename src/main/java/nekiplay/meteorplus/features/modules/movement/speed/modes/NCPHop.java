package nekiplay.meteorplus.features.modules.movement.speed.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedMode;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedModes;

public class NCPHop extends SpeedMode {
	public NCPHop() {
		super(SpeedModes.NCP_Hop);
	}

	@Override
	public void onActivate() {
		Modules.get().get(Timer.class).setOverride(1.0865f);
	}

	@Override
	public void onDeactivate() {
		Modules.get().get(Timer.class).setOverride(Timer.OFF);
		mc.player.getAbilities().setFlyingSpeed(0.02f);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (mc.player.isInWater() || mc.player.isInLava() ||
			mc.player.onClimbable() || mc.player.isHandsBusy()) return;
		Timer timer = Modules.get().get(Timer.class);
		if (PlayerUtils.isMoving() && mc.player.onGround()) {
			mc.player.jumpFromGround();
			mc.player.getAbilities().setFlyingSpeed(0.0223f);
		} else {
			timer.setOverride(1);
		}
	}
}
