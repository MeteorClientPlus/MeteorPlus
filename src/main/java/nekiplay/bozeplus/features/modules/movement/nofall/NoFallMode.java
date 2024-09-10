package nekiplay.bozeplus.features.modules.movement.nofall;

import dev.boze.api.event.EventTick;
import nekiplay.main.events.packets.PacketEvent;
import net.minecraft.client.MinecraftClient;

public class NoFallMode {
	protected final MinecraftClient mc;
	protected final NoFallPlus settings;
	private final NoFallModes type;

	public NoFallMode(NoFallModes type, NoFallPlus settings) {
		this.settings = settings;
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}

	public void onTickEventPre(EventTick.Pre event) {}
	public void onTickEventPost(EventTick.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
