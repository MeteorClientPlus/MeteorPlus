package nekiplay.meteorplus.features.modules.movement.fastladder;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.fastladder.modes.Spartan;

public class FastLadderPlus extends Module {
	public FastLadderPlus() {
		super(Categories.Movement, "fast-ladder+", "Bypass fast-ladder");
		onSpiderModeChanged(spiderMode.get());
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<FastLadderModes> spiderMode = sgGeneral.add(new EnumSetting.Builder<FastLadderModes>()
		.name("mode")
		.description("The method of applying spider.")
		.defaultValue(FastLadderModes.Spartan)
		.onModuleActivated(spiderModesSetting -> onSpiderModeChanged(spiderModesSetting.get()))
		.onChanged(this::onSpiderModeChanged)
		.build()
	);

	private FastLadderMode currentMode;

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onTickEventPre(event);
	}

	@EventHandler
	private void onPostTick(TickEvent.Post event) {
		currentMode.onTickEventPost(event);
	}
	@EventHandler
	public void onSendPacket(PacketEvent.Send event) {
		currentMode.onSendPacket(event);
	}
	@EventHandler
	public void onSentPacket(PacketEvent.Sent event) {
		currentMode.onSentPacket(event);
	}


	private void onSpiderModeChanged(FastLadderModes mode) {
		switch (mode) {
			case Spartan -> currentMode = new Spartan();
		}
	}
}
