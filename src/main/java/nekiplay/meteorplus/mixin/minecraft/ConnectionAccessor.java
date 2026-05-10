package nekiplay.meteorplus.mixin.minecraft;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public interface ConnectionAccessor {
	@Accessor("tickCount")
	int getTickCount();
}
