// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiJavaCodeReferenceElement
package nekiplay.meteorplus.utils;

import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockHelper {

	public static boolean isVecComplete(ArrayList<Vec3> vlist) {
		BlockPos ppos = mc.player.blockPosition();
		for (Vec3 b : vlist) {
			BlockPos bb = ppos.offset((int) b.x(), (int) b.y(), (int) b.z());
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
		return Mth.sqrt((float) (d * d + e * e + f * f));
	}


	public static BlockPos getBlockPosFromDirection(Direction direction, BlockPos orginalPos) {
		return switch (direction) {
			case UP -> orginalPos.above();
			case DOWN -> orginalPos.below();
			case EAST -> orginalPos.east();
			case WEST -> orginalPos.west();
			case NORTH -> orginalPos.north();
			case SOUTH -> orginalPos.south();
		};
	}


	public static Block getBlock(BlockPos p) {
		if (p == null) return null;
		if (mc.level == null) return null;
		return mc.level.getBlockState(p).getBlock();
	}

	public static boolean isOurSurroundBlock(BlockPos bp) {
		BlockPos ppos = mc.player.blockPosition();
		for (Direction direction : Direction.values()) {
			if (direction == Direction.UP || direction == Direction.DOWN) continue;
			BlockPos pos = ppos.offset(direction);
			if (pos.equals(bp)) return true;
		}
		return false;
	}

	public static boolean outOfRange(BlockPos cityBlock) {
		return MathHelper.sqrt((float) mc.player.squaredDistanceTo(cityBlock.getX(), cityBlock.getY(), cityBlock.getZ())) > 4;
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
