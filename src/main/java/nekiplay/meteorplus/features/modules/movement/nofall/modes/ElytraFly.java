package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ElytraFly extends NoFallMode {
	public ElytraFly() {
		super(NoFallModes.Elytra_Fly);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {

		if (mc.player.fallDistance > 2) {
			FindItemResult elytra = InvUtils.find(Items.ELYTRA);
			if (elytra.found()) {
				int slot = elytra.slot();
				if (mc.player.getInventory().getArmorStack(2).getItem() != Items.ELYTRA) {
					InvUtils.move().from(slot).toArmor(2);
				}
			}

			if (mc.player.fallDistance > 2.7) {
				mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
				Vec3d vel = mc.player.getVelocity();
				mc.player.setVelocity(vel.x, 0, vel.z);
				mc.player.fallDistance = 0.0f;
				mc.player.setOnGround(true);
			}
		}
	}
}
