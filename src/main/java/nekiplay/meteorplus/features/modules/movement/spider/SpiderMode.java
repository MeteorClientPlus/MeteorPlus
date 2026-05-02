package nekiplay.meteorplus.features.modules.movement.spider;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;

public class SpiderMode {
	protected final Minecraft mc;
	protected final SpiderPlus settings;
	private final SpiderModes type;

	public SpiderMode(SpiderModes type) {
		this.settings = Modules.get().get(SpiderPlus.class);
		this.mc = Minecraft.getInstance();
		this.type = type;
	}

	public void onSendPacket(PacketEvent.Send event) {}
	public void onSentPacket(PacketEvent.Sent event) {}

	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}

	public void onActivate() {}
	public void onDeactivate() {}
}
