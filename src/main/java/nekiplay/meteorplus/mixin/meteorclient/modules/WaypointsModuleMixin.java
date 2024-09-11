package nekiplay.meteorplus.mixin.meteorclient.modules;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import com.sun.source.tree.Tree;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import baritone.api.pathing.goals.GoalGetToBlock;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.Utils;
import nekiplay.meteorplus.utils.NumeralUtils;
import nekiplay.meteorplus.mixinclasses.EditWaypointScreen;
import nekiplay.meteorplus.mixinclasses.WIcon;
import nekiplay.meteorplus.mixinclasses.WaypointsModuleModes;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.render.color.Color.GRAY;
import static nekiplay.meteorplus.MeteorPlusAddon.HUD_TITLE;

@Mixin(WaypointsModule.class)
public class WaypointsModuleMixin extends Module {

	@Unique
	public final AtomicReference<GuiTheme> themeRef = new AtomicReference<>();
	@Unique
	public final AtomicReference<WTable> tableRef = new AtomicReference<>();

	@Unique
	private final SettingGroup meteorPlusTab = settings.createGroup(HUD_TITLE);

	@Unique
	private final Setting<Boolean> showDistance = meteorPlusTab.add(new BoolSetting.Builder()
		.name("show-distance")
		.description("Show distance in this gui.")
		.onChanged((a) ->  {
			GuiTheme t = themeRef.get();
			WTable tab = tableRef.get();
			if (t != null && tab != null) {
				initTable(t, tab);
			}
		})
		.defaultValue(true)
		.build()
	);

	@Unique
	private final Setting<Boolean> showCompactDistance = meteorPlusTab.add(new BoolSetting.Builder()
		.name("show-compact-distance")
		.description("Show compact distance in this gui.")
		.onChanged((a) ->  {
			GuiTheme t = themeRef.get();
			WTable tab = tableRef.get();
			if (t != null && tab != null) {
				initTable(t, tab);
			}
		})
		.visible(showDistance::get)
		.defaultValue(true)
		.build()
	);

	@Unique
	private final Setting<WaypointsModuleModes.SortMode> sortMode = meteorPlusTab.add(new EnumSetting.Builder<WaypointsModuleModes.SortMode>()
		.name("sort-mode")
		.description("Sorting waypoints mode.")
		.defaultValue(WaypointsModuleModes.SortMode.Distance)
		.onChanged((a) ->  {
			GuiTheme t = themeRef.get();
			WTable tab = tableRef.get();
			if (t != null && tab != null) {
				initTable(t, tab);
			}
		})
		.build()
	);

	@Unique
	private final Setting<String> search = meteorPlusTab.add(new StringSetting.Builder()
		.name("search")
		.description("Search waypoint by text")
		.defaultValue("")
			.onChanged((a) ->  {
				GuiTheme t = themeRef.get();
				WTable tab = tableRef.get();
				if (t != null && tab != null) {
					initTable(t, tab);
				}
			})
		.build()
	);

	public WaypointsModuleMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Inject(method = "getWidget", at = @At("HEAD"), remap = false, cancellable = true)
	private void getWidget(GuiTheme theme, CallbackInfoReturnable<WWidget> cir) {
		if (!Utils.canUpdate()) {
			cir.setReturnValue(theme.label("You need to be in a world."));
		}

		WTable table = theme.table();
		initTable(theme, table);
		themeRef.set(theme);
		tableRef.set(table);
		cir.setReturnValue(table);
	}

	@Unique
	private void initTable(GuiTheme theme, WTable table) {
		table.clear();

		for (Waypoint waypoint : Waypoints.get()) {
			boolean validDim = Waypoints.checkDimension(waypoint);

			table.add(new WIcon(waypoint));

			WLabel name = table.add(theme.label(waypoint.name.get())).expandCellX().widget();
			if (!validDim) name.color = GRAY;

			WCheckbox visible = table.add(theme.checkbox(waypoint.visible.get())).widget();
			visible.action = () -> {
				waypoint.visible.set(visible.checked);
				Waypoints.get().save();
			};

			WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
			edit.action = () -> mc.setScreen(new EditWaypointScreen(theme, waypoint, () -> initTable(theme, table)));

			// Goto
			if (validDim) {
				WButton gotoB = table.add(theme.button("Goto")).widget();
				gotoB.action = () -> {
					if (PathManagers.get().isPathing())
						PathManagers.get().stop();

					PathManagers.get().moveTo(waypoint.getPos());
				};
			}

			WMinus remove = table.add(theme.minus()).widget();
			remove.action = () -> {
				Waypoints.get().remove(waypoint);
				initTable(theme, table);
			};

			table.row();
		}

		table.add(theme.horizontalSeparator()).expandX();
		table.row();

		WButton create = table.add(theme.button("Create")).expandX().widget();
		create.action = () -> mc.setScreen(new EditWaypointScreen(theme, null, () -> initTable(theme, table)));
	}
}
