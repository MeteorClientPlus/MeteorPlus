package nekiplay.meteorplus.utils;

import nekiplay.meteorplus.mixin.minecraft.ClientConnectionAccessor;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class TickTimer {
	private int ticksBegin = ticks();
	private double delay;

	public TickTimer(double delay){
		this.delay = delay;
	}

	public void reset(double delay){
		this.delay = delay;
		this.ticksBegin = ticks();
	}

	public boolean elapsed(double newDelay){
		int currentTicks = ticks();

		if (currentTicks == -1 || ticksBegin == -1 ||
			(1000.0/20.0)*(currentTicks - ticksBegin) >= delay
		){
			reset(newDelay);
			return true;
		}

		return false;
	}

	public boolean elapsed(){
		return elapsed(delay);
	}

	private static int ticks() {
		var handler = mc.getNetworkHandler();
		if (handler == null) return -1;

		return ((ClientConnectionAccessor)handler.getConnection()).getTicks();
	}

}
