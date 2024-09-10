package nekiplay.meteorplus.mixin.minecraft.entity;

import net.minecraft.entity.mob.ShulkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerEntity.class)
public interface ShulkerEntityAccessor {
	@Invoker("getPeekAmount")
	int getPeekAmount();

	@Invoker("isClosed")
	boolean isClosed();
}
