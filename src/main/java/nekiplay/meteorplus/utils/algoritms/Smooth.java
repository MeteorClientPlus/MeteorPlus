package nekiplay.meteorplus.utils.algoritms;

public class Smooth {
	public static double getDouble(SmoothType type, double diffAngle, double minRotation, double maxRotation) {
		double speeds = 180;
		if (type == SmoothType.Linear) {
			speeds = (diffAngle / 180 * maxRotation + (1 - diffAngle / 180) * minRotation);
		} else if (type == SmoothType.Quad) {
			speeds = Math.pow((diffAngle / 180), 2.0) * maxRotation + (1 - Math.pow((diffAngle / 180), 2.0)) * minRotation;
		} else if (type == SmoothType.Sine || type == SmoothType.QuadSine) {
			final double v = -Math.cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5;
			if (type == SmoothType.Sine) {
				speeds = v * maxRotation + (Math.cos((diffAngle / 180 * Math.PI) * 0.5 + 0.5) * 0.5 + 0.5) * minRotation;
			} else {
				speeds = Math.pow(v, 2.0) * maxRotation + (1 - Math.pow(v, 2.0)) * minRotation;
			}
		}
		else if (type == SmoothType.Perlin) {
			int noice = PerlinNoice.PerlinNoice(maxRotation);
			if (noice > 0) {
				speeds = noice;
			}
			else {
				speeds = maxRotation;
			}
		}
		else if (type == SmoothType.PerlinRandom) {
			double random = RandomUtils.nextDouble(minRotation, maxRotation);
			int noice = PerlinNoice.PerlinNoice(random);
			if (noice > 0) {
				speeds = noice;
			}
			else {
				speeds = random;
			}
		}
		return speeds;
	}

	public enum SmoothType {
		None,
		Linear,
		Quad,
		Sine,
		QuadSine,
		Perlin,
		PerlinRandom,
	}
}
