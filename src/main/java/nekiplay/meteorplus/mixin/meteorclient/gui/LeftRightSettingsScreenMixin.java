package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.LeftRightListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Mixin(LeftRightListSettingScreen.class)
public abstract class LeftRightSettingsScreenMixin<T> {
	private final LeftRightListSettingScreen<?> _this = (LeftRightListSettingScreen<?>) (Object) this;
	private WTable tableLeft;
	private WTable tableRight;
	private GuiTheme _theme;

	@Shadow(remap = false)
	protected abstract WTable abc(Consumer<List<Pair<T, Integer>>> addValues, boolean isLeft, Consumer<T> buttonAction);

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void init(GuiTheme theme, String title, Setting<?> setting, Collection<?> collection, Registry<T> registry, CallbackInfo ci) {
		_theme = theme;
	}

	@Redirect(method = "initWidgets(Lnet/minecraft/registry/Registry;)V", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/screens/settings/LeftRightListSettingScreen;abc(Ljava/util/function/Consumer;ZLjava/util/function/Consumer;)Lmeteordevelopment/meteorclient/gui/widgets/containers/WTable;"))
	private WTable getTable(LeftRightListSettingScreen<?> screen, Consumer<List<Pair<T, Integer>>> addValues, boolean isLeft, Consumer<T> buttonAction) {
		WTable table = abc(addValues, isLeft, buttonAction);
		if (isLeft) {
			tableLeft = table;
		} else {
			tableRight = table;
		}
		return table;
	}

	@Inject(method = "initWidgets()V", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/screens/settings/LeftRightListSettingScreen;add(Lmeteordevelopment/meteorclient/gui/widgets/WWidget;)Lmeteordevelopment/meteorclient/gui/utils/Cell;", shift = At.Shift.AFTER, ordinal = 0), remap = false)
	private void initWidgets(CallbackInfo ci) {
		WHorizontalList list = _theme.horizontalList();

		WButton addAllButton = list.add(_theme.button("Select all")).expandX().widget();
		WButton removeAllButton = list.add(_theme.button("Deselect all")).expandX().widget();

		addAllButton.action = () -> {
			tableLeft.cells.forEach(cell -> {
				if (cell.widget() instanceof WPressable button) {
					button.action.run();
				}
			});
		};

		removeAllButton.action = () -> {
			tableRight.cells.forEach(cell -> {
				if (cell.widget() instanceof WPressable button) {
					button.action.run();
				}
			});
		};

		_this.add(list).expandX();
	}
}
