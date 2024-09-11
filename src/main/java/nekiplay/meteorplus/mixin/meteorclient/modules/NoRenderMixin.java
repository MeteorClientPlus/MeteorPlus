package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.orbit.EventHandler;
import nekiplay.main.events.hud.DebugDrawTextEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.List;

@Mixin(value = NoRender.class, remap = false, priority = 1001)
public class NoRenderMixin extends Module {
	public NoRenderMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Unique
	private final NoRender noRender = (NoRender)(Object) this;
	@Unique
	private final SettingGroup noRenderMeteorPlusSetting = noRender.settings.createGroup("F3");


	@Unique
	private final Setting<Boolean> noPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionBlock = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position-block")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionChunk = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position-chunk")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetBlockPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-target-block-position")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetFluidPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-target-fluid-position")
		.defaultValue(false)
		.build()
	);


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
