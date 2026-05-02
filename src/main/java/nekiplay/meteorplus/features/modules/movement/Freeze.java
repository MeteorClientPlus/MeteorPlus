package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

public class Freeze extends Module {
	public Freeze() {
		super(Categories.Movement, "Freeze", "Freezes your position for server.");
	}

	private final SettingGroup FSettings = settings.getDefaultGroup();

	private final Setting<Boolean> FreezeLook = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look")
		.description("Freezes your pitch and yaw.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> Packet = FSettings.add(new BoolSetting.Builder()
		.name("Packet mode")
		.description("Enable packet mode, better.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> FreezeLookSilent = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look silent")
		.description("Freezes your pitch and yaw silent.")
		.defaultValue(true)
		.visible(() -> Packet.get() && FreezeLook.get())
		.build()
	);

	private final Setting<Boolean> FreezeLookPlace = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look place support")
		.description("Unfreez you yaw and pitch on place")
		.defaultValue(false)
		.visible(FreezeLookSilent::get)
		.build()
	);

	private float yaw = 0;
	private float pitch = 0;
	private Vec3 position = Vec3.ZERO;

	@Override()
	public void onActivate() {
		if (mc.player != null) {
			yaw = mc.player.getYRot();
			pitch = mc.player.getXRot();
			position = mc.player.position();
		}
	}

	private boolean rotate = false;

	private void setFreezeLook(PacketEvent.Send event, ServerboundMovePlayerPacket playerMove) {
		if (playerMove.hasRotation() && FreezeLook.get() && FreezeLookSilent.get() && !rotate) {
			event.setCancelled(true);
		} else if (mc.player != null && playerMove.hasRotation() && FreezeLook.get() && !FreezeLookSilent.get()) {
			event.setCancelled(true);
			mc.player.setYRot(yaw);
			mc.player.setXRot(pitch);
		}
		if (mc.player != null && playerMove.hasPosition()) {
			mc.player.setDeltaMovement(0, 0, 0);
			mc.player.setPosRaw(position.x, position.y, position.z);
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void InteractBlockEvent(InteractBlockEvent event) {
		if (mc.player != null && mc.getConnection() != null && FreezeLookPlace.get()) {
			ServerboundMovePlayerPacket.Rot r = new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision);
			rotate = true;
			mc.getConnection().send(r);
			rotate = false;
		}
	}

	@EventHandler
	private void onMovePacket(PacketEvent.Send event) {
		if (event.packet instanceof ServerboundMovePlayerPacket playerMove) {
			if (Packet.get()) {
				setFreezeLook(event, playerMove);
			}
		}
	}

	@EventHandler
	private void connectToServerEvent(GameLeftEvent event) {
		toggle();
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (mc.player != null) {
			mc.player.setDeltaMovement(0, 0, 0);
			mc.player.setPosRaw(position.x, position.y, position.z);
		}
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent event) {
		event.movement = new Vec3(0, 0, 0);
	}

	@EventHandler
	private void remove(EntityRemovedEvent event) {
		if (event.entity == mc.player) {
			if (isActive()) {
				toggle();
			}
		}
	}
}
