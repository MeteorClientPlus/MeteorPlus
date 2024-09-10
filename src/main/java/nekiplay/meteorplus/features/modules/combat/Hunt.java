package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Set;

public class Hunt extends Module {
	public Hunt() {
		super(Categories.Combat, "Hunt", "Automatic walk to selected entities");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.build()
	);

	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return false;
		if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
		if (!entities.get().contains(entity.getType())) return false;
		if (entity instanceof Tameable tameable
			&& tameable.getOwnerUuid() != null
			&& tameable.getOwnerUuid().equals(mc.player.getUuid())) return false;
		if (entity instanceof PlayerEntity player) {
			if (player.isCreative()) return false;
			if (!Friends.get().shouldAttack(player)) return false;
			AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
			Teams teams = Modules.get().get(Teams.class);
			if (antiBotPlus != null && antiBotPlus.isBot(player)) {
				return false;
			}
			if (teams != null && teams.isInYourTeam(player)) {
				return false;
			}
		}

		return true;
	}
	private final ArrayList<Entity> targets = new ArrayList<>();

	@Override
	public void onDeactivate() {
		targets.clear();
		PathManagers.get().stop();
	}

	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		if (mc.world != null) {
			TargetUtils.getList(targets, this::entityCheck, SortPriority.LowestDistance, 25);

			for (Entity entity : targets) {

				PathManagers.get().moveTo(entity.getBlockPos());
				return;
			}
		}
	}
}
