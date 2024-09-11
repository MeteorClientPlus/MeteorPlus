package nekiplay.meteorplus.features.modules.world.timer;

public enum TimerModes {
	NCP,
	Intave,
	Vulcan,
	Grim,
	OldFag,
	Custom,
	Custom_v2;

	@Override
	public String toString() {
		String name = name();
		return name.replace('_', ' ').replaceAll("rem", "");
	}
}
