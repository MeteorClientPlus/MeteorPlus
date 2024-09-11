package nekiplay.meteorplus.features.modules.movement.fly.modes;

import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import nekiplay.meteorplus.features.modules.movement.fly.FlyMode;
import nekiplay.meteorplus.features.modules.movement.fly.FlyModes;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class VulcanClip extends FlyMode {
	public VulcanClip() {
		super(FlyModes.Vulcan_Clip);
	}

	private boolean waitFlag = false;
	private boolean canGlide = false;
	private int ticks = 0;
	private Timer timer;

	@Override
	public void onDeactivate() {
		timer.setOverride(Timer.OFF);
	}

	@Override
	public void onActivate() {
		timer = Modules.get().get(Timer.class);
		if (mc.player.isOnGround() && settings.canClip.get()) {
			clip(0f, -0.1f);
			waitFlag = true;
			canGlide = false;
			ticks = 0;
			timer.setOverride(0.1f);
		}
		else {
			waitFlag = false;
			canGlide = true;
		}
	}

	@Override
	public void onPlayerMoveSendPre(SendMovementPacketsEvent.Pre event) {

		if (canGlide) {
			timer.setOverride(1f);
			Vec3d velocity = mc.player.getVelocity();
			velocity.add(0, -(ticks % 2 == 0 ? 0.17 : 0.10), 0);
			if(ticks == 0) {
				velocity.add(0, -0.07, 0);
			}
			mc.player.setVelocity(velocity);
			ticks++;
		}
	}

	@Override
	public void onRecivePacket(PacketEvent.Receive event) {
		super.onRecivePacket(event);
		if (event.packet instanceof PlayerPositionLookS2CPacket && waitFlag) {
			PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)event.packet;
			Vec3d playerPos = mc.player.getPos();
			waitFlag = false;
			mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerPos.x, playerPos.y, playerPos.z, false));
			event.cancel();
			mc.player.jump();
			clip(0.127318f, 0f);
			clip(3.425559f, 3.7f);
			clip(3.14285f, 3.54f);
			clip(2.88522f, 3.4f);
			canGlide = true;
		}
	}

	private void clip(float dist, float y) {
		float tickDelta = mc.getRenderTickCounter().getTickDelta(true);
		double yaw = Math.toRadians(mc.player.getYaw(tickDelta));
		double x = -sin(yaw) * dist;
		double z = cos(yaw) * dist;
		mc.player.setPosition(mc.player.getPos().x + x, mc.player.getPos().y + y, mc.player.getPos().z + z);
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
	}
}
