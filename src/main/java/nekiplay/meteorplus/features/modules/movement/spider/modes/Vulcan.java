package nekiplay.meteorplus.features.modules.movement.spider.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ServerboundMovePlayerPacketAccessor;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderMode;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderModes;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.phys.Vec3;

public class Vulcan extends SpiderMode {
	public Vulcan() {
		super(SpiderModes.Vulcan);
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
		startY = mc.player.position().y;
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
			if (packet instanceof ServerboundMovePlayerPacket move) {
				assert mc.player != null;
				double y = mc.player.getY();
				y = move.getY(y);

				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((ServerboundMovePlayerPacketAccessor) packet).meteor$setOnGround(true);
				}
				if (mc.player.onGround() && block) {
					block = false;
					startY = mc.player.position().y;
					start = false;
				}
			}
		} else {
			assert mc.player != null;
			if (mc.player.onGround() && block) {
				block = false;
				startY = mc.player.position().y;
				start = false;
			}
		}
	}

	private boolean block = false;

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (modify) {
			LocalPlayer player = mc.player;
			assert player != null;
			double y = player.position().y;
			if (lastY == y && tick > 1) {
				block = true;
			} else {
				lastY = y;
			}
		}
	}

	private TypeStarted getType(double startY) {
		TypeStarted temp = TypeStarted.Air;
		double y = RGround(startY);
		assert mc.player != null;
		if (mc.player.onGround()) {
			temp = TypeStarted.Block;
			assert mc.level != null;
			if (mc.level.getBlockState(mc.player.blockPosition()).getBlock() instanceof SlabBlock) {
				temp = TypeStarted.Slab;
			}
		}
		return temp;
	}

	private enum TypeStarted {
		Block,
		Slab,
		Air,
	}

	private TypeStarted typeStarted = TypeStarted.Air;

	@Override
	public void onTickEventPost(TickEvent.Post event) {
		LocalPlayer player = mc.player;
		assert player != null;
		Vec3 pl_velocity = player.getDeltaMovement();
		ClientPacketListener h = mc.getConnection();
		modify = player.horizontalCollision;
		if (mc.player.onGround()) {
			block = false;
			startY = mc.player.position().y;
			start = false;
			typeStarted = getType(startY);
		}
		if (player.horizontalCollision) {
			if (!start) {
				start = true;
				startY = mc.player.position().y;
				lastY = mc.player.getY();
			}
			if (!block) {
				if (tick == 0) {
					mc.player.setDeltaMovement(pl_velocity.x, 0.41999998688698, pl_velocity.z);
					tick = 1;
				} else if (tick == 1) {
					mc.player.setDeltaMovement(pl_velocity.x, 0.33319999363 - coff, pl_velocity.z);
					tick = 2;
				} else if (tick == 2) {
					mc.player.setDeltaMovement(pl_velocity.x, 0.24813599862 - coff, pl_velocity.z);
					tick = 0;
				}
				switch (typeStarted) {
					case Air -> {
						if (mc.player.position().y >= startY + 1.5) {
							block = true;
						}
					}
					case Slab -> {
						if (mc.player.position().y >= startY + 2.5) {
							block = true;
						}
					}
					case Block -> {
						if (mc.player.position().y >= startY + 2) {
							block = true;
						}
					}
				}
			}
		} else {
			modify = false;
			tick = 0;
		}
	}
}
