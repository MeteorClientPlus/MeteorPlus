package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderExperienceLevelEvent extends Cancellable {
	private static final RenderExperienceLevelEvent INSTANCE = new RenderExperienceLevelEvent();
	private GuiGraphicsExtractor context;
	private DeltaTracker tickCounter;

	public static RenderExperienceLevelEvent get(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
		INSTANCE.context = context;
		INSTANCE.tickCounter = tickCounter;
		return INSTANCE;
	}
}
