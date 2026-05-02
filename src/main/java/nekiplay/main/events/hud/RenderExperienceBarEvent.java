package nekiplay.main.events.hud;
import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphics;

public class RenderExperienceBarEvent extends Cancellable {
	private static final RenderExperienceBarEvent INSTANCE = new RenderExperienceBarEvent();
	private GuiGraphics context;
	private int x;
	public static RenderExperienceBarEvent get(GuiGraphics context, int x) {
		INSTANCE.context = context;
		INSTANCE.x = x;
		return INSTANCE;
	}
}
