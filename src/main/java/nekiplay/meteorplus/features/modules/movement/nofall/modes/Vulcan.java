package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.utils.MovementUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Vulcan extends NoFallMode {
	public Vulcan() {
		super(NoFallModes.Vulcan);
	}

	private boolean vulCanNoFall = false;
	private boolean vulCantNoFall = false;
	private boolean nextSpoof = false;
	private boolean doSpoof = false;

	@Override
	public void onActivate() {
		vulCanNoFall = false;
		vulCantNoFall = false;
		nextSpoof = false;
		doSpoof = false;
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if(!vulCanNoFall && mc.player.fallDistance > 3.25) {
			vulCanNoFall = true;
		}
		if(vulCanNoFall && mc.player.isOnGround() && vulCantNoFall) {
			vulCantNoFall = false;
		}
		if(vulCantNoFall) return;
		if(nextSpoof) {
			mc.player.getVelocity().add(0, -0.1, 0);
			mc.player.fallDistance = -0.1f;
			MovementUtils.strafe(0.3f);
			nextSpoof = false;
		}
		if(mc.player.fallDistance > 3.5625f) {
			mc.player.fallDistance = 0.0f;
			doSpoof = true;
			nextSpoof = true;
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket) {
			PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.packet;
			PlayerMoveC2SPacketAccessor accessor = (PlayerMoveC2SPacketAccessor) packet;


			accessor.setOnGround(true);
			doSpoof = false;
			accessor.setY((double) Math.round(mc.player.getPos().y * 2) / 2);
			mc.player.setPosition(mc.player.getPos().x, ((PlayerMoveC2SPacket) event.packet).getY(mc.player.getPos().y), mc.player.getPos().z);
		}
	}
}
