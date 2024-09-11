package nekiplay.meteorplus.features.modules.movement.elytrafly;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class ElytraFlyMode {
	protected final MinecraftClient mc;
	protected final ElytraFlyPlus elytraFly;
	private final ElytraFlyModes type;

	protected boolean lastJumpPressed;
	protected boolean incrementJumpTimer;
	protected boolean lastForwardPressed;
	protected int jumpTimer;
	protected double velX, velY, velZ;
	protected double ticksLeft;
	protected Vec3d forward, right;
	protected double acceleration;

	public ElytraFlyMode(ElytraFlyModes type) {
		this.elytraFly = Modules.get().get(ElytraFlyPlus.class);
		this.mc = MinecraftClient.getInstance();
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

	public void onPlayerMove() {
	}

	public void onActivate() {
		lastJumpPressed = false;
		jumpTimer = 0;
		ticksLeft = 0;
		acceleration = 0;
	}

	public void onDeactivate() {
	}

	public void autoTakeoff() {

	}

	public void handleHorizontalSpeed(PlayerMoveEvent event) {
		boolean a = false;
		boolean b = false;

		if (mc.options.forwardKey.isPressed()) {
			velX += forward.x * getSpeed() * 10;
			velZ += forward.z * getSpeed() * 10;
			a = true;
		} else if (mc.options.backKey.isPressed()) {
			velX -= forward.x * getSpeed() * 10;
			velZ -= forward.z * getSpeed() * 10;
			a = true;
		}

		if (mc.options.rightKey.isPressed()) {
			velX += right.x * getSpeed() * 10;
			velZ += right.z * getSpeed() * 10;
			b = true;
		} else if (mc.options.leftKey.isPressed()) {
			velX -= right.x * getSpeed() * 10;
			velZ -= right.z * getSpeed() * 10;
			b = true;
		}

		if (a && b) {
			double diagonal = 1 / Math.sqrt(2);
			velX *= diagonal;
			velZ *= diagonal;
		}
	}

	public void handleVerticalSpeed(PlayerMoveEvent event) {
		if (mc.options.jumpKey.isPressed()) velY += 0.5 * 0;
		else if (mc.options.sneakKey.isPressed()) velY -= 0.5 * 0;
	}

	protected double getSpeed() {
		return 2.5;
	}

	public String getHudString() {
		return type.name();
	}
}
