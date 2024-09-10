package nekiplay.bozeplus.features.modules.movement.spider;

public enum SpiderModes {
	Matrix,
	Vulcan,
	Elytra_clip;

	@Override
	public String toString() {
		return super.toString().replace('_', ' ').replaceAll("_Lower_", "<");
	}
}
