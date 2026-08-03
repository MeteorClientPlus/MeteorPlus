package nekiplay.meteorplus.features.modules.render;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;

import java.util.ArrayList;

public class KillEffect extends Module {
	public KillEffect() {
		super(Categories.Render, "kill-effect", "Render kill effect");
	}

	public final ArrayList<Entity> entityList = new ArrayList<>();
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<Effects> mode = sgGeneral.add(new EnumSetting.Builder<Effects>()
		.name("effect")
		.description("The method of applying fly.")
		.defaultValue(Effects.Lighting_Bolt)
		.build()
	);


	public enum Effects {
		Lighting_Bolt,
		Falling_Lava;


		@Override
		public String toString() {
			return super.toString().replaceAll("_", " ");
		}
	}

	@EventHandler
	public void onTickEvent(TickEvent.Post event) {
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (entity instanceof LivingEntity livingEntity) {
				if (livingEntity.deathTime > 0 || livingEntity.getHealth() <= 0) {
					if (!entityList.contains(entity)) {
						switch (mode.get()) {
							case Lighting_Bolt -> {
								LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, mc.level);
								lightning.snapTo(livingEntity.position());
								mc.level.addEntity(lightning);
							}
							case Falling_Lava -> {
								for (int i = 0; i < entity.getBbHeight() * 10; i++) {
									for (int j = 0; j < entity.getBbWidth() * 10; j++) {
										for (int k = 0; k < entity.getBbWidth() * 10; k++) {
											mc.level.addParticle(ParticleTypes.FALLING_LAVA, entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0);
										}
									}
								}
							}
							default -> {
							}
						}
						entityList.add(entity);
					}
				}
			}
		}
	}

}
