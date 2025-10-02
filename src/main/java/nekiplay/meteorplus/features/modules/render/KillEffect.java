package nekiplay.meteorplus.features.modules.render;

import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.movement.fly.FlyModes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;

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
		for (Entity entity : mc.world.getEntities()) {
			if (entity instanceof LivingEntity livingEntity) {
				if (livingEntity.deathTime > 0 || livingEntity.getHealth() <= 0) {
					if (!entityList.contains(entity)) {
						switch (mode.get()) {
							case Lighting_Bolt -> {
								LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
								lightning.refreshPositionAfterTeleport(livingEntity.getEntityPos());
								mc.world.addEntity(lightning);
							}
							case Falling_Lava -> {
								for (int i = 0; i < entity.getHeight() * 10; i++) {
									for (int j = 0; j < entity.getWidth() * 10; j++) {
										for (int k = 0; k < entity.getWidth() * 10; k++) {
											mc.world.addParticleClient(ParticleTypes.FALLING_LAVA, entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0);
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
