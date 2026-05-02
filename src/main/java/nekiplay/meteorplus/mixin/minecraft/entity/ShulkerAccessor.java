package nekiplay.meteorplus.mixin.minecraft.entity;

import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Shulker.class)
public interface ShulkerAccessor {
	@Invoker("getRawPeekAmount")
	int GetRawPeekAmount();

	@Invoker("isClosed")
	boolean isClosed();
}
