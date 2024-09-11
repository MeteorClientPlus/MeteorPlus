package nekiplay.meteorplus.features.modules.movement.scaffold;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

/*
	Not done
 */
public class ScaffoldPlus extends Module {
	public ScaffoldPlus() {
		super(Categories.Movement, "Scaffold+", "Better scaffold module");
	}

	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("dot", ".");
	}
}
