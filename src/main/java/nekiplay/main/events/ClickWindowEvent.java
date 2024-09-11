package nekiplay.main.events;

import net.minecraft.screen.slot.SlotActionType;

public class ClickWindowEvent extends Cancellable {
	private static final ClickWindowEvent INSTANCE = new ClickWindowEvent();

	public int windowId;
	public int slotId;
	public int mouseButtonClicked;
	public SlotActionType mode;

	public static ClickWindowEvent get(int windowId, int slotId, int mouseButtonClicked, SlotActionType mode) {
		INSTANCE.setCancelled(false);
		INSTANCE.windowId = windowId;
		INSTANCE.mouseButtonClicked = mouseButtonClicked;;
		INSTANCE.slotId = slotId;
		INSTANCE.mode = mode;
		return INSTANCE;
	}
}
