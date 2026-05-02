package nekiplay.main.events.hud;
import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;

public class RenderFoodBarEvent extends Cancellable {
	private static final RenderFoodBarEvent INSTANCE = new RenderFoodBarEvent();
	private GuiGraphics context;
	private Player player;
	private int top;
	private int right;
	public static RenderFoodBarEvent get(GuiGraphics context, Player player, int top, int right) {
		INSTANCE.context = context;
		INSTANCE.player = player;
		INSTANCE.top = top;
		INSTANCE.right = right;
		return INSTANCE;
	}
}
