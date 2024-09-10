package nekiplay.meteorplus.features.modules.movement.jesus;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.jesus.modes.MatrixZoom;
import nekiplay.meteorplus.features.modules.movement.jesus.modes.MatrixZoom2;
import nekiplay.meteorplus.features.modules.movement.jesus.modes.NCP;
import nekiplay.meteorplus.features.modules.movement.jesus.modes.VulcanExploit;


public class JesusPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public JesusPlus() {
		super(Categories.Movement, "jesus+", "Bypass-jesus");
		onJesusModeChanged(jesusMode.get());
	}

	public final Setting<JesusModes> jesusMode = sgGeneral.add(new EnumSetting.Builder<JesusModes>()
		.name("mode")
		.description("The method of applying jesus.")
		.defaultValue(JesusModes.Matrix_Zoom)
		.onModuleActivated(spiderModesSetting -> onJesusModeChanged(spiderModesSetting.get()))
		.onChanged(this::onJesusModeChanged)
		.build()
	);

	public final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
		.name("speed")
		.description("Jesus speed.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.build()
	);

	public final Setting<Double> limit_speed = sgGeneral.add(new DoubleSetting.Builder()
		.name("limit-speed")
		.description("Jesus speed.")
		.visible(() -> jesusMode.get() == JesusModes.NCP)
		.build()
	);

	public final Setting<Boolean> autoSwapVulcan = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-swap")
		.description("Auto swap.")
		.defaultValue(true)
		.visible(() -> jesusMode.get() == JesusModes.Vulcan_Exploit)
		.build()
	);

	private JesusMode currentMode;

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
	public void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
		currentMode.onCanWalkOnFluid(event);
	}
	@EventHandler
	public void onCollisionShape(CollisionShapeEvent event) {
		currentMode.onCollisionShape(event);
	}

	@EventHandler
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		currentMode.onPlayerMoveEvent(event);
	}


	private void onJesusModeChanged(JesusModes mode) {
		switch (mode) {
			case Matrix_Zoom -> currentMode = new MatrixZoom();
			case Matrix_Zoom_2 -> currentMode = new MatrixZoom2();
			case Vulcan_Exploit -> currentMode = new VulcanExploit();
			case NCP -> currentMode = new NCP();
		}
	}
}
