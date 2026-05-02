package nekiplay.meteorplus.features.modules.render;


import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.settings.items.ESPItemData;
import nekiplay.meteorplus.settings.items.ItemDataSetting;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;

public class ItemFrameEsp extends Module {

	public ItemFrameEsp() {
		super(Categories.Render, "ItemFrame-esp", "backlighting of the frames in which the selected items");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<List<Item>> whitelist = sgGeneral.add(new ItemListSetting.Builder()
		.name("whitelist")
		.description("Which items to show esp.")
		.defaultValue(
			Items.ELYTRA
		)
		.build()
	);

	private final Setting<ESPItemData> defaultBlockConfig = sgGeneral.add(new GenericSetting.Builder<ESPItemData>()
		.name("whitelist-default-config")
		.description("Default item config.")
		.defaultValue(
			new ESPItemData(
				ShapeMode.Lines,
				new SettingColor(0, 255, 200),
				new SettingColor(0, 255, 200, 25),
				false,
				new SettingColor(0, 255, 200, 125)
			)
		)
		.build()
	);
	private final Setting<Map<Item, ESPItemData>> blockConfigs = sgGeneral.add(new ItemDataSetting.Builder<ESPItemData>()
		.name("whitelist-items-configs")
		.description("Config for each item.")
		.defaultData(defaultBlockConfig)
		.build()
	);
	@EventHandler
	private void onRender2D(Render3DEvent event) {
		if (mc.level == null) return;
		for (Entity entity : mc.level.entitiesForRendering()) {
			double xl = entity.getX();
			double yl = entity.getY();
			double zl = entity.getZ();
			if (entity instanceof ItemFrame) {
				ItemFrame itemFrame = (ItemFrame)entity;
				ItemStack held = itemFrame.getItem();
				if (whitelist.get().contains(held.getItem())) {
					ESPItemData espItemData = blockConfigs.get().get(held.getItem());
					if (espItemData != null) {
						if (espItemData.tracer) {
							event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, xl, yl, zl, espItemData.tracerColor);
						}

						double x = Mth.lerp(event.tickDelta, entity.xOld, entity.getX()) - entity.getX();
						double y = Mth.lerp(event.tickDelta, entity.yOld, entity.getY()) - entity.getY();
						double z = Mth.lerp(event.tickDelta, entity.zOld, entity.getZ()) - entity.getZ();

						AABB box = entity.getBoundingBox();
						event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, espItemData.sideColor, espItemData.lineColor, espItemData.shapeMode, 0);
					}
					else {
						if (defaultBlockConfig.get().tracer) {
							event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, xl, yl, zl, defaultBlockConfig.get().tracerColor);
						}

						double x = Mth.lerp(event.tickDelta, entity.xOld, entity.getX()) - entity.getX();
						double y = Mth.lerp(event.tickDelta, entity.yOld, entity.getY()) - entity.getY();
						double z = Mth.lerp(event.tickDelta, entity.zOld, entity.getZ()) - entity.getZ();

						AABB box = entity.getBoundingBox();
						event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, defaultBlockConfig.get().sideColor, defaultBlockConfig.get().lineColor, defaultBlockConfig.get().shapeMode, 0);
					}
				}
			}
		}
	}
}
