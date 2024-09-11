package nekiplay.meteorplus.features.modules.movement.nofall;

public enum NoFallModes {
	Matrix_New,
	Vulcan,
	Vulcan_2dot7dot7,
	Verus,
	Elytra_Clip,
	Elytra_Fly;
	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("dot", ".");
	}
}
