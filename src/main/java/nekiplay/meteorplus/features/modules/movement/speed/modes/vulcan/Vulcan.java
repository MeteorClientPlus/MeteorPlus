package nekiplay.meteorplus.features.modules.movement.speed.modes.vulcan;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedMode;
import nekiplay.meteorplus.features.modules.movement.speed.SpeedModes;
import nekiplay.meteorplus.utils.CustomSpeedUtils;

import java.util.Objects;

public class Vulcan extends SpeedMode {
	public Vulcan() {
		super(SpeedModes.Vulcan);
	}
	public Item chestPlate;
	@Override
	public void onDeactivate() {
		FindItemResult chest = InvUtils.find(chestPlate);
		if (chest.found() && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && settings.autoSwapVulcan.get()) {
			InvUtils.move().from(chest.slot()).toArmor(2);
		}
	}

	@Override
	public void onActivate() {
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (!elytra.found()) {
			settings.error("Elytra not found");
			settings.toggle();
		}
		else {
			if (!SlotUtils.isArmor(elytra.slot()) && settings.autoSwapVulcan.get()) {
				if (mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
					chestPlate = mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem();
					InvUtils.move().from(elytra.slot()).toArmor(2);
				}
			}
		}
	}

	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (mc.player != null && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
			if (mc.player.hasEffect(MobEffects.SPEED) && mc.player.getEffect(MobEffects.SPEED) != null) {
				if (Objects.requireNonNull(mc.player.getEffect(MobEffects.SPEED)).getAmplifier() == 1) {
					CustomSpeedUtils.applySpeed(event, settings.speedVulcanef2.get());
				}
				else if (Objects.requireNonNull(mc.player.getEffect(MobEffects.SPEED)).getAmplifier() == 0) {
					CustomSpeedUtils.applySpeed(event, settings.speedVulcanef1.get());
				}
			}
			else {
				CustomSpeedUtils.applySpeed(event, settings.speedVulcanef0.get());
			}
		}
	}
}
