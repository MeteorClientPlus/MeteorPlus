package nekiplay.meteorplus.features.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3;
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
		pitch = mc.player.getXRot();

		double cos = Math.cos(Math.toRadians(yaw + 90));
		double sin = Math.sin(Math.toRadians(yaw + 90));

		double x = moving ? cos * elytraFly.horizontal_wasp.get() : 0;
		double y = -elytraFly.fallSpeed_wasp.get();
		double z = moving ? sin * elytraFly.horizontal_wasp.get() : 0;

		if (elytraFly.smartFall_wasp.get()) {
			y *= Math.abs(Math.sin(Math.toRadians(pitch)));
		}

		if (mc.options.keyShift.isDown() && !mc.options.keyJump.isDown()) {
			y = -elytraFly.down_wasp.get();
		}
		if (!mc.options.keyShift.isDown() && mc.options.keyJump.isDown()) {
			y = elytraFly.up_wasp.get();
		}

		((IVec3) event.movement).meteor$set(x, y, z);

		if (elytraFly.resetSpeed.get()) {
			mc.player.setDeltaMovement(0, 0, 0);
		}
	}

	private void updateWaspMovement() {
		float yaw = mc.player.getYRot();

		float forward = mc.player.input.getMoveVector().y;
		float sideways = mc.player.input.getMoveVector().x;

		if (forward > 0) {
			moving = true;
			yaw += sideways > 0 ? -45 : sideways < 0 ? 45 : 0;
		} else if (forward < 0) {
			moving = true;
			yaw += sideways > 0 ? -135 : sideways < 0 ? 135 : 180;
		} else {
			moving = sideways != 0;
			yaw += sideways > 0 ? -90 : sideways < 0 ? 90 : 0;
		}

		this.yaw = yaw;
	}
}
