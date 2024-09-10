package nekiplay.meteorplus.features.modules.combat.velocity;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.combat.velocity.modes.GrimCancel;
import nekiplay.meteorplus.features.modules.combat.velocity.modes.GrimCancel_v2;
import nekiplay.meteorplus.features.modules.combat.velocity.modes.GrimSkip;

public class VelocityPlus extends Module {
	public VelocityPlus() {
		super(Categories.Movement, "velocity+", "Bypass velocity.");
	}
	private final SettingGroup settingsGroup = settings.getDefaultGroup();

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<VelocityModes> mode = sgGeneral.add(new EnumSetting.Builder<VelocityModes>()
		.name("mode")
		.description("Velocity mode.")
		.defaultValue(VelocityModes.Grim_Cancel)
		.onModuleActivated(timerModesSetting -> onTimerModeChanged(timerModesSetting.get()))
		.onChanged(this::onTimerModeChanged)
		.build()
	);


	private VelocityMode currentMode;

	private void onTimerModeChanged(VelocityModes mode) {
		switch (mode) {
			case Grim_Cancel -> currentMode = new GrimCancel();
			case Grim_Cancel_v2 -> currentMode = new GrimCancel_v2();
			case Grim_Skip -> currentMode = new GrimSkip();
		}
	}

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
	@EventHandler
	private void onRecivePacket(PacketEvent.Receive event) {
		currentMode.onReceivePacket(event);
	}
}
