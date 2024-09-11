package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallModes;
import nekiplay.meteorplus.utils.ElytraUtils;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.error;

public class Eclip extends NoFallMode {
	public Eclip() {
		super(NoFallModes.Elytra_Clip);
	}

	private int ticks = 0;
	private int slot = -1;
	private int blocks = 0;
	private boolean cliped = false;
	private boolean groundcheck = false;
	private int timer = 0;
	private int teleports = 0;
	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (!elytra.found()) {
			error("Elytra not found");
			settings.toggle();
		}
		else {

			if (mc.player.isOnGround() && groundcheck) {
				groundcheck = false;
				cliped = false;
				ChatUtils.infoPrefix("No Fall Plus", "Grounded in " + teleports + " teleports");
				mc.player.fallDistance = 0;
				teleports = 0;
			} else if (mc.player.fallDistance > 3) {
				BlockHitResult result = mc.world.raycast(new RaycastContext(mc.player.getPos(), mc.player.getPos().subtract(0, 10, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
				if (result != null && result.getType() == HitResult.Type.BLOCK) {
					blocks = result.getBlockPos().add(0, 1, 0).getY();
					cliped = true;
				} else if (result == null || result.getType() == HitResult.Type.MISS) {
					blocks = (int) mc.player.getPos().y - 10;
					cliped = true;
				}
			}
			if (cliped) {
				clip();
			}
		}
	}

	@Override
	public void onSendPacket(PacketEvent.Send event) {
		if (!groundcheck) return;
		if (!(event.packet instanceof PlayerMoveC2SPacket)
			|| ((IPlayerMoveC2SPacket) event.packet).getTag() == 1337) return;
		((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
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
					groundcheck = true;
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
					player.setPosition(player.getX(), blocks, player.getZ());
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), blocks, player.getZ(), true));
					teleports++;
					ticks++;
				}
				case 5: {
					ticks = 0;
					InvUtils.move().fromArmor(2).to(slot);
				}
			}
		}
	}
}
