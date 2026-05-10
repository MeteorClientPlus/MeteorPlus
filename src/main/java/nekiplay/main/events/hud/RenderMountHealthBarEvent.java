package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderMountHealthBarEvent extends Cancellable {
	private static final RenderMountHealthBarEvent INSTANCE = new RenderMountHealthBarEvent();
	private GuiGraphicsExtractor context;

	public static RenderMountHealthBarEvent get(GuiGraphicsExtractor context) {
		INSTANCE.context = context;
		return INSTANCE;
	}
}
