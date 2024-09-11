package nekiplay.meteorplus.features.modules.movement.speed;

import meteordevelopment.meteorclient.events.entity.player.JumpVelocityMultiplierEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;

public class SpeedMode {
	protected final MinecraftClient mc;
	protected final SpeedPlus settings;
	private final SpeedModes type;

	public SpeedMode(SpeedModes type) {
		this.settings = Modules.get().get(SpeedPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onReceivePacket(PacketEvent.Receive event) {}
	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}
	public void onPlayerMoveEvent(PlayerMoveEvent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onJump(JumpVelocityMultiplierEvent event) {}

	public void onActivate() {}
	public void onDeactivate() {}

	protected double getDefaultSpeed() {
		double defaultSpeed = 0.2873;
		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
			defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
			int amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
			defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
		}
		return defaultSpeed;
	}
}
