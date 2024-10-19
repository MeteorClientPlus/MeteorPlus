package nekiplay.meteorplus.features.modules.movement.elytrafly;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.compiler.Expr;
import nekiplay.meteorplus.features.modules.movement.elytrafly.modes.Control;
import nekiplay.meteorplus.features.modules.movement.elytrafly.modes.Wasp;

public class ElytraFlyPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	// General

	public final Setting<ElytraFlyModes> flightMode = sgGeneral.add(new EnumSetting.Builder<ElytraFlyModes>()
		.name("mode")
		.description("The mode of flying.")
		.defaultValue(ElytraFlyModes.Control)
		.onModuleActivated(flightModesSetting -> onModeChanged(flightModesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);
	//<editor-fold desc="Wasp">
	public final Setting<Double> horizontal_wasp = sgGeneral.add(new DoubleSetting.Builder()
		.name("Horizontal-Speed")
		.description("How many blocks to move each tick horizontally.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Wasp)
		.build()
	);

	public final Setting<Double> fallSpeed_wasp = sgGeneral.add(new DoubleSetting.Builder()
		.name("Fall-Speed")
		.description("How many blocks to fall down each tick.")
		.defaultValue(0.01)
		.min(0)
		.sliderRange(0, 1)
		.visible(() -> flightMode.get() == ElytraFlyModes.Wasp)
		.build()
	);

	public final Setting<Boolean> smartFall_wasp = sgGeneral.add(new BoolSetting.Builder()
		.name("Smart-Fall")
		.description("Only falls down when looking down.")
		.defaultValue(true)
		.visible(() -> flightMode.get() == ElytraFlyModes.Wasp)
		.build()
	);

	public final Setting<Double> up_wasp = sgGeneral.add(new DoubleSetting.Builder()
		.name("Up-Speed")
		.description("How many blocks to move up each tick.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Wasp)
		.build()
	);

	public final Setting<Double> down_wasp = sgGeneral.add(new DoubleSetting.Builder()
		.name("Down-Speed")
		.description("How many blocks to move down each tick.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Wasp)
		.build()
	);

	//</editor-fold>

	//<editor-fold desc="Control">
	public final Setting<Double> speed_control = sgGeneral.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("How many blocks to move each tick.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Control)
		.build()
	);

	public final Setting<Double> upMultiplier_control = sgGeneral.add(new DoubleSetting.Builder()
		.name("Up-Multiplier")
		.description("How many times faster should we fly up.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Control)
		.build()
	);

	public final Setting<Double> downSpeed_control = sgGeneral.add(new DoubleSetting.Builder()
		.name("Down-Speed")
		.description("How many blocks to move down each tick.")
		.defaultValue(1)
		.min(0)
		.sliderRange(0, 5)
		.visible(() -> flightMode.get() == ElytraFlyModes.Control)
		.build()
	);

	public final Setting<Double> fallSpeed_control = sgGeneral.add(new DoubleSetting.Builder()
		.name("Fall-Speed")
		.description("How many blocks to fall down each tick.")
		.defaultValue(0.01)
		.min(0)
		.sliderRange(0, 1)
		.visible(() -> flightMode.get() == ElytraFlyModes.Control)
		.build()
	);
	//</editor-fold>

	public final Setting<Boolean> resetSpeed = sgGeneral.add(new BoolSetting.Builder()
		.name("reset-speed")
		.description("Reset you speed after disable")
		.defaultValue(false)
		.build()
	);

	private ElytraFlyMode currentMode = new Control();

	public ElytraFlyPlus() {
		super(Categories.Movement, "elytra-fly+", "Gives you more control over your elytra.");
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
	private void onPlayerMove(PlayerMoveEvent event) {
		currentMode.onPlayerMove(event);
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		currentMode.onTick();
	}

	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		currentMode.onPreTick();
	}

	@EventHandler
	private void onPacketSend(PacketEvent.Send event) {
		currentMode.onPacketSend(event);
	}

	@EventHandler
	private void onPacketReceive(PacketEvent.Receive event) {
		currentMode.onPacketReceive(event);
	}

	private void onModeChanged(ElytraFlyModes mode) {
		switch (mode) {
			case Control -> currentMode = new Control();
			case Wasp -> currentMode = new Wasp();
		}
	}
}
