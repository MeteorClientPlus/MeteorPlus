package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WBlockPosEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import nekiplay.meteorplus.mixinclasses.SpoofMode;
import nekiplay.meteorplus.settings.ConfigModifier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WBlockPosEdit.class, remap = false, priority = 1001)
public class WBlockPosEditMixin extends WHorizontalList  {
	@Shadow
	public Runnable action;
	@Shadow
	public Runnable actionOnRelease;
	@Shadow
	private WTextBox textBoxX;
	@Shadow
	private WTextBox textBoxY;
	@Shadow
	private WTextBox textBoxZ;
	@Shadow
	private Screen previousScreen;
	@Shadow
	private BlockPos value;
	@Shadow
	private BlockPos lastValue;
	@Shadow
	private boolean clicking;
	@Inject(method = "addTextBox", at = @At("HEAD"), cancellable = true)
	private void addTextBox(CallbackInfo ci) {
		this.textBoxX = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.getX()), this::filter)).minWidth(75.0).widget();
		this.textBoxY = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.getY()), this::filter)).minWidth(75.0).widget();
		this.textBoxZ = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.getZ()), this::filter)).minWidth(75.0).widget();
		this.textBoxX.actionOnUnfocused = () -> {
			try {
				this.lastValue = this.value;
				if (this.textBoxX.get().isEmpty()) {
					this.set(new BlockPos(0, 0, 0));
				} else {
					this.set(new BlockPos(Integer.parseInt(this.textBoxX.get()) - ConfigModifier.get().x_spoof.get(), this.value.getY(), this.value.getZ()));
				}

				this.newValueCheck();
			}
			catch (NumberFormatException ignore) { }
		};
		this.textBoxY.actionOnUnfocused = () -> {
			try {
				this.lastValue = this.value;
				if (this.textBoxY.get().isEmpty()) {
					this.set(new BlockPos(0, 0, 0));
				} else {
					this.set(new BlockPos(this.value.getX(), Integer.parseInt(this.textBoxY.get()), this.value.getZ()));
				}

				this.newValueCheck();
			}
			catch (NumberFormatException ignore) { }
		};
		this.textBoxZ.actionOnUnfocused = () -> {
			try {
				this.lastValue = this.value;
				if (this.textBoxZ.get().isEmpty()) {
					this.set(new BlockPos(0, 0, 0));
				} else {
					if (ConfigModifier.get().spoofMode.get() == SpoofMode.Fake) {
						this.set(new BlockPos(this.value.getX(), this.value.getY(), Integer.parseInt(this.textBoxZ.get())));
					}
					else {
						this.set(new BlockPos(this.value.getX(), this.value.getY(), Integer.parseInt(this.textBoxZ.get()) - ConfigModifier.get().z_spoof.get()));
					}
				}

				this.newValueCheck();
			}
			catch (NumberFormatException ignore) { }
		};



		if (ConfigModifier.get().positionProtection.get()) {
			textBoxX.set("***");
			textBoxZ.set("***");
			textBoxY.set("***");
		}
		ci.cancel();
	}
	@Shadow
	private boolean filter(String text, char c) {
		boolean validate = true;
		boolean good;
		if (c == '-' && text.isEmpty()) {
			good = true;
			validate = false;
		} else {
			good = Character.isDigit(c);
		}

		if (good && validate) {
			try {
				Integer.parseInt(text + c);
			} catch (NumberFormatException var6) {
				good = false;
			}
		}

		return good;
	}
	@Shadow
	public BlockPos get() {
		return this.value;
	}
	@Shadow
	public void set(BlockPos value) {
		this.value = value;
	}
	@Shadow
	private void newValueCheck() {
		if (this.value != this.lastValue) {
			if (this.action != null) {
				this.action.run();
			}

			if (this.actionOnRelease != null) {
				this.actionOnRelease.run();
			}
		}

	}

}
