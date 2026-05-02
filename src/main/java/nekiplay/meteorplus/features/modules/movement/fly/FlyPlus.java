package nekiplay.meteorplus.features.modules.movement.fly;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.fly.modes.MatrixExploit;
import nekiplay.meteorplus.features.modules.movement.fly.modes.MatrixExploit2;
import nekiplay.meteorplus.features.modules.movement.fly.modes.VulcanClip;

public class FlyPlus extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();


	public FlyPlus() {
		super(Categories.Movement, "flight+", "Bypass fly");
		onFlyModeChanged(flyMode.get());
	}

	public final Setting<FlyModes> flyMode = sgGeneral.add(new EnumSetting.Builder<FlyModes>()
		.name("mode")
		.description("The method of applying fly.")
		.defaultValue(FlyModes.Matrix_Exploit)
		.onModuleActivated(spiderModesSetting -> onFlyModeChanged(spiderModesSetting.get()))
		.onChanged(this::onFlyModeChanged)
		.build()
	);

	public final Setting<Double> speed_1 = sgGeneral.add(new DoubleSetting.Builder()
		.name("speed-№1")
		.description("Fly speed.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.visible(() -> flyMode.get() == FlyModes.Matrix_Exploit)
		.build()
	);

	public final Setting<Double> speed2 = sgGeneral.add(new DoubleSetting.Builder()
		.name("speed-№2")
		.description("Fly speed.")
		.defaultValue(0.3)
		.max(5)
		.sliderRange(0, 5)
		.visible(() -> flyMode.get() == FlyModes.Matrix_Exploit_2)
		.build()
	);

	public final Setting<Boolean> canClip = sgGeneral.add(new BoolSetting.Builder()
		.name("can-clip")
		.description("Max fly ticks.")
		.visible(() -> flyMode.get() == FlyModes.Vulcan_Clip)
		.build()
	);

	public final Setting<Boolean> showInfo = sgGeneral.add(new BoolSetting.Builder()
		.name("show-info")
		.description("Displays information about whether this mode is running on the server.")
		.visible(() -> flyMode.get() == FlyModes.Vulcan_Clip)
		.build()
	);

	@Override
	public WWidget getWidget(GuiTheme theme) {
		WWidget widget = super.getWidget(theme);
		if (flyMode.get() == FlyModes.Vulcan_Clip) {
			return theme.label("This mode work only on 1.89 servers");
		}
		return widget;
	}

	private FlyMode currentMode;

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
	public void onReceivePacket(PacketEvent.Receive event) {
		currentMode.onReceivePacket(event);
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

	@EventHandler
	private void onPlayerMoveSendPre(SendMovementPacketsEvent.Pre event) {
		currentMode.onPlayerMoveSendPre(event);
	}

	private void onFlyModeChanged(FlyModes mode) {
		switch (mode) {
			case Matrix_Exploit_2 -> currentMode = new MatrixExploit2();
			case Matrix_Exploit -> currentMode = new MatrixExploit();
			case Vulcan_Clip -> {
				if (showInfo.get()) {
					info("Vulcan fly work on 1.8.9 servers");
				}
				currentMode = new VulcanClip();
			}
		}
	}

}
