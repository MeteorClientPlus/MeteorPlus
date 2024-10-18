package nekiplay.meteorplus.features.modules.world.customblocks;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class PosData {
	public double x;
	public double y;
	public double z;


	public PosData(BlockPos pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		PosData otherPos = (PosData) obj;
		if (x == otherPos.x && y == otherPos.y && z == otherPos.z) {
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}
