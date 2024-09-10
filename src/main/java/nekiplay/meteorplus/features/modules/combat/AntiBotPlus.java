package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import nekiplay.meteorplus.utils.ColorRemover;

import java.util.*;

public class AntiBotPlus extends Module {
	public AntiBotPlus() {
		super(Categories.Combat, "Anti Bot", "Remove bots.");
	}

	/* Thanks LiquidBounce
		https://github.com/CCBlueX/LiquidBounce/blob/legacy/src/main/java/net/ccbluex/liquidbounce/features/module/modules/misc/AntiBot.kt
	 */

	private final SettingGroup sgFilters = settings.createGroup("Filters");

	public enum TabMode {
		Equals,
		Contains,
		Contains_LowerCase;

		@Override
		public String toString() {
			return super.toString().replace('_', ' ');
		}
	}

	private final Setting<Boolean> tab = sgFilters.add(new BoolSetting.Builder()
		.name("tab")
		.description("check tab.")
		.defaultValue(true)
		.build()
	);

	private final Setting<TabMode> tabMode = sgFilters.add(new EnumSetting.Builder<TabMode>()
		.name("tab-mode")
		.description("check tab mode.")
		.defaultValue(TabMode.Contains)
		.visible(tab::get)
		.build()
	);

	private final Setting<Boolean> entityID = sgFilters.add(new BoolSetting.Builder()
		.name("EntityID")
		.description("check entity id.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> color = sgFilters.add(new BoolSetting.Builder()
		.name("Color")
		.description("check color.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> ground = sgFilters.add(new BoolSetting.Builder()
		.name("ground")
		.description("check ground.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> air = sgFilters.add(new BoolSetting.Builder()
		.name("air")
		.description("check air.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> InvalidGround = sgFilters.add(new BoolSetting.Builder()
		.name("Invalid-Ground")
		.description("check Invalid Ground.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> swing = sgFilters.add(new BoolSetting.Builder()
		.name("Swing")
		.description("check Swing.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> health = sgFilters.add(new BoolSetting.Builder()
		.name("health")
		.description("check health.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> derp = sgFilters.add(new BoolSetting.Builder()
		.name("derp")
		.description("check derp.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> useHash = sgFilters.add(new BoolSetting.Builder()
		.name("Memorize bots")
		.description("Prevent attacking and blinking esp.")
		.defaultValue(true)
		.build()
	);

	private ArrayList<UUID> hash = new ArrayList<UUID>();
	private ArrayList<Integer> swings = new ArrayList<Integer>();
	private ArrayList<Integer> grounds = new ArrayList<Integer>();
	private ArrayList<Integer> airs = new ArrayList<Integer>();
	private Map<Integer, Integer> invalidGrounds  = new HashMap<>();

	@Override
	public void onDeactivate() {
		hash.clear();
	}

	public boolean isBot(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return isBot(living);
		}
		return false;
	}

	public boolean isBot(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity))
			return false;
		if (!isActive())
			return false;

		if (useHash.get() && hash.contains(entity.getUuid())) {
			return true;
		}

		if (color.get() && entity.getDisplayName().getString().replace("§r", "").contains("§")) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}

		if (ground.get() && !grounds.contains(entity.getId())) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}

		if (InvalidGround.get() && invalidGrounds.getOrDefault(entity.getId(), 0) >= 10) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}

		if (entityID.get() && (entity.getId() >= 1000000000 || entity.getId() <= -1)) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}

		if (derp.get() && (entity.getPitch() > 90f || entity.getPitch() < -90)) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}

		if (swing.get() && !swings.contains(entity.getId())) {
			if (useHash.get()) {
				hash.add(entity.getUuid());
			}
			return true;
		}


		if (tab.get()) {
			String targetname = ColorRemover.GetVerbatim(entity.getDisplayName().getString());
			if (mc != null && mc.getNetworkHandler() != null) {
				Collection<PlayerListEntry> entryCollection = mc.getNetworkHandler().getPlayerList();
				for (PlayerListEntry info : entryCollection) {
					if (info.getDisplayName() != null) {
						String networkName = ColorRemover.GetVerbatim(info.getDisplayName().getString());
						if (tabMode.get() == TabMode.Equals) {
							if (targetname.equals(networkName)) {
								return false;
							}
						}
						else if (tabMode.get() == TabMode.Contains_LowerCase) {
							if (targetname.toLowerCase().contains(networkName.toLowerCase())) {
								return false;
							}
						}
						else {
							if (targetname.contains(networkName)) {
								return false;
							}
						}
					}
				}
				if (useHash.get()) {
					hash.add(entity.getUuid());
				}
				return true;
			}
		}

		return entity.getName().getString().isEmpty() || entity.getName() == mc.player.getName();
	}

	@EventHandler
	private void onEntityAdd(EntityAddedEvent event) {
		isBot(event.entity);
	}

	@EventHandler
	private void onEntityRemove(EntityRemovedEvent event) {
		if (hash.contains(event.entity.getUuid())) {
			Iterator<UUID> iterator = hash.iterator();
			while (iterator.hasNext()) {
				if (iterator.next() == event.entity.getUuid()) {
					iterator.remove();
					return;
				}
			}
		}
	}

	@EventHandler
	private void livingEntityMove(PacketEvent.Receive event) {
		if (event.packet instanceof EntityPositionS2CPacket packet) {
			if (mc.world != null) {
				Entity entity = mc.world.getEntityById(packet.getId());
				if (entity != null) {
					if (entity.isOnGround()) {
						grounds.add(entity.getId());
					}

					if (!entity.isOnGround() && !airs.contains(entity.getId()))
						airs.add(entity.getId());

					if (entity.isOnGround()) {
						if (entity.prevY != entity.getY())
							invalidGrounds.put(entity.getId(), invalidGrounds.getOrDefault(entity.getId(), 0) + 1);
					} else {
						int currentVL = invalidGrounds.getOrDefault(entity.getId(), 0) / 2;
						if (currentVL <= 0)
							invalidGrounds.remove(entity.getId());
						else
							//invalidGrounds.put(entity.getId(), invalidGrounds.getOrDefault(entity.getId(), currentVL));
							invalidGrounds.replace(entity.getId(), currentVL);
					}
				}
			}
		}
		else if (event.packet instanceof EntityAnimationS2CPacket packet) {
			if (mc.world != null) {
				Entity entity = mc.world.getEntityById(packet.getId());
				if (entity != null) {
					if (entity instanceof LivingEntity && packet.getAnimationId() == 0 && !swings.contains(entity.getId())) {
						swings.add(entity.getId());
					}
				}
			}
		}
	}

	@EventHandler
	public void worldEvent(GameLeftEvent event) {
		clearAll();
	}

	private void clearAll() {
		swings.clear();
		grounds.clear();
		invalidGrounds.clear();
	}
}
