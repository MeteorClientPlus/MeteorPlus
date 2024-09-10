package nekiplay.meteorplus.features.modules.integrations;

import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class MyUtils {

	public static boolean place(BlockPos blockPos, Direction direction, boolean airPlace, boolean swingHand, boolean rotate, boolean clientSide, int range) {
		if (mc.player == null) return false;
		if (!canPlace(blockPos)) return false;

		Vec3d hitPos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

		BlockPos neighbour;

		if (direction == null) {
			direction = Direction.UP;
			neighbour = blockPos;
		} else if(airPlace) {
			neighbour = blockPos;
		}else {
			neighbour = blockPos.offset(direction.getOpposite());
			hitPos.add(direction.getOffsetX() * 0.5, direction.getOffsetY() * 0.5, direction.getOffsetZ() * 0.5);
		}


		Direction s = direction;

		if (rotate) {
			BetterBlockPos placeAgainstPos = new BetterBlockPos(neighbour.getX(), neighbour.getY(), neighbour.getZ());
			VoxelShape collisionShape = mc.world.getBlockState(placeAgainstPos).getCollisionShape(mc.world, placeAgainstPos);

			if(collisionShape.isEmpty()) {
				Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos), 50, clientSide,
					() ->
						place(new BlockHitResult(hitPos, s, neighbour, false), swingHand)
				);
				return true;
			}

			Box aabb = collisionShape.getBoundingBox();
			for (Vec3d placementMultiplier : aabbSideMultipliers(direction.getOpposite())) {
				double placeX = placeAgainstPos.x + aabb.minX * placementMultiplier.x + aabb.maxX * (1 - placementMultiplier.x);
				double placeY = placeAgainstPos.y + aabb.minY * placementMultiplier.y + aabb.maxY * (1 - placementMultiplier.y);
				double placeZ = placeAgainstPos.z + aabb.minZ * placementMultiplier.z + aabb.maxZ * (1 - placementMultiplier.z);

				Vec3d testHitPos = new Vec3d(placeX, placeY, placeZ);
				Vec3d playerHead = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());

				Rotation rot = RotationUtils.calcRotationFromVec3d(playerHead, testHitPos, new Rotation(mc.player.getYaw(), mc.player.getPitch()));
				HitResult res = RayTraceUtils.rayTraceTowards(mc.player, rot, range, false);
				BlockHitResult blockHitRes = ((BlockHitResult) res);
				if(
					res == null ||
						!blockHitRes.getBlockPos().equals(placeAgainstPos) ||
						blockHitRes.getSide() != direction
				) continue;

				Rotations.rotate(Rotations.getYaw(testHitPos), Rotations.getPitch(testHitPos), 50, clientSide,
					() ->
						place(new BlockHitResult(testHitPos, s, neighbour, false), swingHand)
				);

				return true;
			}
		} else {
			place(new BlockHitResult(hitPos, s, neighbour, false), swingHand);
		}

		return true;
	}

	private static void place(BlockHitResult blockHitResult, boolean swing) {
		if (mc.player == null || mc.interactionManager == null || mc.getNetworkHandler() == null) return;
		boolean wasSneaking = mc.player.input.sneaking;
		mc.player.input.sneaking = false;

		ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);

		if (result.shouldSwingHand()) {
			if (swing) mc.player.swingHand(Hand.MAIN_HAND);
			else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
		}

		mc.player.input.sneaking = wasSneaking;
	}

	public static boolean isBlockNormalCube(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof ScaffoldingBlock
			|| block instanceof ShulkerBoxBlock
			|| block instanceof PointedDripstoneBlock
			|| block instanceof AmethystClusterBlock) {
			return false;
		}
		try {
			return Block.isShapeFullCube(state.getCollisionShape(null, null)) || state.getBlock() instanceof StairsBlock;
		} catch (Exception ignored) {
			// if we can't get the collision shape, assume it's bad...
		}
		return false;
	}

	public static boolean canPlaceAgainst(BlockState placeAtState, BlockState placeAgainstState, Direction against) {
		// can we look at the center of a side face of this block and likely be able to place?
		// therefore dont include weird things that we technically could place against (like carpet) but practically can't


		return isBlockNormalCube(placeAgainstState) ||
			placeAgainstState.getBlock() == Blocks.GLASS ||
			placeAgainstState.getBlock() instanceof StainedGlassBlock ||
			placeAgainstState.getBlock() instanceof SlabBlock &&
				(
					placeAgainstState.get(SlabBlock.TYPE) != SlabType.BOTTOM &&
						placeAtState.getBlock() == placeAgainstState.getBlock() &&
						against != Direction.DOWN ||
						placeAtState.getBlock() != placeAgainstState.getBlock()
				);
	}

	public static boolean isBlockInLineOfSight(BlockPos placeAt, BlockState placeAtState) {
		Vec3d playerHead = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
		Vec3d placeAtVec = new Vec3d(placeAt.getX(), placeAt.getY(), placeAt.getZ());

		ShapeType type = RaycastContext.ShapeType.COLLIDER;
		FluidHandling fluid = RaycastContext.FluidHandling.NONE;

		RaycastContext context =
			new RaycastContext(playerHead, placeAtVec, type, fluid, mc.player);
		BlockHitResult bhr = mc.world.raycast(context);

		// check line of sight
		return (bhr.getType() == HitResult.Type.MISS);

	}

	public static Direction getVisiblePlaceSide(BlockPos placeAt, BlockState placeAtState, int range, Direction requiredDir) {
		if (mc.world == null) return null;
		for (Direction against : Direction.values()) {
			BetterBlockPos placeAgainstPos = new BetterBlockPos(placeAt.getX(), placeAt.getY(), placeAt.getZ()).relative(against);
			// BlockState placeAgainstState = mc.world.getBlockState(placeAgainstPos);

			if(requiredDir != null && requiredDir != against && requiredDir != Direction.UP)
				continue;

			if(!canPlaceAgainst(
				placeAtState,
				mc.world.getBlockState(placeAgainstPos),
				against
			))
				continue;
			Box aabb = mc.world.getBlockState(placeAgainstPos).getCollisionShape(mc.world, placeAgainstPos).getBoundingBox();

			for (Vec3d placementMultiplier : aabbSideMultipliers(against)) {
				double placeX = placeAgainstPos.x + aabb.minX * placementMultiplier.x + aabb.maxX * (1 - placementMultiplier.x);
				double placeY = placeAgainstPos.y + aabb.minY * placementMultiplier.y + aabb.maxY * (1 - placementMultiplier.y);
				double placeZ = placeAgainstPos.z + aabb.minZ * placementMultiplier.z + aabb.maxZ * (1 - placementMultiplier.z);

				Vec3d hitPos = new Vec3d(placeX, placeY, placeZ);
				Vec3d playerHead = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());

				Rotation rot = RotationUtils.calcRotationFromVec3d(playerHead, hitPos, new Rotation(mc.player.getYaw(), mc.player.getPitch()));
				HitResult res = RayTraceUtils.rayTraceTowards(mc.player, rot, range, false);
				BlockHitResult blockHitRes = ((BlockHitResult) res);

				if(
					res == null
						|| res.getType() != HitResult.Type.BLOCK
						|| !blockHitRes.getBlockPos().equals(placeAgainstPos)
						|| blockHitRes.getSide() != against.getOpposite()
				) continue;


				return against.getOpposite();

			}
		}

		return null;
	}

	public static Direction getPlaceSide(BlockPos blockPos, Direction requiredDir) {
		for (Direction side : Direction.values()) {

			BlockPos neighbor = blockPos.offset(side);
			Direction side2 = side.getOpposite();

			if(requiredDir != null && requiredDir != side2 && requiredDir != Direction.UP)
				continue;

			BlockState state = mc.world.getBlockState(neighbor);

			// Check if neighbour isn't empty
			if (state.isAir() || !BlockUtils.isClickable(state.getBlock())) continue;

			// Check if neighbour is a fluid
			if (!state.getFluidState().isEmpty()) continue;
			ChatUtils.info("" + side2);
			return side2;
		}

		return null;
	}

	public static NbtCompound getNbtFromBlockState (ItemStack itemStack, BlockState state) {
		NbtCompound nbt = itemStack.getOrCreateNbt();
		NbtCompound subNbt = new NbtCompound();
		for (Property<?> property : state.getProperties()) {
			subNbt.putString(property.getName(), state.get(property).toString());
		}
		nbt.put("BlockStateTag", subNbt);

		return nbt;
	}

	private static Vec3d[] aabbSideMultipliers(Direction side) {
		switch (side) {
			case UP -> {
				return new Vec3d[]{new Vec3d(0.5, 1, 0.5), new Vec3d(0.1, 1, 0.5), new Vec3d(0.9, 1, 0.5), new Vec3d(0.5, 1, 0.1), new Vec3d(0.5, 1, 0.9)};
			}
			case DOWN -> {
				return new Vec3d[]{new Vec3d(0.5, 0, 0.5), new Vec3d(0.1, 0, 0.5), new Vec3d(0.9, 0, 0.5), new Vec3d(0.5, 0, 0.1), new Vec3d(0.5, 0, 0.9)};
			}
			case NORTH, SOUTH, EAST, WEST -> {
				double x = side.getOffsetX() == 0 ? 0.5 : (1 + side.getOffsetX()) / 2D;
				double z = side.getOffsetZ() == 0 ? 0.5 : (1 + side.getOffsetZ()) / 2D;
				return new Vec3d[]{new Vec3d(x, 0.25, z), new Vec3d(x, 0.75, z)};
			}
			default -> // null
				throw new IllegalStateException();
		}
	}
}
