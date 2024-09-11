package nekiplay.meteorplus.features.modules.movement.spider.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderMode;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderModes;

public class Matrix extends SpiderMode {
	public Matrix() {
		super(SpiderModes.Matrix);
	}

	private int tick = 0;
	private boolean modify = false;
	private boolean start = false;

	private double startY = 0;
	private double lastY = 0;

	private double coff = 0.0000000000326;

	@Override
	public void onActivate() {
		tick = 0;
		start = false;
		modify = false;

		assert mc.player != null;
		startY = mc.player.getPos().y;
	}

	private boolean YGround(double height, double min, double max) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		return y >= min && y <= max;
	}

	private double RGround(double height) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		return Double.parseDouble(yString);
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		work(event.packet);
	}

	@Override
	public void onSentPacket(PacketEvent.Sent event) {
		work(event.packet);
	}

	private void work(Packet<?> packet) {
		if (modify) {
			if (packet instanceof PlayerMoveC2SPacket move) {
				assert mc.player != null;
				double y = mc.player.getY();
				y = move.getY(y);

				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (mc.player.isOnGround() && block) {
					block = false;
					startY = mc.player.getPos().y;
					start = false;
				}
			}
		} else {
			assert mc.player != null;
			if (mc.player.isOnGround() && block) {
				block = false;
				startY = mc.player.getPos().y;
				start = false;
			}
		}
	}

	private boolean block = false;

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (modify) {
			ClientPlayerEntity player = mc.player;
			double y = player.getPos().y;
			if (lastY == y && tick > 1) {
				block = true;
			} else {
				lastY = y;
			}
		}
	}

	@Override
	public void onTickEventPost(TickEvent.Post event) {
		ClientPlayerEntity player = mc.player;
		assert player != null;
		Vec3d pl_velocity = player.getVelocity();
		Vec3d pos = player.getPos();
		ClientPlayNetworkHandler h = mc.getNetworkHandler();
		modify = player.horizontalCollision;
		if (mc.player.isOnGround()) {
			block = false;
			startY = mc.player.getPos().y;
			start = false;
		}
		if (player.horizontalCollision) {
			if (!start) {
				start = true;
				startY = mc.player.getPos().y;
				lastY = mc.player.getY();
			}
			if (!block) {
				if (tick == 0) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
					tick = 1;
				} else if (tick == 1) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
					tick = 2;
				} else if (tick == 2) {
					mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
					tick = 0;
				}
			}
		}
		else {
			modify = false;
			tick = 0;
		}
	}
}
