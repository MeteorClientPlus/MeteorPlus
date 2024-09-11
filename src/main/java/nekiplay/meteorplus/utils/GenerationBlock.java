package nekiplay.meteorplus.utils;

import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class GenerationBlock {
	public int min_height;
	public int max_height;

	public Dimension dimension;

	public Block block;

	public GenerationBlock(Block block, Dimension dimension, int min_height, int max_height) {
		this.block = block;
		this.min_height = min_height;
		this.max_height = max_height;
		this.dimension = dimension;
	}

	public static GenerationBlock getGenerationBlock(Block block, boolean newGeneration) {
		if (block == Blocks.ANCIENT_DEBRIS) {
			return new GenerationBlock(block, Dimension.Nether, 8, 22);
		}
		else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, -64, 15);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 15);
			}
		}
		else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, -16, 112);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 32);
			}
		}
		else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, -16, 48);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 63	);
			}
		}
		else if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, -64, 64);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 31);
			}
		}
		else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, -64, 8);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 15);
			}
		}
		else if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
			if (newGeneration) {
				return new GenerationBlock(block, Dimension.Overworld, 0, 80);
			}
			else {
				return new GenerationBlock(block, Dimension.Overworld, 1, 114);
			}
		}
		return null;
	}
}
