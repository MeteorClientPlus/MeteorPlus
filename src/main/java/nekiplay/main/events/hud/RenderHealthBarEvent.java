package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class RenderHealthBarEvent extends Cancellable {
	private static final RenderHealthBarEvent INSTANCE = new RenderHealthBarEvent();
	private GuiGraphicsExtractor context;
	private Player player;
	private int x;
	private int y;
	private int lines;
	private int regeneratingHeartIndex;
	private float maxHealth;
	private int lastHealth;
	private int health;
	private int absorption;
	private boolean blinking;

	public static RenderHealthBarEvent get(GuiGraphicsExtractor context, Player player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
		INSTANCE.context = context;
		INSTANCE.player = player;
		INSTANCE.x = x;
		INSTANCE.y = y;
		INSTANCE.lines = lines;
		INSTANCE.regeneratingHeartIndex = regeneratingHeartIndex;
		INSTANCE.maxHealth = maxHealth;
		INSTANCE.lastHealth = lastHealth;
		INSTANCE.health = health;
		INSTANCE.absorption = absorption;
		INSTANCE.blinking = blinking;
		return INSTANCE;
	}
}
