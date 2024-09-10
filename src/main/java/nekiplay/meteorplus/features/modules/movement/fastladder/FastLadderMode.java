package nekiplay.meteorplus.features.modules.movement.fastladder;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;

public class FastLadderMode {
	protected final MinecraftClient mc;
	protected final FastLadderPlus settings;
	private final FastLadderModes type;

	public FastLadderMode(FastLadderModes type) {
		this.settings = Modules.get().get(FastLadderPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
