package nekiplay.main.events.hud;
import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class RenderMountHealthBarEvent extends Cancellable {
	private static final RenderMountHealthBarEvent INSTANCE = new RenderMountHealthBarEvent();
	private GuiGraphics context;
	public static RenderMountHealthBarEvent get(GuiGraphics context) {
		INSTANCE.context = context;
		return INSTANCE;
	}
}
