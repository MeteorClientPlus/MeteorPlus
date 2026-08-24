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
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.ArrayList;
import java.util.Iterator;

public class SafeMine extends Module {
	public SafeMine() {
		super(Categories.World, "safe-mine", "Save you from lava.");
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
		.name("Replace delay")
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
		if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
			if (replaceLava.get()) {
				synchronized (lava) {
					Iterator<BlockPos> iterator = lava.iterator();
					if (iterator.hasNext()) {
						if (tick == 0) {
							if (mc.player != null) {
								BlockPos block = iterator.next();
								BlockUtils.place(block, InteractionHand.OFF_HAND, mc.player.getInventory().getSelectedSlot(), false, 0, false, false, false);
								iterator.remove();
								tick = delay.get();
							}
						} else {
							tick--;
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onCanContactLava(TickEvent.Post event) {
		if (mc.player != null && mc.level != null) {
			Vec3 underpos = mc.player.position().add(0, -1, 0);
			BlockPos under = new BlockPos((int) underpos.x, (int) underpos.y, (int) underpos.z);
			if (mc.level.getBlockState(under).is(Blocks.LAVA)) {
				if (solidLavaFreeze.get() && mc.player.onGround()) {
					if (!freeze) {
						freeze = true;
						yaw = mc.player.getYRot();
						pitch = mc.player.getXRot();
						position = mc.player.position();
					}
				}
			} else {
				freeze = false;
			}
		}
	}

	@EventHandler
	private void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
		if ((event.fluidState.getType() == Fluids.LAVA || event.fluidState.getType() == Fluids.FLOWING_LAVA) && solidLava.get()) {
			event.walkOnFluid = true;
			if (solidLavaFreeze.get()) {
				freeze = true;
			}
		}
	}

	@EventHandler
	private void onFluidCollisionShape(CollisionShapeEvent event) {
		if (!event.state.getFluidState().isEmpty()) {
			if (mc.player != null && event.state != null && event.state.is(Blocks.LAVA) && !mc.player.isInLava() && solidLava.get()) {
				event.shape = Shapes.block();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onStartBreakingBlock(StartBreakingBlockEvent event) {
		ArrayList<BlockPos> lavaBlocks = isExposedLava(event.blockPos);
		if (!lavaBlocks.isEmpty() && antiMine.get()) {
			mc.options.keyAttack.setDown(false);
			event.setCancelled(true);
			synchronized (lava) {
				lava = isExposedLava(event.blockPos);
			}
		}
	}

	private ArrayList<BlockPos> isExposedLava(BlockPos pos) {
		ArrayList<BlockPos> blocks = new ArrayList<>();
		if (mc.level != null) {
			if (mc.level.getBlockState(pos).is(Blocks.LAVA)) {
				blocks.add(pos);
			}
			if (mc.level.getBlockState(pos.offset(1, 0, 0)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(1, 0, 0));
			}
			if (mc.level.getBlockState(pos.offset(-1, 0, 0)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(-1, 0, 0));
			}
			if (mc.level.getBlockState(pos.offset(0, 1, 0)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(0, 1, 0));
			}
			if (mc.level.getBlockState(pos.offset(0, -1, 0)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(0, -1, 0));
			}
			if (mc.level.getBlockState(pos.offset(0, 0, 1)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(0, 0, 1));
			}
			if (mc.level.getBlockState(pos.offset(0, 0, -1)).is(Blocks.LAVA)) {
				blocks.add(pos.offset(0, 0, -1));
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
		.description("Unfreeze you yaw and pitch on place")
		.defaultValue(false)
		.visible(FreezeLookSilent::get)
		.build()
	);

	private float yaw = 0;
	private float pitch = 0;
	private Vec3 position = Vec3.ZERO;

	@Override()
	public void onActivate() {
		if (mc.player != null) {
			yaw = mc.player.getYRot();
			pitch = mc.player.getXRot();
			position = mc.player.position();
		}
	}

	private boolean rotate = false;

	private void setFreezeLook(PacketEvent.Send event, ServerboundMovePlayerPacket playerMove) {
		if (playerMove.hasRotation() && FreezeLook.get() && FreezeLookSilent.get() && !rotate) {
			event.setCancelled(true);
		} else if (mc.player != null && playerMove.hasRotation() && FreezeLook.get() && !FreezeLookSilent.get()) {
			event.setCancelled(true);
			mc.player.setYRot(yaw);
			mc.player.setXRot(pitch);
		}
		if (mc.player != null && playerMove.hasPosition()) {
			mc.player.setDeltaMovement(0, 0, 0);
			mc.player.setPosRaw(position.x, position.y, position.z);
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void InteractBlockEvent(InteractBlockEvent event) {
		if (mc.player != null && mc.getConnection() != null && FreezeLookPlace.get() && freeze) {
			ServerboundMovePlayerPacket.Rot r = new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), mc.player.getXRot(), mc.player.onGround(), mc.player.horizontalCollision);
			rotate = true;
			mc.getConnection().send(r);
			rotate = false;
		}
	}

	@EventHandler
	private void onMovePacket(PacketEvent.Send event) {
		if (freeze) {
			if (event.packet instanceof ServerboundMovePlayerPacket playerMove) {
				if (Packet.get()) {
					setFreezeLook(event, playerMove);
				}
			}
		}
	}

	@EventHandler
	private void onMovePacket2(PacketEvent.Send event) {
		if (freeze) {
			if (event.packet instanceof ServerboundMovePlayerPacket playerMove) {
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
				mc.player.setDeltaMovement(0, 0, 0);
				mc.player.setPosRaw(position.x, position.y, position.z);
			}
		}
	}
}
