package nekiplay.meteorplus.features.modules.movement.spider.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderMode;
import nekiplay.meteorplus.features.modules.movement.spider.SpiderModes;
import nekiplay.meteorplus.utils.ElytraUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.item.Items;

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
			blocks = settings.Blocks.get();
			clip();
		} else {
			ticks = 0;
		}
	}

	private boolean work() {
		LocalPlayer player = mc.player;
		assert player != null;
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		return elytra.found();
	}

	private void clip() {
		if (blocks != 0) {
			LocalPlayer player = mc.player;
			assert player != null;
			switch (ticks) {
				case 0: {
					FindItemResult elytra = InvUtils.find(Items.ELYTRA);
					slot = elytra.slot();
					InvUtils.move().from(slot).toArmor(2);
					ticks++;
				}
				case 1: {
					mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(false, mc.player.horizontalCollision));
					ticks++;
				}
				case 2: {
					mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(false, mc.player.horizontalCollision));
					ticks++;
				}
				case 3: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 4: {
					player.setPos(player.getX(), player.getY() + blocks, player.getZ());
					mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(player.getX(), player.getY() + blocks, player.getZ(), false, mc.player.horizontalCollision));
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
