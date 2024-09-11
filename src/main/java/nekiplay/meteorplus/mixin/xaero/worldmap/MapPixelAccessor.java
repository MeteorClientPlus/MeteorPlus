package nekiplay.meteorplus.mixin.xaero.worldmap;

import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xaero.map.region.MapPixel;

@Mixin(MapPixel.class)
public interface MapPixelAccessor {
	@Accessor("state")
	BlockState getBlockState();
}
