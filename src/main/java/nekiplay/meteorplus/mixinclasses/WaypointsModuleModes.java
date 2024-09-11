package nekiplay.meteorplus.mixinclasses;

import meteordevelopment.meteorclient.systems.waypoints.Waypoint;

import java.util.Comparator;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WaypointsModuleModes {
	public enum SortMode {
		None,
		Distance,
		Name,
	}

	public static class DistanceComparator implements Comparator<String> {
		Map<String, Waypoint> base;

		public DistanceComparator(Map<String, Waypoint> base) {
			this.base = base;
		}
		public int compare(String a, String b) {
			long distance1 = 0;
			long distance2 = 0;
			if (mc.player != null) {
				Waypoint awp = base.get(a);
				Waypoint bwp = base.get(b);
				if (awp != null && bwp != null) {
					if (awp.getPos() != null && bwp.getPos() != null) {

						distance1 = Math.round(mc.player.getPos().distanceTo(awp.getPos().toCenterPos()));
						distance2 = Math.round(mc.player.getPos().distanceTo(bwp.getPos().toCenterPos()));
					}
				}
			}
			if (distance1 >= distance2) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	public static class NameComparator implements Comparator<String> {
		Map<String, Waypoint> base;

		public NameComparator(Map<String, Waypoint> base) {
			this.base = base;
		}
		public int compare(String a, String b) {
			if (base.containsKey(a) && base.containsKey(b)) {

				Waypoint awp = base.get(a);
				Waypoint bwp = base.get(b);
				if (awp != null && bwp != null) {
					if (awp.name.get().length() >= bwp.name.get().length()) {
						return 1;
					} else {
						return -1;
					}
				}
				return 0;
			}
			else {
				return 0;
			}
		}
	}
}
