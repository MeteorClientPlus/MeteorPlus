package nekiplay.meteorplus.utils;

import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.core.BlockPos;

public class BlockHelper {

	public static BlockPos opposite(BlockPos pos, Dimension dimension) {
		int x = pos.getX();
		int z = pos.getZ();

		if (dimension == Dimension.Overworld) {
			x /= 8;
			z /= 8;
		} else if (dimension == Dimension.Nether) {
			x *= 8;
			z *= 8;
		}
		return new BlockPos(x, pos.getY(), z);
	}
}
