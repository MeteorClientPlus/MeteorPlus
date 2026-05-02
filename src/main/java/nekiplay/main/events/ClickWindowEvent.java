package nekiplay.main.events;

import net.minecraft.world.inventory.ContainerInput;

public class ClickWindowEvent extends Cancellable {
	private static final ClickWindowEvent INSTANCE = new ClickWindowEvent();

	public int windowId;
	public int slotId;
	public int mouseButtonClicked;
	public ContainerInput mode;

	public static ClickWindowEvent get(int windowId, int slotId, int mouseButtonClicked, ContainerInput mode) {
		INSTANCE.setCancelled(false);
		INSTANCE.windowId = windowId;
		INSTANCE.mouseButtonClicked = mouseButtonClicked;
		;
		INSTANCE.slotId = slotId;
		INSTANCE.mode = mode;
		return INSTANCE;
	}
}
