package nekiplay.meteorplus.mixin.minecraft;

import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeAccessor {
	@Accessor("category")
    CraftingBookCategory getCategory();

}
