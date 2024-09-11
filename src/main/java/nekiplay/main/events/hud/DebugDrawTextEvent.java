package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.util.hit.HitResult;

import java.util.ArrayList;
import java.util.List;

public class DebugDrawTextEvent extends Cancellable {

	private static final DebugDrawTextEvent INSTANCE = new DebugDrawTextEvent();
	private List<String> lines = new ArrayList<String>();

	public List<String> getLines() { return lines; }
	private boolean isLeft = false;
	public boolean isLeft() { return isLeft; }


	private HitResult blockHit;
	private HitResult fluidHit;
	public HitResult blockHit() { return blockHit; }
	public HitResult fluidHit() { return fluidHit; }

	public static DebugDrawTextEvent get(List<String> lines, boolean isLeft, HitResult blockHit, HitResult fluidHit) {
		INSTANCE.lines = lines;
		INSTANCE.isLeft = isLeft;
		INSTANCE.blockHit = blockHit;
		INSTANCE.fluidHit = fluidHit;
		return INSTANCE;
	}
}
