package nekiplay.meteorplus.utils.algoritms;

import nekiplay.meteorplus.utils.Perlin2D;

import java.util.Random;

public class PerlinNoice {
	public static int PerlinNoice(int multiply) {
		Perlin2D perlin = new Perlin2D(new Random().nextInt());
		float Phi = 0.70710678118f;
		float noice = perlin.Noise(5, 5) + perlin.Noise((25 - 25) * Phi, (25 + 25) * Phi) * -1;
		return (int) (noice * multiply);
	}
	public static int PerlinNoice(double multiply) {
		Perlin2D perlin = new Perlin2D(new Random().nextInt());
		float Phi = 0.70710678118f;
		float noice = perlin.Noise(5, 5) + perlin.Noise((25 - 25) * Phi, (25 + 25) * Phi) * -1;
		return (int) (noice * multiply);
	}
}
