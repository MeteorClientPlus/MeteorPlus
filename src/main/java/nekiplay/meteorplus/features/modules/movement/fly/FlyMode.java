package nekiplay.meteorplus.features.modules.movement.fly;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;

public class FlyMode {
	protected final MinecraftClient mc;
	protected final FlyPlus settings;
	private final FlyModes type;

	public FlyMode(FlyModes type) {
		this.settings = Modules.get().get(FlyPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}
	public void onRecivePacket(PacketEvent.Receive event) {}
	public void onPlayerMoveEvent(PlayerMoveEvent event) {}
	public void onPlayerMoveSendPre(SendMovementPacketsEvent.Pre event) {}

	public void onCanWalkOnFluid(CanWalkOnFluidEvent event) {}
	public void onCollisionShape(CollisionShapeEvent event) {}
	public void onDamage(DamageEvent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
