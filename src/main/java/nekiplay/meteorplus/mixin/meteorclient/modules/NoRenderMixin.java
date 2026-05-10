package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.hud.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = NoRender.class, remap = false, priority = 1001)
public class NoRenderMixin extends Module {
	public NoRenderMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Unique
	private final SettingGroup noRenderMeteorPlusSetting = settings.createGroup("F3");

	@Shadow
	private final SettingGroup sgHUD = settings.getGroup("HUD");


	@Unique
	private final Setting<Boolean> noPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("position")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionBlock = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("position-block")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionChunk = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("position-chunk")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetBlockPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("target-block-position")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetFluidPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("target-fluid-position")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> experienceBar = sgHUD.add(new BoolSetting.Builder()
		.name("experience-bar")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> experienceLevel = sgHUD.add(new BoolSetting.Builder()
		.name("experience-level")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> healthBar = sgHUD.add(new BoolSetting.Builder()
		.name("health-bar")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> mountHealthBar = sgHUD.add(new BoolSetting.Builder()
		.name("mount-health-bar")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> foodBar = sgHUD.add(new BoolSetting.Builder()
		.name("food-bar")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> armorBar = sgHUD.add(new BoolSetting.Builder()
		.name("armor-bar")
		.defaultValue(false)
		.build()
	);

	@Unique
	@EventHandler
	private void onMountHealthBarRender(RenderMountHealthBarEvent event) {
		if (mountHealthBar.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onArmorBarRender(RenderArmorBarEvent event) {
		if (armorBar.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onFoodBarRender(RenderFoodBarEvent event) {
		if (foodBar.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onHealthBarRender(RenderHealthBarEvent event) {
		if (healthBar.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onExperienceBarRender(RenderExperienceBarEvent event) {
		if (experienceBar.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onExperienceLevelRender(RenderExperienceLevelEvent event) {
		if (experienceLevel.get()) {
			event.setCancelled(true);
		}
	}

	@Unique
	@EventHandler
	private void onDebugF3RenderText(DebugDrawTextEvent event) {
		List<String> lines = event.getLines();

		if (event.isLeft()) {
			if (noPosition.get()) {
				lines.removeIf(s -> s.contains("XYZ:"));
			}

			if (noPositionBlock.get()) {
				lines.removeIf(s -> s.contains("Block:"));
			}

			if (noPositionChunk.get()) {
				lines.removeIf(s -> s.contains("Chunk:"));
			}
		} else {
			if (noTargetBlockPosition.get()) {
				lines.removeIf(s -> s.contains("Targeted Block:"));
			}

			if (noTargetFluidPosition.get()) {
				lines.removeIf(s -> s.contains("Targeted Fluid:"));
			}
		}
	}
}
