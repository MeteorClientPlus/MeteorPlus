package nekiplay.meteorplus.features.modules.world.autoobsidianmine;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.MixinPlugin;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.modes.Cauldrons;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.modes.Portals;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

public class AutoObsidianFarm extends Module {
	public AutoObsidianFarm() {
		super(Categories.World, "Auto-obsidian-farm", "Automatically farm obsidian in AFK.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<AutoObsidianFarmModes> workingMode = sgGeneral.add(new EnumSetting.Builder<AutoObsidianFarmModes>()
		.name("mode")
		.description("Working mode.")
		.defaultValue(AutoObsidianFarmModes.Portals_Vanila)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	public final Setting<BlockPos> mainPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 1")
		.description("the position of the portal to hell")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<BlockPos> twoPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 2")
		.description("portal position in hell for new portal generations")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
		.name("command")
		.description("Send command.")
		.defaultValue("/home")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portal_Homes)
		.build()
	);

	public final Setting<BlockPos> lavaPlaceLocation = sgGeneral.add(new BlockPosSetting.Builder()
		.name("lava-place-location")
		.description("the position placing lava")
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Integer> delayCommand = sgGeneral.add(new IntSetting.Builder()
		.name("command-delay")
		.description("Ticks delay.")
		.defaultValue(700)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portal_Homes)
		.build()
	);

	public final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
		.name("mining-delay")
		.description("Mining delay.")
		.defaultValue(3)
		.build()
	);

	public final Setting<Integer> collectDelay = sgGeneral.add(new IntSetting.Builder()
		.name("collect-delay")
		.description("Cauldron collecting lava delay.")
		.defaultValue(8)
		.build()
	);

	public final Setting<Integer> lavaPlaceDelay = sgGeneral.add(new IntSetting.Builder()
		.name("lava-place-delay")
		.description("Delay for placing lava.")
		.defaultValue(8)
		.build()
	);

	public final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
		.name("range")
		.description("Cauldron range's.")
		.defaultValue(5)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> solidCauldrons = sgGeneral.add(new BoolSetting.Builder()
		.name("solid-cauldrons")
		.description("Solid cauldrons.")
		.defaultValue(false)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> bypassSneak = sgGeneral.add(new BoolSetting.Builder()
		.name("bypass-sneak")
		.description("Bypass sneak interact.")
		.defaultValue(false)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> tpsCheck = sgGeneral.add(new BoolSetting.Builder()
		.name("no-work-if-server-lag")
		.description("Stop working if server lagging.")
		.defaultValue(false)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> noBaritoneBreaking = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-breaking-if-not-mine-portal")
		.description("No break blocks if is not mining portal.")
		.defaultValue(true)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<Boolean> noBaritonePlacing = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-place")
		.description("No place blocks.")
		.defaultValue(true)
		.visible(() -> workingMode.get() == AutoObsidianFarmModes.Portals_Vanila)
		.build()
	);

	public final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
		.name("rotate")
		.description("Rotate to breaking block.")
		.defaultValue(false)
		.visible(() -> workingMode.get() != AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> swingHand = sgGeneral.add(new BoolSetting.Builder()
		.name("swing-hand")
		.description("Swing hand client side.")
		.defaultValue(true)
		.visible(() -> workingMode.get() != AutoObsidianFarmModes.Cauldrons)
		.build()
	);

	public final Setting<Boolean> pauseOnEat = sgGeneral.add(new BoolSetting.Builder()
		.name("pause-on-eat")
		.description("Pause farming on eat or auto eat.")
		.defaultValue(true)
		.build()
	);

	private AutoObsidianFarmMode currentMode;

	private void onModeChanged(AutoObsidianFarmModes mode) {
		switch (mode) {
			case Portal_Homes, Portals_Vanila -> {
				if (MixinPlugin.isBaritonePresent) {
					currentMode = new Portals();
				}
				else {
					error("This mode need Baritone API (Fabric)");
				}
			}
			case Cauldrons ->  {
				currentMode = new Cauldrons();
			}
		}
	}

	@Override
	public String getInfoString() {
		if (currentMode == null) {
			return "";
		}
		return currentMode.getInfoString();
	}

	@Override
	public void onActivate() {
		if (currentMode != null) {
			currentMode.onActivate();
		}
	}

	@Override
	public void onDeactivate() {
		if (currentMode != null) {
			currentMode.onDeactivate();
		}
	}
	@EventHandler
	private void onPreTickPost(TickEvent.Post event) {
		if (currentMode != null) {
			currentMode.onTickEventPost(event);
		}
	}
	@EventHandler
	private void onPreTick(TickEvent.Pre event) {
		if (currentMode != null) {
			currentMode.onTickEventPre(event);
		}
	}

	@EventHandler
	private void onCollisionShape(CollisionShapeEvent event) {
		if (currentMode != null) {
			currentMode.onCollisionShape(event);
		}
	}

	@EventHandler
	private void onMovePacket(PacketEvent.Send event) {
		if (event.packet instanceof PlayerMoveC2SPacket playerMove) {
			if (currentMode != null) {
				currentMode.onMovePacket(playerMove);
			}
		}
	}
}
