package nekiplay.bozeplus.impl;

import dev.boze.api.input.Bind;

public class BozePlusBind implements Bind {

	private int bind;
	private boolean button;

	public BozePlusBind(int bind, boolean button) {
		this.bind = bind;
		this.button = button;
	}

	public void setBind(int bind, boolean button) {
		this.bind = bind;
		this.button = button;
	}

	@Override
	public int getBind() {
		return bind;
	}

	@Override
	public boolean isButton() {
		return button;
	}
}
