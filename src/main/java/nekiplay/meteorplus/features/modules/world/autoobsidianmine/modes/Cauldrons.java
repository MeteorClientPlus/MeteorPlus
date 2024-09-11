package nekiplay.meteorplus.features.modules.world.autoobsidianmine.modes;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.AutoObsidianFarmMode;
import nekiplay.meteorplus.features.modules.world.autoobsidianmine.AutoObsidianFarmModes;
import nekiplay.meteorplus.utils.RaycastUtils;
import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cauldrons extends AutoObsidianFarmMode {
	public Cauldrons() {
		super(AutoObsidianFarmModes.Cauldrons);
	}

	private final List<BlockPos.Mutable> blocks = new ArrayList<>();
	private boolean firstBlock;
	private int noBlockTimer;
	private final BlockPos.Mutable lastBlockPos = new BlockPos.Mutable();
	private int timer;
	private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);
	@Override
	public void onActivate() {
		firstBlock = true;
		timer = 0;
		noBlockTimer = 0;
		collectTimer = 0;
		lavaPlaceTimer = 0;
		placed = 0;
	}
	private int collectTimer = 0;
	private int lavaPlaceTimer = 0;
	private final Portals.SortMode sortMode = Portals.SortMode.Closest;

	@Override
	public void onCollisionShape(CollisionShapeEvent event) {
		if (!settings.solidCauldrons.get()) {
			return;
		}
		if (event.state.getBlock() == Blocks.CAULDRON || event.state.getBlock() == Blocks.LAVA_CAULDRON || event.state.getBlock() == Blocks.WATER_CAULDRON) {
			event.shape = VoxelShapes.fullCube();
		}
	}
	private int placed = 0;

	@Override
	public void onDeactivate() {
		if (placed > 0) {
			ChatUtils.info("Farmed obsidian: " + (placed/64) + " stacks");
			if (placed >= 64 * 27) {
				ChatUtils.info("Farmed obsidian: " + (placed/64/27) + " chests");
			}
		}
	}
	
	@Override
	public String getInfoString() {
		return Integer.toString(placed);
	}

	@Override
	public void onTickEventPost(TickEvent.Post event) {
		if (mc.player == null || mc.world == null || mc.interactionManager == null) { return; }
		if ((mc.player.isUsingItem() || (Modules.get().get(AutoEat.class).isActive() && Modules.get().get(AutoEat.class).eating)) && settings.pauseOnEat.get()) {
			return;
		}
		if (TickRate.INSTANCE.getTimeSinceLastTick() >= 1.7 && settings.tpsCheck.get()) {
			return;
		}
		BlockPos placing = settings.lavaPlaceLocation.get();
		if (mc.player.squaredDistanceTo(placing.toCenterPos()) >= settings.range.get() + settings.range.get() + 1) {
			return;
		}
		BlockIterator.register(settings.range.get(), settings.range.get(), (blockPos, blockState) -> {
			Block block = blockState.getBlock();
			if (block == Blocks.LAVA_CAULDRON || block == Blocks.OBSIDIAN || block == Blocks.CAULDRON) {
				blocks.add(blockPosPool.get().set(blockPos));
			}
		});

		// Collect lava if found
		BlockIterator.after(() -> {
			if (mc.player == null || mc.player.getInventory() == null) return;
			if (blocks.isEmpty()) {
				// If no block was found for long enough then set firstBlock flag to true to not wait before breaking another again
				if (noBlockTimer++ >= settings.delay.get()) firstBlock = true;
				return;
			} else {
				noBlockTimer = 0;
				double pX = mc.player.getX();
				double pY = mc.player.getY();
				double pZ = mc.player.getZ();
				blocks.sort(Comparator.comparingDouble(value -> Utils.squaredDistance(pX, pY, pZ, value.getX() + 0.5, value.getY() + 0.5, value.getZ() + 0.5) * (sortMode == Portals.SortMode.Closest ? 1 : -1)));
			}

			// Update timer
			if (!firstBlock && !lastBlockPos.equals(blocks.get(0))) {
				timer = settings.delay.get();

				firstBlock = false;
				lastBlockPos.set(blocks.get(0));

				if (timer > 0) return;
			}

			BlockState state = mc.world.getBlockState(placing);

			if (state.getBlock() == Blocks.OBSIDIAN) {
				if (BlockUtils.canBreak(placing)) {
					rotate(placing, null);
					lavaPlaceTimer = 0;
					BlockUtils.breakBlock(placing, true);
				}
			} else {
				FindItemResult bucket = InvUtils.findInHotbar(Items.BUCKET);
				FindItemResult lavaBucket = InvUtils.findInHotbar(Items.LAVA_BUCKET);
				if (lavaBucket.found() && mc.player.getPos().distanceTo(placing.toCenterPos()) <= settings.range.get() + 1) {
					if (state.getBlock() != Blocks.LAVA) {
						if (lavaPlaceTimer >= settings.lavaPlaceDelay.get()) {
							double yaw = Rotations.getYaw(placing);
							double pitch = Rotations.getPitch(placing);
							Vec3d pos = mc.player.getEyePos();
							HitResult result = RaycastUtils.bucketRaycast(pos, (float) pitch, (float) yaw, RaycastContext.FluidHandling.NONE);
							if (result.getType() == HitResult.Type.BLOCK) {
								BlockHitResult blockHitResult = (BlockHitResult) result;
								BlockPos blockPos = blockHitResult.getBlockPos();
								Direction direction = blockHitResult.getSide();
								BlockPos blockPos2 = blockPos.offset(direction);

								if (blockPos2.equals(placing)) {
									Rotations.rotate(yaw, pitch, 10, true, () -> {
										InvUtils.swap(lavaBucket.slot(), true);
										mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
										placed++;
										InvUtils.swapBack();
										lavaPlaceTimer = 0;
									});
								}
							}
						}
						else {
							lavaPlaceTimer++;
						}
					}
				}
				else if (bucket.found()) {
					for (BlockPos block : blocks) {
						BlockState state2 = mc.world.getBlockState(block);
						if (state2.getBlock() == Blocks.LAVA_CAULDRON && mc.player.getPos().distanceTo(block.toCenterPos()) <= settings.range.get() + 1) {
							if (collectTimer >= settings.collectDelay.get()) {
								mc.player.getInventory().selectedSlot = bucket.slot();
								mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bucket.slot()));
								rotate(block, () -> {
									Vec3d hitPos = Vec3d.ofCenter(block);
									Direction side = Direction.DOWN;
									//BlockPos neighbour;
									//if (side == null) {
									//	side = Direction.UP;
									//	neighbour = block;
									//} else {
									//	neighbour = block.offset(side);
									//	hitPos = hitPos.add((double) side.getOffsetX() * 0.5, (double) side.getOffsetY() * 0.5, (double) side.getOffsetZ() * 0.5);
									//}
									BlockHitResult bhr = new BlockHitResult(hitPos, Direction.UP, block, false);
									boolean isSneaking = false;
									if (settings.bypassSneak.get()) {
										mc.player.setSneaking(false);
										isSneaking = true;
									}
									BlockUtils.interact(bhr, Hand.MAIN_HAND, true);
									if (settings.bypassSneak.get() && isSneaking) {
										mc.player.setSneaking(true);
									}
									collectTimer = 0;
								});
							} else {
								collectTimer++;
							}
							break;
						}
					}
					firstBlock = false;
					for (BlockPos.Mutable blockPos : blocks) blockPosPool.free(blockPos);
					blocks.clear();
				}
			}
		});
	}
	private void rotate(BlockPos target, Runnable action) {
		Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), action);
	}
}
