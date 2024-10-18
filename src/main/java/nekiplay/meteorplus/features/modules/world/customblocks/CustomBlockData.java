package nekiplay.meteorplus.features.modules.world.customblocks;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockData {
	public int block_id;
	public List<PosData> positions = new ArrayList<PosData>();
	public String world;
	public String dimension;
	public CustomBlockData(BlockPos pos,int block_id) {
		this.positions.add(new PosData(pos));
		this.block_id = block_id;
	}
}
