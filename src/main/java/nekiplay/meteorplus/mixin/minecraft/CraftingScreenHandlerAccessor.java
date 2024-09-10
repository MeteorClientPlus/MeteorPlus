package nekiplay.meteorplus.mixin.minecraft;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingScreenHandlerAccessor {
	@Accessor("result")
	CraftingResultInventory getResult();

	@Accessor("input")
	RecipeInputInventory getInput();
}
