package nekiplay.main.events.hud;
import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;

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
