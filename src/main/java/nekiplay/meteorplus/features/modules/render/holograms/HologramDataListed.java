package nekiplay.meteorplus.features.modules.render.holograms;

import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class HologramDataListed {
	public double x;
	public double y;
	public double z;
	public String text;
	public String world;
	public String dimension;
	public Color color;
	public double max_render_distance = 16;
	public int item_id = 0;
	public double scale = 1;
	public int item_scale = 2;
	public boolean distanceScaling = false;

	public ArrayList<HologramData> other_holograms = new ArrayList<HologramData>();

	public HologramDataListed() {

	}
	public HologramDataListed(double x, double y, double z, String text, String world, String dimension, Color color, double max_render_distance) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;

		this.text = text;
		this.world = world;
		this.dimension = dimension;
		this.max_render_distance = max_render_distance;
	}

	public HologramDataListed(BlockPos pos, String text, String world, Dimension dimension, Color color, double max_render_distance) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.color = color;

		this.text = text;
		this.world = world;
		this.dimension = dimension.name();
		this.max_render_distance = max_render_distance;
	}
}
