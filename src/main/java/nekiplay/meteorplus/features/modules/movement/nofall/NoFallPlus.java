package nekiplay.meteorplus.features.modules.movement.nofall;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.nofall.modes.*;

public class NoFallPlus extends Module {
	public NoFallPlus() {
		super(Categories.Movement, "no-fall+", "Bypass fall damage or reduce fall damage");
		onModeChanged(mode.get());
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private NoFallMode currentMode;

	public final Setting<NoFallModes> mode = sgGeneral.add(new EnumSetting.Builder<NoFallModes>()
		.name("mode")
		.description("The method of applying nofall.")
		.defaultValue(NoFallModes.Elytra_Clip)
		.onModuleActivated(spiderModesSetting -> onModeChanged(spiderModesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

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


	private void onModeChanged(NoFallModes mode) {
		switch (mode) {
			case No_Ground_Elytra -> currentMode = new No_Ground_Elytra();
			case No_Ground -> currentMode = new No_Ground();
			case Elytra_Fly -> currentMode = new ElytraFly();
			case Elytra_Clip -> currentMode = new Eclip();
			case Matrix_New -> currentMode = new MatrixNew();
			case Verus -> currentMode = new Verus();
			case Vulcan -> currentMode = new Vulcan();
			case Vulcan_2dot7dot7 -> currentMode = new Vulcan277();
		}
	}
}
