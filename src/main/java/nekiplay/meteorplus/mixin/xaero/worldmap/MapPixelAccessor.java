package nekiplay.meteorplus.mixin.xaero.worldmap;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xaero.map.region.MapPixel;

@Mixin(MapPixel.class)
public interface MapPixelAccessor {
	@Accessor("state")
	BlockState getBlockState();
}
