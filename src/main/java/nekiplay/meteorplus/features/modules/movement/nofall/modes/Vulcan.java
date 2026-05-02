package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ServerboundMovePlayerPacketAccessor;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.utils.MovementUtils;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

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
		if(vulCanNoFall && mc.player.onGround() && vulCantNoFall) {
			vulCantNoFall = false;
		}
		if(vulCantNoFall) return;
		if(nextSpoof) {
			mc.player.getDeltaMovement().add(0, -0.1, 0);
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
		if (event.packet instanceof ServerboundMovePlayerPacket) {
			ServerboundMovePlayerPacket packet = (ServerboundMovePlayerPacket) event.packet;
			ServerboundMovePlayerPacketAccessor accessor = (ServerboundMovePlayerPacketAccessor) packet;


			accessor.meteor$setOnGround(true);
			doSpoof = false;
			accessor.meteor$setY((double) Math.round(mc.player.position().y * 2) / 2);
			mc.player.setPos(mc.player.position().x, ((ServerboundMovePlayerPacket) event.packet).getY(mc.player.position().y), mc.player.position().z);
		}
	}
}
