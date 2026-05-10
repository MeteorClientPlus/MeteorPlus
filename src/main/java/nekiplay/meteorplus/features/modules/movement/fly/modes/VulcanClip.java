package nekiplay.meteorplus.features.modules.movement.fly.modes;

import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import nekiplay.meteorplus.features.modules.movement.fly.FlyMode;
import nekiplay.meteorplus.features.modules.movement.fly.FlyModes;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

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
		if (mc.player.onGround() && settings.canClip.get()) {
			clip(0f, -0.1f);
			waitFlag = true;
			canGlide = false;
			ticks = 0;
			timer.setOverride(0.1f);
		} else {
			waitFlag = false;
			canGlide = true;
		}
	}

	@Override
	public void onPlayerMoveSendPre(SendMovementPacketsEvent.Pre event) {

		if (canGlide) {
			timer.setOverride(1f);
			Vec3 velocity = mc.player.getDeltaMovement();
			velocity.add(0, -(ticks % 2 == 0 ? 0.17 : 0.10), 0);
			if (ticks == 0) {
				velocity.add(0, -0.07, 0);
			}
			mc.player.setDeltaMovement(velocity);
			ticks++;
		}
	}

	@Override
	public void onReceivePacket(PacketEvent.Receive event) {
		super.onReceivePacket(event);
		if (event.packet instanceof ClientboundPlayerPositionPacket packet && waitFlag) {
			Vec3 playerPos = mc.player.position();
			waitFlag = false;
			mc.player.setPos(packet.change().position().x, packet.change().position().y, packet.change().position().z);
			mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(playerPos.x, playerPos.y, playerPos.z, false, mc.player.horizontalCollision));
			event.cancel();
			mc.player.jumpFromGround();
			clip(0.127318f, 0f);
			clip(3.425559f, 3.7f);
			clip(3.14285f, 3.54f);
			clip(2.88522f, 3.4f);
			canGlide = true;
		}
	}

	private void clip(float dist, float y) {
		float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		double yaw = Math.toRadians(mc.player.getViewYRot(tickDelta));
		double x = -sin(yaw) * dist;
		double z = cos(yaw) * dist;
		mc.player.setPos(mc.player.position().x + x, mc.player.position().y + y, mc.player.position().z + z);
		mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false, mc.player.horizontalCollision));
	}
}
