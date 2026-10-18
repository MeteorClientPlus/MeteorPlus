package nekiplay.meteorplus.utils.xraybruteforce;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class XChunk {

	public Long2ObjectMap<XBlock> blocks;

	public XBlock get(int x, int y, int z) {
		return blocks == null ? null : blocks.get(XBlock.getKey(x, y, z));
	}
}
