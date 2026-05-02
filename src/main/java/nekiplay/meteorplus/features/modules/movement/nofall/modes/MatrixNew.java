package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.ServerboundMovePlayerPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Iterator;

public class MatrixNew extends NoFallMode {
	public MatrixNew() {
		super(NoFallModes.Matrix_New);
	}

	private Timer timer;

	@Override
	public void onDeactivate() {
		timer = Modules.get().get(Timer.class);
		timer.setOverride(Timer.OFF);
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof ServerboundMovePlayerPacket) {
			ServerboundMovePlayerPacket packet = (ServerboundMovePlayerPacket) event.packet;
			ServerboundMovePlayerPacketAccessor accessor = (ServerboundMovePlayerPacketAccessor) packet;
			timer = Modules.get().get(Timer.class);

			if (!mc.player.onGround()) {
				if (mc.player.fallDistance > 2.69) {
					timer.setOverride(0.3);
					accessor.meteor$setOnGround(true);
					mc.player.fallDistance = 0;
				}
				if (mc.player.fallDistance > 3.5) {
					timer.setOverride(0.3);
				} else {
					timer.setOverride(Timer.OFF);
				}
			}
			Iterator<VoxelShape> voxelShapeIterator = mc.level.getCollisions(mc.player, mc.player.getBoundingBox().move(0.0, mc.player.getDeltaMovement().y, 0.0)).iterator();
			boolean isEmpty = true;
			while (voxelShapeIterator.hasNext()) {
				VoxelShape shape = voxelShapeIterator.next();
				isEmpty = shape.isEmpty();
			}
			if (!isEmpty) {
				if (!((ServerboundMovePlayerPacket) event.packet).isOnGround() && mc.player.getDeltaMovement().y < -0.6) {
					accessor.meteor$setOnGround(true);
				}
			}
		}
	}
}
