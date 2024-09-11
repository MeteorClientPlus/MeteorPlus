package nekiplay.meteorplus.features.modules.movement.spider.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderMode;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderModes;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderPlus;
import nekiplay.meteorplus.utils.ElytraUtils;

public class Eclip extends SpiderMode {
	public Eclip() {
		super(SpiderModes.Elytra_clip);
	}
	private int ticks = 0;
	private int slot = -1;
	private double blocks = 0;

	@Override
	public void onActivate() {
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (!elytra.found()) {
			settings.error(Names.get(Items.ELYTRA) + " not found");
			settings.toggle();
		}
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (work() && mc.player.horizontalCollision) {
			blocks = Modules.get().get(SpiderPlus.class).Blocks.get();
			clip();
		} else {
			ticks = 0;
		}
	}
	private boolean work() {
		ClientPlayerEntity player = mc.player;
		assert player != null;
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (elytra.found()) {
			return true;
		}
		else {
			return false;
		}
	}
	private void clip() {
		if (blocks != 0) {
			ClientPlayerEntity player = mc.player;
			assert player != null;
			switch (ticks) {
				case 0: {
					FindItemResult elytra = InvUtils.find(Items.ELYTRA);
					slot = elytra.slot();
					InvUtils.move().from(slot).toArmor(2);
					ticks++;
				}
				case 1: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 2: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 3: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 4: {
					player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + blocks, player.getZ(), false));
					ticks++;
				}
				case 5: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 6: {
					ticks = 0;
					blocks = 0;
					InvUtils.move().fromArmor(2).to(slot);
				}
			}
		}
	}
}
