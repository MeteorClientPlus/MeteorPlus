package nekiplay.meteorplus.features.modules.movement.nofall.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ServerboundMovePlayerPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IServerboundMovePlayerPacket;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import nekiplay.meteorplus.features.modules.movement.nofall.NoFallMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
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

			if (mc.player.onGround() && groundcheck) {
				groundcheck = false;
				cliped = false;
				ChatUtils.infoPrefix("No Fall Plus", "Grounded in " + teleports + " teleports");
				mc.player.fallDistance = 0;
				teleports = 0;
			} else if (mc.player.fallDistance > 3) {
				BlockHitResult result = mc.level.clip(new ClipContext(mc.player.position(), mc.player.position().subtract(0, 10, 0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
				if (result != null && result.getType() == HitResult.Type.BLOCK) {
					blocks = result.getBlockPos().offset(0, 1, 0).getY();
					cliped = true;
				} else if (result == null || result.getType() == HitResult.Type.MISS) {
					blocks = (int) mc.player.position().y - 10;
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
		if (!(event.packet instanceof ServerboundMovePlayerPacket)
			|| ((IServerboundMovePlayerPacket) event.packet).meteor$getTag() == 1337) return;
		((ServerboundMovePlayerPacketAccessor) event.packet).meteor$setOnGround(true);
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
					groundcheck = true;
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
					player.setPos(player.getX(), blocks, player.getZ());
					mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(player.getX(), blocks, player.getZ(), true, mc.player.horizontalCollision));
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
