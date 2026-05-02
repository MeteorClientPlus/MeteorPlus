package nekiplay.meteorplus.features.modules.movement.elytrafly;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class ElytraFlyMode {
	protected final Minecraft mc;
	protected final ElytraFlyPlus elytraFly;
	private final ElytraFlyModes type;

	protected boolean lastJumpPressed;
	protected boolean incrementJumpTimer;
	protected boolean lastForwardPressed;
	protected int jumpTimer;
	protected double velX, velY, velZ;
	protected double ticksLeft;
	protected Vec3 forward, right;
	protected double acceleration;

	public ElytraFlyMode(ElytraFlyModes type) {
		this.elytraFly = Modules.get().get(ElytraFlyPlus.class);
		this.mc = Minecraft.getInstance();
		this.type = type;
	}

	public void onTick() {

	}

	public void onPreTick() {
	}

	public void onPacketSend(PacketEvent.Send event) {
	}

	public void onPacketReceive(PacketEvent.Receive event) {
	}

	public void onPlayerMove(PlayerMoveEvent event) {
	}

	public void onActivate() {
		lastJumpPressed = false;
		jumpTimer = 0;
		ticksLeft = 0;
		acceleration = 0;
	}

	public void onDeactivate() {
	}




	public String getHudString() {
		return type.name();
	}
}
