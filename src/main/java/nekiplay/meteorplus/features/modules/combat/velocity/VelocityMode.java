package nekiplay.meteorplus.features.modules.combat.velocity;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;

public class VelocityMode {
	protected final MinecraftClient mc;
	protected final VelocityPlus settings;
	private final VelocityModes type;

	public VelocityMode(VelocityModes type) {
		this.settings = Modules.get().get(VelocityPlus.class);;
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}
	public void onReceivePacket(PacketEvent.Receive event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
