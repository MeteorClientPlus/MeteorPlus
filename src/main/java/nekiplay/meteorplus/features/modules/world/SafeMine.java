package nekiplay.meteorplus.features.modules.world;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.Iterator;

public class SafeMine extends Module {
	public SafeMine() {
		super(Categories.World, "Safe Mine", "Save you from lava.");
	}

	private final SettingGroup ALSettings = settings.getDefaultGroup();
	private final SettingGroup FSettings = settings.createGroup("Freeze Settings");

	public final Setting<Boolean> solidLava = ALSettings.add(new BoolSetting.Builder()
		.name("Solid lava")
		.description("Solid lava.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> solidLavaFreeze = ALSettings.add(new BoolSetting.Builder()
		.name("Solid lava freeze")
		.description("Solid lava.")
		.defaultValue(true)
		.visible(solidLava::get)
		.build()
	);

	public final Setting<Boolean> antiMine = ALSettings.add(new BoolSetting.Builder()
		.name("Anti lava mine")
		.description("Block mine block is nearbly lava.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> replaceLava = ALSettings.add(new BoolSetting.Builder()
		.name("Replace lava")
		.description("Place blocks in lava in offhand.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Integer> delay = ALSettings.add(new IntSetting.Builder()
		.name("Repalce delay")
		.description("Delay for replace lava.")
		.defaultValue(0)
		.min(0)
		.visible(replaceLava::get)
		.sliderRange(0, 20)
		.build()
	);


	ArrayList<BlockPos> lava = new ArrayList<>();

	private Integer tick = 0;

	@EventHandler
	private void onTickEvent(TickEvent.Post event) {
		if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			if (replaceLava.get()) {
				synchronized (lava) {
					Iterator<BlockPos> iterator = lava.iterator();
					if (iterator.hasNext())
					{
						if (tick == 0)
						{
							if (mc.player != null) {
								BlockPos block = iterator.next();
								BlockUtils.place(block, Hand.OFF_HAND, mc.player.getInventory().selectedSlot, false, 0, false, false, false);
								iterator.remove();
								tick = delay.get();
							}
						}
						else
						{
							tick--;
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onCanContactLava(TickEvent.Post event)
	{
		if (mc.player != null && mc.world != null) {
			Vec3d underpos = mc.player.getPos().add(0, -1, 0);
			BlockPos under = new BlockPos((int) underpos.x, (int) underpos.y, (int) underpos.z);
			if (mc.world.getBlockState(under).isOf(Blocks.LAVA)) {
				if (solidLavaFreeze.get() && mc.player.isOnGround()) {
					if (!freeze) {
						freeze = true;
						yaw = mc.player.getYaw();
						pitch = mc.player.getPitch();
						position = mc.player.getPos();
					}
				}
			}
			else {
				freeze = false;
			}
		}
	}

	@EventHandler
	private void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
		if ((event.fluidState.getFluid() == Fluids.LAVA || event.fluidState.getFluid() == Fluids.FLOWING_LAVA) && solidLava.get()) {
			event.walkOnFluid = true;
			if (solidLavaFreeze.get()) {
				freeze = true;
			}
		}
	}

	@EventHandler
	private void onFluidCollisionShape(CollisionShapeEvent event) {
		if (!event.state.getFluidState().isEmpty()) {
			if (mc.player != null && event.state != null && event.state.isOf(Blocks.LAVA )&& !mc.player.isInLava() && solidLava.get()) {
				event.shape = VoxelShapes.fullCube();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onStartBreakingBlock(StartBreakingBlockEvent event) {
		ArrayList<BlockPos> lavaBlocks = isExposedLava(event.blockPos);
		if (lavaBlocks.size() > 0 && antiMine.get()) {
			mc.options.attackKey.setPressed(false);
			event.setCancelled(true);
			synchronized (lava) {
				lava = isExposedLava(event.blockPos);
			}
		}
	}

	private ArrayList<BlockPos> isExposedLava(BlockPos pos)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		if (mc.world != null) {
			if (mc.world.getBlockState(pos).isOf(Blocks.LAVA)) {
				blocks.add(pos);
			}
			if (mc.world.getBlockState(pos.add(1, 0, 0)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(1, 0, 0));
			}
			if (mc.world.getBlockState(pos.add(-1, 0, 0)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(-1, 0, 0));
			}
			if (mc.world.getBlockState(pos.add(0, 1, 0)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(0,1, 0));
			}
			if (mc.world.getBlockState(pos.add(0, -1, 0)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(0,-1, 0));
			}
			if (mc.world.getBlockState(pos.add(0, 0, 1)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(0,0, 1));
			}
			if (mc.world.getBlockState(pos.add(0, 0, -1)).isOf(Blocks.LAVA)) {
				blocks.add(pos.add(0,0, -1));
			}
		}
		return blocks;
	}

	private boolean freeze = false;

	private final Setting<Boolean> FreezeLook = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look")
		.description("Freezes your pitch and yaw.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> Packet = FSettings.add(new BoolSetting.Builder()
		.name("Packet mode")
		.description("Enable packet mode, better.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> FreezeLookSilent = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look silent")
		.description("Freezes your pitch and yaw silent.")
		.defaultValue(true)
		.visible(Packet::get)
		.build()
	);

	private final Setting<Boolean> FreezeLookPlace = FSettings.add(new BoolSetting.Builder()
		.name("Freeze look place support")
		.description("Unfreez you yaw and pitch on place")
		.defaultValue(false)
		.visible(FreezeLookSilent::get)
		.build()
	);

	private float yaw = 0;
	private float pitch = 0;
	private Vec3d position = Vec3d.ZERO;

	@Override()
	public void onActivate() {
		if (mc.player != null){
			yaw = mc.player.getYaw();
			pitch = mc.player.getPitch();
			position = mc.player.getPos();
		}
	}

	private boolean rotate = false;

	private void setFreezeLook(PacketEvent.Send event, PlayerMoveC2SPacket playerMove)
	{
		if (playerMove.changesLook() && FreezeLook.get() && FreezeLookSilent.get() && !rotate) {
			event.setCancelled(true);
		}
		else if (mc.player != null && playerMove.changesLook() && FreezeLook.get() && !FreezeLookSilent.get()) {
			event.setCancelled(true);
			mc.player.setYaw(yaw);
			mc.player.setPitch(pitch);
		}
		if (mc.player != null && playerMove.changesPosition()) {
			mc.player.setVelocity(0, 0, 0);
			mc.player.setPos(position.x, position.y, position.z);
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void InteractBlockEvent(InteractBlockEvent event)
	{
		if (mc.player != null && mc.getNetworkHandler() != null && FreezeLookPlace.get() && freeze) {
			PlayerMoveC2SPacket.LookAndOnGround r = new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround());
			rotate = true;
			mc.getNetworkHandler().sendPacket(r);
			rotate = false;
		}
	}

	@EventHandler
	private void onMovePacket(PacketEvent.Send event) {
		if (freeze) {
			if (event.packet instanceof PlayerMoveC2SPacket playerMove) {
				if (Packet.get()) {
					setFreezeLook(event, playerMove);
				}
			}
		}
	}
	@EventHandler
	private void onMovePacket2(PacketEvent.Send event) {
		if (freeze) {
			if (event.packet instanceof PlayerMoveC2SPacket playerMove) {
				if (Packet.get()) {
					setFreezeLook(event, playerMove);
				}
			}
		}
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (freeze) {
			if (mc.player != null) {
				mc.player.setVelocity(0, 0, 0);
				mc.player.setPos(position.x, position.y, position.z);
			}
		}
	}
}
