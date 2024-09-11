package nekiplay.meteorplus.features.modules.movement.elytrafly;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.elytrafly.modes.OldFag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ElytraFlyPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgInventory = settings.createGroup("Inventory");
	private final SettingGroup sgAutopilot = settings.createGroup("Autopilot");

	// General

	public final Setting<ElytraFlyModes> flightMode = sgGeneral.add(new EnumSetting.Builder<ElytraFlyModes>()
		.name("mode")
		.description("The mode of flying.")
		.defaultValue(ElytraFlyModes.OldFag)
		.onModuleActivated(flightModesSetting -> onModeChanged(flightModesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);



	private ElytraFlyMode currentMode = new OldFag();

	public ElytraFlyPlus() {
		super(Categories.Movement, "elytra-fly+", "Gives you more control over your elytra.");
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent event) {
		if (!(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem)) return;

		currentMode.autoTakeoff();

		if (mc.player.isFallFlying()) {

			currentMode.velX = 0;
			currentMode.velY = event.movement.y;
			currentMode.velZ = 0;
			currentMode.forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(0.1);
			currentMode.right = Vec3d.fromPolar(0, mc.player.getYaw() + 90).multiply(0.1);

			// Handle stopInWater
			if (mc.player.isTouchingWater()) {
				mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
				return;
			}

			currentMode.handleHorizontalSpeed(event);
			currentMode.handleVerticalSpeed(event);

			int chunkX = (int) ((mc.player.getX() + currentMode.velX) / 16);
			int chunkZ = (int) ((mc.player.getZ() + currentMode.velZ) / 16);
			currentMode.onPlayerMove();
		} else {
			if (currentMode.lastForwardPressed) {
				mc.options.forwardKey.setPressed(false);
				currentMode.lastForwardPressed = false;
			}
		}

		if (mc.player.isFallFlying()) {
			Vec3d lookAheadPos = mc.player.getPos().add(mc.player.getVelocity().normalize().multiply(2));
			RaycastContext raycastContext = new RaycastContext(mc.player.getPos(), new Vec3d(lookAheadPos.getX(), mc.player.getY(), lookAheadPos.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
			BlockHitResult hitResult = mc.world.raycast(raycastContext);
			if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
				((IVec3d) event.movement).set(0, currentMode.velY, 0);
			}
		}
	}

	public boolean canPacketEfly() {
		return isActive() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem && !mc.player.isOnGround();
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		currentMode.onTick();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onPreTick();
	}

	@EventHandler
	private void onPacketSend(PacketEvent.Send event) {
		currentMode.onPacketSend(event);
	}

	@EventHandler
	private void onPacketReceive(PacketEvent.Receive event) {
		currentMode.onPacketReceive(event);
	}

	private void onModeChanged(ElytraFlyModes mode) {
		switch (mode) {
			case OldFag -> currentMode = new OldFag();
		}
	}

	//Ground
	private class StaticGroundListener {
		@EventHandler
		private void chestSwapGroundListener(PlayerMoveEvent event) {
			if (mc.player != null && mc.player.isOnGround()) {
				if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
					Modules.get().get(ChestSwap.class).swap();
					disableGroundListener();
				}
			}
		}
	}

	private final StaticGroundListener staticGroundListener = new StaticGroundListener();

	protected void enableGroundListener() {
		MeteorClient.EVENT_BUS.subscribe(staticGroundListener);
	}

	protected void disableGroundListener() {
		MeteorClient.EVENT_BUS.unsubscribe(staticGroundListener);
	}

	//Drop
	private class StaticInstaDropListener {
		@EventHandler
		private void onInstadropTick(TickEvent.Post event) {
			if (mc.player != null && mc.player.isFallFlying()) {
				mc.player.setVelocity(0, 0, 0);
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
			} else {
				disableInstaDropListener();
			}
		}
	}

	private final StaticInstaDropListener staticInstadropListener = new StaticInstaDropListener();

	protected void enableInstaDropListener() {
		MeteorClient.EVENT_BUS.subscribe(staticInstadropListener);
	}

	protected void disableInstaDropListener() {
		MeteorClient.EVENT_BUS.unsubscribe(staticInstadropListener);
	}

	@Override
	public String getInfoString() {
		return currentMode.getHudString();
	}

	public enum ChestSwapMode {
		Always,
		Never,
		WaitForGround
	}

	public enum AutoPilotMode {
		Vanilla,
		Pitch40
	}
}
