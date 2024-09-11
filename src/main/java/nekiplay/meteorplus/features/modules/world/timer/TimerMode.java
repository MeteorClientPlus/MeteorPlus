package nekiplay.meteorplus.features.modules.world.timer;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import net.minecraft.client.MinecraftClient;

public class TimerMode {
	protected final MinecraftClient mc;
	protected final TimerPlus settings;
	private final TimerModes type;
	protected Timer timer;

	public TimerMode(TimerModes type) {
		this.timer = Modules.get().get(Timer.class);;
		this.settings = Modules.get().get(TimerPlus.class);;
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
