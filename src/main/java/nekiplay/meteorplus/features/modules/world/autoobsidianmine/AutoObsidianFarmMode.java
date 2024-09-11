package nekiplay.meteorplus.features.modules.world.autoobsidianmine;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AutoObsidianFarmMode {
	protected final MinecraftClient mc;
	protected final AutoObsidianFarm settings;
	private final AutoObsidianFarmModes type;

	public AutoObsidianFarmMode(AutoObsidianFarmModes type) {
		this.settings = Modules.get().get(AutoObsidianFarm.class);;
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}

	public void onActivate() {}
	public void onDeactivate() {}
	public void onTickEventPre(TickEvent.Pre event) {}
	public void onTickEventPost(TickEvent.Post event) {}
	public void onCollisionShape(CollisionShapeEvent event) {}

	public void onMovePacket(PlayerMoveC2SPacket playerMove) {}

	public String getInfoString() { return ""; }
}
