package nekiplay.meteorplus.features.modules.movement.noslow;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import net.minecraft.client.MinecraftClient;

public class NoSlowMode {
	protected final MinecraftClient mc;
	protected final NoSlowPlus settings;
	private final NoSlowModes type;

	public NoSlowMode(NoSlowModes type) {
		this.settings = Modules.get().get(NoSlowPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}
	public void onUse(PlayerUseMultiplierEvent event) { }
	public void onTickEventPre(TickEvent.Pre event) { }
	public void onActivate() { }
}
