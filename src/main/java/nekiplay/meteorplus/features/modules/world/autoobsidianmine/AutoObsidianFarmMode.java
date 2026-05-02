package nekiplay.meteorplus.features.modules.world.autoobsidianmine;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class AutoObsidianFarmMode {
	protected final Minecraft mc;
	protected final AutoObsidianFarm settings;
	private final AutoObsidianFarmModes type;

	public AutoObsidianFarmMode(AutoObsidianFarmModes type) {
		this.settings = Modules.get().get(AutoObsidianFarm.class);
		;
		this.mc = Minecraft.getInstance();
		this.type = type;
	}

	public void onActivate() {
	}

	public void onDeactivate() {
	}

	public void onTickEventPre(TickEvent.Pre event) {
	}

	public void onTickEventPost(TickEvent.Post event) {
	}

	public void onCollisionShape(CollisionShapeEvent event) {
	}

	public void onMovePacket(ServerboundMovePlayerPacket playerMove) {
	}

	public String getInfoString() {
		return "";
	}
}
