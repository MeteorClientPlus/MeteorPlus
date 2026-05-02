package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

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
				if (mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
					InvUtils.move().from(slot).toArmor(2);
				}
			}

			if (mc.player.fallDistance > 2.7) {
				mc.player.connection.send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
				mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
				Vec3 vel = mc.player.getDeltaMovement();
				mc.player.setDeltaMovement(vel.x, 0, vel.z);
				mc.player.fallDistance = 0.0f;
				mc.player.setOnGround(true);
			}
		}
	}
}
