package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;

import java.util.ArrayList;
import java.util.List;

public class DebugDrawTextEvent extends Cancellable {

	private static final DebugDrawTextEvent INSTANCE = new DebugDrawTextEvent();
	private List<String> lines = new ArrayList<String>();

	public List<String> getLines() {
		return lines;
	}

	private boolean isLeft = false;

	public boolean isLeft() {
		return isLeft;
	}


	public static DebugDrawTextEvent get(List<String> lines, boolean isLeft) {
		INSTANCE.lines = lines;
		INSTANCE.isLeft = isLeft;
		return INSTANCE;
	}
}
