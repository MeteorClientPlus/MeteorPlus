package nekiplay.bozeplus.features.modules.movement.spider.modes;

import dev.boze.api.event.EventTick;
import nekiplay.bozeplus.features.modules.movement.spider.SpiderMode;
import nekiplay.bozeplus.features.modules.movement.spider.SpiderModes;
import nekiplay.bozeplus.features.modules.movement.spider.SpiderPlus;
import nekiplay.bozeplus.mixin.minecraft.PlayerMoveC2SPacketAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Matrix extends SpiderMode {
	public Matrix(SpiderPlus spiderPlus) {
		super(SpiderModes.Matrix, spiderPlus);
	}

	private int tick = 0;
	private boolean modify = false;
	private boolean start = false;
	private double startY = 0;
	private double lastY = 0;
	private double coff = 0.0000000000326;
	private boolean block = false;

	@Override
	public void onActivate() {
		tick = 0;
		start = false;
		modify = false;

		if (mc.player != null) {
			startY = mc.player.getPos().y;
		}
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
	private void work(Packet<?> packet) {
		if (modify && mc.player != null) {
			if (packet instanceof PlayerMoveC2SPacket move) {
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
		} else if (mc.player != null) {
			if (mc.player.isOnGround() && block) {
				block = false;
				startY = mc.player.getPos().y;
				start = false;
			}
		}
	}

	@Override
	public void onTickEventPre(EventTick.Pre event) {
		if (modify && mc.player != null) {
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
	public void onTickEventPost(EventTick.Post event) {
		ClientPlayerEntity player = mc.player;
		if (player != null) {
			Vec3d pl_velocity = player.getVelocity();
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
			} else {
				modify = false;
				tick = 0;
			}
		}
	}
}
