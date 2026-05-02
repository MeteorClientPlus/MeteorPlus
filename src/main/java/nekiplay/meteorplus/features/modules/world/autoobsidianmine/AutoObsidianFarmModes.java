package nekiplay.meteorplus.features.modules.world.autoobsidianmine;

public enum AutoObsidianFarmModes {
	Portals_Vanilla,
	Portal_Homes,
	Cauldrons;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ');
	}
}
