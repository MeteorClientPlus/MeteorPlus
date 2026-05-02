package nekiplay.meteorplus.utils;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MovementUtils {
	public static double getSpeed() {
		return Math.sqrt(mc.player.getDeltaMovement().x * mc.player.getDeltaMovement().x + mc.player.getDeltaMovement().z * mc.player.getDeltaMovement().z);
	}
	public static void strafe(float speed) {
		strafe((double)speed);
	}
	public static void strafe(double speed) {
		double yaw = direction();
		double sin = -Math.sin(yaw) * speed;
		double cos = Math.cos(yaw) * speed;
		mc.player.getDeltaMovement().add(cos, 0, sin);
	}
	public static double direction() {
		float yaw = mc.player.getYRot();
		if (mc.player.input.getMoveVector().x < 0) yaw += 180;
		float forward = 1;
		if (mc.player.input.getMoveVector().x < 0) forward -= 0.5; else if (mc.player.input.getMoveVector().x > 0) forward += 0.5;
		if (mc.player.input.getMoveVector().y > 0) yaw -= 90 * forward;
		if (mc.player.input.getMoveVector().y < 0) yaw += 90 * forward;
		return Math.toRadians(yaw);
	}
}
