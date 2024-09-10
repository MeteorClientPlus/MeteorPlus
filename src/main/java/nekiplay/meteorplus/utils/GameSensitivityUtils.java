package nekiplay.meteorplus.utils;

import net.minecraft.client.MinecraftClient;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GameSensitivityUtils {
	public static float getSensitivity(float rot) {
		return getDeltaMouse(rot) * getGCDValue();
	}

	public static float getGCDValue() {
		return (float) (getGCD() * 0.15);
	}

	public static float getGCD() {
		float f1;
		return (f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2)) * f1 * f1 * 8;
	}

	public static float getDeltaMouse(float delta) {
		return Math.round(delta / getGCDValue());
	}
}
