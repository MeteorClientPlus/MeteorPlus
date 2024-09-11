package nekiplay.meteorplus.features.modules.movement;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.util.math.MathHelper;
import nekiplay.main.events.ClickWindowEvent;

import static org.lwjgl.glfw.GLFW.*;

public class InventoryMovePlus extends Module {
	public InventoryMovePlus() {
		super(Categories.Movement, "gui-move+", "Move in inventories.");
	}
	public enum Screens {
		GUI,
		Inventory,
		Both
	}
	public enum Bypass {
		No_Open_Packet,
		None;

		@Override
		public String toString() {
			return super.toString().replace('_', ' ');
		}
	}
	public enum NoSprint {
		Real,
		Packet_Spoof,
		None;

		@Override
		public String toString() {
			return super.toString().replace('_', ' ');
		}
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Bypass> bypassSetting = sgGeneral.add(new EnumSetting.Builder<Bypass>()
		.name("Bypass")
		.description("Bypass mode.")
		.defaultValue(Bypass.None)
		.build()
	);

	private final Setting<NoSprint> noSprintSetting = sgGeneral.add(new EnumSetting.Builder<NoSprint>()
		.name("NoSprint")
		.description("NoSprint Bypass mode.")
		.defaultValue(NoSprint.None)
		.build()
	);

	private final Setting<Boolean> noMoveClicks = sgGeneral.add(new BoolSetting.Builder()
		.name("NoMoveClicks")
		.description("Block clicks in move.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Screens> screens = sgGeneral.add(new EnumSetting.Builder<Screens>()
		.name("gUIs")
		.description("Which GUIs to move in.")
		.defaultValue(Screens.Inventory)
		.build()
	);

	private final Setting<Boolean> jump = sgGeneral.add(new BoolSetting.Builder()
		.name("jump")
		.description("Allows you to jump while in GUIs.")
		.defaultValue(true)
		.onChanged(aBoolean -> {
			if (isActive() && !aBoolean) set(mc.options.jumpKey, false);
		})
		.build()
	);

	private final Setting<Boolean> sneak = sgGeneral.add(new BoolSetting.Builder()
		.name("sneak")
		.description("Allows you to sneak while in GUIs.")
		.defaultValue(true)
		.onChanged(aBoolean -> {
			if (isActive() && !aBoolean) set(mc.options.sneakKey, false);
		})
		.build()
	);

	private final Setting<Boolean> arrowsRotate = sgGeneral.add(new BoolSetting.Builder()
		.name("arrows-rotate")
		.description("Allows you to use your arrow keys to rotate while in GUIs.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Double> rotateSpeed = sgGeneral.add(new DoubleSetting.Builder()
		.name("rotate-speed")
		.description("Rotation speed while in GUIs.")
		.defaultValue(4)
		.min(0)
		.build()
	);
	@Override
	public void onDeactivate() {
		set(mc.options.forwardKey, false);
		set(mc.options.backKey, false);
		set(mc.options.leftKey, false);
		set(mc.options.rightKey, false);
		if (jump.get()) set(mc.options.jumpKey, false);
		if (sneak.get()) set(mc.options.sneakKey, false);
		if (noSprintSetting.get() == NoSprint.None) set(mc.options.sprintKey, false);
	}

	@EventHandler
	private void onClickSlot(ClickWindowEvent event) {
		if (noMoveClicks.get() && PlayerUtils.isMoving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onPacketSend(PacketEvent.Send event) {
		if (event.packet instanceof ClientCommandC2SPacket packet) {
			if (packet.getMode() == ClientCommandC2SPacket.Mode.OPEN_INVENTORY && bypassSetting.get() == Bypass.No_Open_Packet) {
				if (noSprintSetting.get() == NoSprint.Packet_Spoof) {
					if (mc.player.isSprinting())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
					if (mc.player.isSneaking())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
				}
				event.cancel();
			}

			if (event.packet instanceof CloseHandledScreenC2SPacket closeHandledScreenC2SPacket) {
				if (noSprintSetting.get() == NoSprint.Packet_Spoof) {
					if (mc.player.isSprinting())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
					if (mc.player.isSneaking())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
				}
			}
			if (event.packet instanceof CloseHandledScreenC2SPacket closeHandledScreenC2SPacket) {
				if (noSprintSetting.get() == NoSprint.Packet_Spoof) {
					if (mc.player.isSprinting())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
					if (mc.player.isSneaking())
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
				}
			}
		}
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (skip()) return;
		if (screens.get() == Screens.GUI && !(mc.currentScreen instanceof WidgetScreen)) return;
		if (screens.get() == Screens.Inventory && !(mc.currentScreen instanceof InventoryScreen)) return;

		set(mc.options.forwardKey, Input.isPressed(mc.options.forwardKey));
		set(mc.options.backKey, Input.isPressed(mc.options.backKey));
		set(mc.options.leftKey, Input.isPressed(mc.options.leftKey));
		set(mc.options.rightKey, Input.isPressed(mc.options.rightKey));

		if (jump.get()) set(mc.options.jumpKey, Input.isPressed(mc.options.jumpKey));
		if (sneak.get()) set(mc.options.sneakKey, Input.isPressed(mc.options.sneakKey));
		if (noSprintSetting.get() == NoSprint.None) set(mc.options.sprintKey, Input.isPressed(mc.options.sprintKey));

		if (arrowsRotate.get()) {
			float yaw = mc.player.getYaw();
			float pitch = mc.player.getPitch();

			for (int i = 0; i < (rotateSpeed.get() * 2); i++) {
				if (Input.isKeyPressed(GLFW_KEY_LEFT)) yaw -= 0.5;
				if (Input.isKeyPressed(GLFW_KEY_RIGHT)) yaw += 0.5;
				if (Input.isKeyPressed(GLFW_KEY_UP)) pitch -= 0.5;
				if (Input.isKeyPressed(GLFW_KEY_DOWN)) pitch += 0.5;
			}

			pitch = MathHelper.clamp(pitch, -90, 90);

			mc.player.setYaw(yaw);
			mc.player.setPitch(pitch);
		}
	}

	private void set(KeyBinding bind, boolean pressed) {
		boolean wasPressed = bind.isPressed();
		bind.setPressed(pressed);

		InputUtil.Key key = ((KeyBindingAccessor) bind).getKey();
		if (wasPressed != pressed && key.getCategory() == InputUtil.Type.KEYSYM) {
			MeteorClient.EVENT_BUS.post(KeyEvent.get(key.getCode(), 0, pressed ? KeyAction.Press : KeyAction.Release));
		}
	}

	public boolean skip() {
		return mc.currentScreen == null || (mc.currentScreen instanceof CreativeInventoryScreen && CreativeInventoryScreenAccessor.getSelectedTab().getType() == ItemGroup.Type.SEARCH) || mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof SignEditScreen || mc.currentScreen instanceof AnvilScreen || mc.currentScreen instanceof AbstractCommandBlockScreen || mc.currentScreen instanceof StructureBlockScreen;
	}
}
