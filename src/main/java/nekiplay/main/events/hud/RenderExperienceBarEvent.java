package nekiplay.main.events.hud;
import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderExperienceBarEvent extends Cancellable {
	private static final RenderExperienceBarEvent INSTANCE = new RenderExperienceBarEvent();
	private GuiGraphicsExtractor context;
	private int x;
	public static RenderExperienceBarEvent get(GuiGraphicsExtractor context, int x) {
		INSTANCE.context = context;
		INSTANCE.x = x;
		return INSTANCE;
	}
}
