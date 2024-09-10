package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.shape.VoxelShape;

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
		if (event.packet instanceof PlayerMoveC2SPacket) {
			PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)event.packet;
			PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor)packet;
			timer = Modules.get().get(Timer.class);

			if (!mc.player.isOnGround()) {
				if (mc.player.fallDistance > 2.69) {
					timer.setOverride(0.3);
					accessor.setOnGround(true);
					mc.player.fallDistance = 0;
				}
				if (mc.player.fallDistance > 3.5) {
					timer.setOverride(0.3);
				}
				else {
					timer.setOverride(Timer.OFF);
				}
			}
			Iterator<VoxelShape> voxelShapeIterator = mc.world.getCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)).iterator();
			boolean isEmpty = true;
			while (voxelShapeIterator.hasNext()) {
				VoxelShape shape = voxelShapeIterator.next();
				isEmpty = shape.isEmpty();
			}
			if (!isEmpty) {
				if (!((PlayerMoveC2SPacket) event.packet).isOnGround() && mc.player.getVelocity().y < -0.6) {
					accessor.setOnGround(true);
				}
			}
		}
	}
}
