package nekiplay.meteorplus.utils.algoritms;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
	public static int nextInt(int min, int max) {
		if (max <= min) return max;
		else return ThreadLocalRandom.current().nextInt(min, max);
	}
}
