package nekiplay.meteorplus.utils;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CustomSpeedUtils {
	public static void applySpeed(PlayerMoveEvent event, double speed) {
		Vec3d vel = PlayerUtils.getHorizontalVelocity(speed);
		double velX = vel.getX();
		double velZ = vel.getZ();

		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			double value = (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
			velX += velX * value;
			velZ += velZ * value;
		}

		Anchor anchor = Modules.get().get(Anchor.class);
		if (anchor.isActive() && anchor.controlMovement) {
			velX = anchor.deltaX;
			velZ = anchor.deltaZ;
		}

		((IVec3d) event.movement).set(velX, event.movement.y, velZ);
	}
}
