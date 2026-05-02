package nekiplay.meteorplus.features.modules.render.holograms;

import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import nekiplay.meteorplus.features.modules.world.customblocks.PosData;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Objects;

public class HologramDataListed {
	public double x;
	public double y;
	public double z;
	public String text;
	public String dimension;
	public Color color;
	public double max_render_distance = 16;
	public double scale = 1;
	public boolean distanceScaling = false;
	public int item_id = 0;
	public int item_scale = 2;

	public ArrayList<HologramData> other_holograms = new ArrayList<HologramData>();

	public HologramDataListed() {

	}
	public HologramDataListed(double x, double y, double z, String text, String dimension, Color color, double max_render_distance) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;

		this.text = text;
		this.dimension = dimension;
		this.max_render_distance = max_render_distance;
	}

	public HologramDataListed(BlockPos pos, String text, Dimension dimension, Color color, double max_render_distance) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.color = color;

		this.text = text;
		this.dimension = dimension.name();
		this.max_render_distance = max_render_distance;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		HologramDataListed otherPos = (HologramDataListed) obj;
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
