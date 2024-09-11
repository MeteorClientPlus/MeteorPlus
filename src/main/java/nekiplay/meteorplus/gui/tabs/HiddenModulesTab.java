package nekiplay.meteorplus.gui.tabs;


import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.Screen;
import nekiplay.meteorplus.mixinclasses.IModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class HiddenModulesTab extends Tab {
	public HiddenModulesTab() {
		super("Hidden modules");
	}

	@Override
	public TabScreen createScreen(GuiTheme theme) {
		return new HiddenModulesScreen(theme, this);
	}

	@Override
	public boolean isScreen(Screen screen) {
		return screen instanceof HiddenModulesScreen;
	}



	public static class HiddenModulesScreen extends WindowTabScreen {
		public HiddenModulesScreen(GuiTheme theme, Tab tab) {
			super(theme, tab);
		}

		public class GuiCategory
		{
			public boolean hasHidenModules;
			public Category category;
			public WSection section;
		}

		private List<GuiCategory> categoryList = new ArrayList<>();

		@Override
		protected void onClosed() {
			super.onClosed();
			categoryList.clear();
		}

		@Override
		public void initWidgets() {
			for (Category category : Modules.loopCategories()) {
				GuiCategory guiCategory = new GuiCategory();
				guiCategory.category = category;
				for (Module module : Modules.get().getGroup(category)) {
					if (module instanceof IModule) {
						boolean isVisible = !((IModule) module).isHidden();
						if (isVisible) continue;
						guiCategory.hasHidenModules = true;
						if (categoryList.stream().noneMatch((a) -> a.category.name.equals(guiCategory.category.name))) {
							categoryList.add(guiCategory);
						}
					}
					break;
				}
			}
			for (GuiCategory guiCategory : categoryList) {
				guiCategory.section = theme.section(guiCategory.category.name, guiCategory.section != null && guiCategory.section.isExpanded());

				Cell<WSection> modulesCell = add(guiCategory.section).expandX();

				WTable table = guiCategory.section.add(theme.table()).expandX().widget();

				for (Module module : Modules.get().getGroup(guiCategory.category)) {
					if (module instanceof IModule) {
						boolean isVisible = !((IModule) module).isHidden();
						if (isVisible) continue;

						WButton moduleButton = theme.button(module.title);
						moduleButton.action = () -> {
							((IModule) module).setHidden(false);
							reload();
						};
						table.add(moduleButton).expandCellX().center().widget();
						table.row();
					}
				}
			}
		}
	}
}
