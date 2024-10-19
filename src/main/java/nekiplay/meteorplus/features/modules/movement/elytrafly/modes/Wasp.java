package nekiplay.meteorplus.features.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyMode;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyModes;

public class Wasp extends ElytraFlyMode {
	public Wasp() {
		super(ElytraFlyModes.Wasp);
	}

	private boolean moving;
	private float yaw;
	private float pitch;

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!mc.player.isFallFlying()) {return;}

		updateWaspMovement();
		pitch = mc.player.getPitch();

		double cos = Math.cos(Math.toRadians(yaw + 90));
		double sin = Math.sin(Math.toRadians(yaw + 90));

		double x = moving ? cos * elytraFly.horizontal_wasp.get() : 0;
		double y = -elytraFly.fallSpeed_wasp.get();
		double z = moving ? sin * elytraFly.horizontal_wasp.get() : 0;

		if (elytraFly.smartFall_wasp.get()) {
			y *= Math.abs(Math.sin(Math.toRadians(pitch)));
		}

		if (mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed()) {
			y = -elytraFly.down_wasp.get();
		}
		if (!mc.options.sneakKey.isPressed() && mc.options.jumpKey.isPressed()) {
			y = elytraFly.up_wasp.get();
		}

		((IVec3d) event.movement).set(x, y, z);
		mc.player.setVelocity(0, 0, 0);
	}

	private void updateWaspMovement() {
		float yaw = mc.player.getYaw();

		float f = mc.player.input.movementForward;
		float s = mc.player.input.movementSideways;

		if (f > 0) {
			moving = true;
			yaw += s > 0 ? -45 : s < 0 ? 45 : 0;
		} else if (f < 0) {
			moving = true;
			yaw += s > 0 ? -135 : s < 0 ? 135 : 180;
		} else {
			moving = s != 0;
			yaw += s > 0 ? -90 : s < 0 ? 90 : 0;
		}
		this.yaw = yaw;
	}
}
