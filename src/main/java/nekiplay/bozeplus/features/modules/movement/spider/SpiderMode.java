package nekiplay.bozeplus.features.modules.movement.spider;
import dev.boze.api.event.EventTick;
import nekiplay.main.events.packets.PacketEvent;
import net.minecraft.client.MinecraftClient;

public class SpiderMode {
	protected final MinecraftClient mc;
	protected final SpiderPlus settings;
	private final SpiderModes type;

	public SpiderMode(SpiderModes type, SpiderPlus settings) {
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
