package nekiplay.meteorplus.utils;

import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockHelper {

	public static boolean isVecComplete(ArrayList<Vec3d> vlist) {
		BlockPos ppos = mc.player.getBlockPos();
		for (Vec3d b : vlist) {
			BlockPos bb = ppos.add((int) b.getX(), (int) b.getY(), (int) b.getZ());
			if (getBlock(bb) == Blocks.AIR) return false;
		}
		return true;
	}

	public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
		ArrayList<BlockPos> blocks = new ArrayList<>();
		for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
			for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
				for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
					BlockPos pos = new BlockPos(i, j, k);
					if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
				}
			}
		}
		return blocks;
	}


	public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
		double d = pos1.getX() - pos2.getX();
		double e = pos1.getY() - pos2.getY();
		double f = pos1.getZ() - pos2.getZ();
		return MathHelper.sqrt((float) (d * d + e * e + f * f));
	}


	public static BlockPos getBlockPosFromDirection(Direction direction, BlockPos orginalPos) {
		return switch (direction) {
			case UP -> orginalPos.up();
			case DOWN -> orginalPos.down();
			case EAST -> orginalPos.east();
			case WEST -> orginalPos.west();
			case NORTH -> orginalPos.north();
			case SOUTH -> orginalPos.south();
		};
	}


	public static Block getBlock(BlockPos p) {
		if (p == null) return null;
		if (mc.world == null) return null;
		return mc.world.getBlockState(p).getBlock();
	}

	public static boolean isOurSurroundBlock(BlockPos bp) {
		BlockPos ppos = mc.player.getBlockPos();
		for (Direction direction : Direction.values()) {
			if (direction == Direction.UP || direction == Direction.DOWN) continue;
			BlockPos pos = ppos.offset(direction);
			if (pos.equals(bp)) return true;
		}
		return false;
	}

	public static boolean outOfRange(BlockPos cityBlock) {
		return MathHelper.sqrt((float) mc.player.squaredDistanceTo(cityBlock.getX(), cityBlock.getY(), cityBlock.getZ())) > mc.interactionManager.getReachDistance();
	}

	public static BlockPos opposite(BlockPos pos, Dimension dimension)
	{
		int x = pos.getX();
		int z = pos.getZ();

		if (dimension == Dimension.Overworld)
		{
			x /= 8;
			z /= 8;
		}
		else if (dimension == Dimension.Nether) {
			x *= 8;
			z *= 8;
		}
		return new BlockPos(x, pos.getY(), z);
	}
}
