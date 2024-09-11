package nekiplay.meteorplus.features.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyMode;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyModes;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class OldFag extends ElytraFlyMode {
	private final Vec3d vec3d = new Vec3d(0,0,0);

	public OldFag() {
		super(ElytraFlyModes.OldFag);
	}

	@Override
	public void onDeactivate() {
		mc.player.getAbilities().flying = false;
		mc.player.getAbilities().allowFlying = false;

		mc.player.setJumping(false);
		mc.player.setSprinting(true);
		mc.player.jump();

		mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
	}

	@Override
	public void onTick() {
		super.onTick();

		if (mc.player.getInventory().getArmorStack(2).getItem() != Items.ELYTRA || mc.player.fallDistance <= 0.2 || mc.options.sneakKey.isPressed()) return;

		if (mc.options.forwardKey.isPressed()) {
			vec3d.add(0, 0, 2.5);
			vec3d.rotateY(-(float) Math.toRadians(mc.player.getYaw()));
		} else if (mc.options.backKey.isPressed()) {
			vec3d.add(0, 0, 2.5);
			vec3d.rotateY((float) Math.toRadians(mc.player.getYaw()));
		}

		if (mc.options.jumpKey.isPressed()) {
			vec3d.add(0, 0, 0);
		} else if (!mc.options.jumpKey.isPressed()) {
			vec3d.add(0, -0, 0);
		}

		mc.player.setVelocity(vec3d);
		if (!mc.options.jumpKey.isPressed()) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
		}

	}

	@Override
	public void onPacketSend(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket) {
			if (!mc.options.jumpKey.isPressed()) {
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
			}
		}
	}

	@Override
	public void onPlayerMove() {
		mc.player.getAbilities().flying = true;
		mc.player.getAbilities().setFlySpeed(2.5f / 20);
		if (!PlayerUtils.isMoving()) {
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
		}
	}
}
