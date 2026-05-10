package nekiplay.meteorplus.features.modules.render.holograms;

import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.core.BlockPos;

public class HologramData {
	public double x;
	public double y;
	public double z;
	public String text;
	public Color color;
	public int item_id = 0;
	public int item_scale = 2;

	public HologramData() {

	}

	public HologramData(double x, double y, double z, String text, String dimension, Color color, double max_render_distance) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;

		this.text = text;
	}

	public HologramData(BlockPos pos, String text, Dimension dimension, Color color, double max_render_distance) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.color = color;

		this.text = text;
	}
}
