package nekiplay.main.events;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class RenderSignTextEvent {
	private static final RenderSignTextEvent INSTANCE = new RenderSignTextEvent();

	public BlockPos pos;
	public SignText signText;
	public MatrixStack matrices;
	public VertexConsumerProvider vertexConsumers;
	public int light;
	public int lineHeight;
	public int lineWidth;
	public boolean front;

	public static RenderSignTextEvent get(BlockPos pos, SignText signText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int lineHeight, int lineWidth, boolean front) {
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
