package nekiplay.meteorplus.features.modules.movement.nofall;

public enum NoFallModes {
	Matrix_New,
	Vulcan,
	Vulcan_2dot7dot7,
	Verus,
	Elytra_Clip,
	No_Ground,
	No_Ground_Elytra;
	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("dot", ".");
	}
}
