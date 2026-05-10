package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

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

	private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
		.name("On ground only")
		.description("Attack entities on ground only")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> customCheck = sgGeneral.add(new BoolSetting.Builder()
		.name("Custom on ground check")
		.description("Use a custom on ground check instead of the usual entity.isOnGround. Useful on some servers")
		.defaultValue(false)
		.build()
	);

	private boolean entityCheck(Entity entity) {
		if (entity.equals(mc.player) || entity.equals(mc.getCameraEntity())) return false;
		if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDeadOrDying()) || !entity.isAlive())
			return false;
		if (!entities.get().contains(entity.getType())) return false;
		if (entity instanceof OwnableEntity tameable
			&& tameable.getOwner().getUUID() != null
			&& tameable.getOwner().getUUID().equals(mc.player.getUUID())) return false;
		if (entity instanceof Player player) {
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
		if (onGround.get()) {
			if (customCheck.get()) {
				Level world = entity.level();

				Vec3 entityPos = entity.position();
				BlockPos posBelow = new BlockPos((int) entityPos.x, (int) (entityPos.y - 1), (int) entityPos.z);

				Block blockBelow = world.getBlockState(posBelow).getBlock();

				return (blockBelow != Blocks.AIR && blockBelow != Blocks.WATER && blockBelow != Blocks.LAVA);
			} else return entity.onGround();
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
		if (mc.level != null) {
			TargetUtils.getList(targets, this::entityCheck, SortPriority.LowestDistance, 25);

			for (Entity entity : targets) {

				PathManagers.get().moveTo(entity.blockPosition());
				return;
			}
		}
	}
}
