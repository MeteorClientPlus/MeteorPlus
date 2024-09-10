package nekiplay.bozeplus.features.modules.movement.nofall;

import dev.boze.api.addon.module.ToggleableModule;
import dev.boze.api.event.EventTick;
import dev.boze.api.setting.SettingMode;
import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.packets.PacketEvent;
import nekiplay.bozeplus.features.modules.movement.nofall.modes.MatrixNew;

import java.util.ArrayList;

public class NoFallPlus extends ToggleableModule {
	private final SettingMode mode = new SettingMode("Mode", "Bypass mode", new ArrayList<>() {{
		add("Matrix");
	}});

	public NoFallPlus() {
		super("No Fall+", "Bypass no fall");
		elements.add(mode);
		onNoFallModeChanged(mode.getMode());
	}

	@Override
	protected void onEnable() {
		onNoFallModeChanged(mode.getMode());
		if (currentMode != null) {
			currentMode.onActivate();
		}
	}

	@EventHandler
	public void onSendPacket(PacketEvent.Send event) {
		if (currentMode != null) {
			currentMode.onSendPacket(event);
		}
	}

	@EventHandler
	public void onSentPacket(PacketEvent.Sent event) {
		if (currentMode != null) {
			currentMode.onSentPacket(event);
		}
	}

	@EventHandler
	public void onTickEventPre(EventTick.Pre event) {
		if (currentMode != null) {
			currentMode.onTickEventPre(event);
		}
	}

	@EventHandler
	public void onTickEventPost(EventTick.Post event) {
		if (currentMode != null) {
			currentMode.onTickEventPost(event);
		}
	}
	private NoFallMode currentMode = null;
	private void onNoFallModeChanged(int mode) {
		switch (mode) {
			case 0 -> currentMode = new MatrixNew(this);
		}
	}
}
