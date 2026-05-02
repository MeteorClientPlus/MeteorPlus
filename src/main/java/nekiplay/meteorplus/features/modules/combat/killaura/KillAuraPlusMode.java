package nekiplay.meteorplus.features.modules.combat.killaura;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;

public class KillAuraPlusMode {
	protected final Minecraft mc;
	protected final KillAuraPlus settings;
	private final KillAuraPlusModes type;

	public KillAuraPlusMode(KillAuraPlusModes type) {
		this.settings = Modules.get().get(KillAuraPlus.class);
		this.mc = Minecraft.getInstance();
		this.type = type;
	}

	public void onActivate() {
	}

	public void onDeactivate() {
	}

	public void onTickPre(TickEvent.Pre event) {
	}

	public void onTickPost(TickEvent.Post event) {
	}

	public void onSendPacket(PacketEvent.Send event) {
	}

	public String getInfoString() {
		return "";
	}
}
