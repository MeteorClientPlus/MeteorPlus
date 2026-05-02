package nekiplay.main.events;

import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;

public class RenderSignTextEvent {
	private static final RenderSignTextEvent INSTANCE = new RenderSignTextEvent();

	public BlockPos pos;
	public SignText signText;
	public PoseStack matrices;
	public MultiBufferSource vertexConsumers;
	public int light;
	public int lineHeight;
	public int lineWidth;
	public boolean front;

	public static RenderSignTextEvent get(BlockPos pos, SignText signText, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int lineHeight, int lineWidth, boolean front) {
		INSTANCE.pos = pos;
		INSTANCE.signText = signText;
		INSTANCE.matrices = matrices;
		INSTANCE.vertexConsumers = vertexConsumers;
		INSTANCE.light = light;
		INSTANCE.lineHeight = lineHeight;
		INSTANCE.lineWidth = lineWidth;
		INSTANCE.front = front;
		return INSTANCE;
	}
}
