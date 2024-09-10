package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ElytraFly.class, remap = false)
public class ElytraFlyMixin extends Module {

	public ElytraFlyMixin(Category category, String name, String description) {
		super(category, name, description);
	}
}
