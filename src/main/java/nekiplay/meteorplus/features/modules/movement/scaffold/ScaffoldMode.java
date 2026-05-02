package nekiplay.meteorplus.features.modules.movement.scaffold;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import net.minecraft.client.Minecraft;

public class ScaffoldMode {
	protected final Minecraft mc;
	protected final ScaffoldPlus settings;
	private final ScaffoldModes type;

	public ScaffoldMode(ScaffoldModes type) {
		this.settings = Modules.get().get(ScaffoldPlus.class);
		this.mc = Minecraft.getInstance();
		this.type = type;
	}
	public void onUse(PlayerUseMultiplierEvent event) { }
}
