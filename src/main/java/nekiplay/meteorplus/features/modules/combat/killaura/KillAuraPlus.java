package nekiplay.meteorplus.features.modules.combat.killaura;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.features.modules.combat.killaura.modes.Matrix;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Set;

public class KillAuraPlus extends Module {
	public KillAuraPlus() {
		super(Categories.Combat, "kill-aura+", "Better killaura.");
	}

	public final SettingGroup sgGeneral = settings.getDefaultGroup();

	public final Setting<KillAuraPlusModes> mode = sgGeneral.add(new EnumSetting.Builder<KillAuraPlusModes>()
		.name("mode")
		.description("KillAura mode.")
		.defaultValue(KillAuraPlusModes.Matrix)
		.onModuleActivated(modesSetting -> onModeChanged(modesSetting.get()))
		.onChanged(this::onModeChanged)
		.build()
	);

	private final SettingGroup sgTargeting = settings.createGroup("Targeting");
	private final SettingGroup sgRotation = settings.createGroup("Rotation");

	// General

	public final Setting<KillAura.Weapon> weapon = sgGeneral.add(new EnumSetting.Builder<KillAura.Weapon>()
		.name("weapon")
		.description("Only attacks an entity when a specified weapon is in your hand.")
		.defaultValue(KillAura.Weapon.All)
		.build()
	);

	public final Setting<Boolean> autoSwitch = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-switch")
		.description("Switches to your selected weapon when attacking the target.")
		.defaultValue(false)
		.build()
	);

	public final Setting<KillAura.ShieldMode> shieldMode = sgGeneral.add(new EnumSetting.Builder<KillAura.ShieldMode>()
		.name("shield-mode")
		.description("Will try and use an axe to break target shields.")
		.defaultValue(KillAura.ShieldMode.Break)
		.visible(() -> autoSwitch.get() && weapon.get() != KillAura.Weapon.Axe)
		.build()
	);

	// Targeting

	public final Setting<Set<EntityType<?>>> entities = sgTargeting.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to attack.")
		.onlyAttackable()
		.defaultValue(EntityType.PLAYER)
		.build()
	);

	public final Setting<SortPriority> priority = sgTargeting.add(new EnumSetting.Builder<SortPriority>()
		.name("priority")
		.description("How to filter targets within range.")
		.defaultValue(SortPriority.ClosestAngle)
		.build()
	);

	public final Setting<Double> range = sgTargeting.add(new DoubleSetting.Builder()
		.name("range")
		.description("The maximum range the entity can be to attack it.")
		.defaultValue(4.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	public final Setting<Double> wallsRange = sgTargeting.add(new DoubleSetting.Builder()
		.name("walls-range")
		.description("The maximum range the entity can be attacked through walls.")
		.defaultValue(3.5)
		.min(0)
		.sliderMax(6)
		.build()
	);

	public final Setting<Boolean> ignorePassive = sgTargeting.add(new BoolSetting.Builder()
		.name("ignore-passive")
		.description("Will only attack sometimes passive mobs if they are targeting you.")
		.defaultValue(true)
		.build()
	);

	@Unique
	public final Setting<Boolean> onlyCrits = sgTargeting.add(new BoolSetting.Builder()
		.name("only-crits")
		.description("Attack enemy only if this attack crit after jump.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> ignoreTamed = sgTargeting.add(new BoolSetting.Builder()
		.name("ignore-tamed")
		.description("Will avoid attacking mobs you tamed.")
		.defaultValue(false)
		.build()
	);

	public final Setting<Matrix.Type> rotationType = sgRotation.add(new EnumSetting.Builder<Matrix.Type>()
		.name("rotation-type")
		.defaultValue(Matrix.Type.Smooth)
		.build()
	);

	public final Setting<Boolean> speedUpRotationWhenAttacking = sgRotation.add(new BoolSetting.Builder()
		.name("Speed-up-the-rotation-when-attacking")
		.defaultValue(false)
		.build()
	);



	private KillAuraPlusMode currentMode;

	private void onModeChanged(KillAuraPlusModes mode) {
		switch (mode) {
			case Matrix -> currentMode = new Matrix();
		}
	}
	@EventHandler
	private void onTickPre(TickEvent.Pre event) {
		currentMode.onTickPre(event);
	}
	@EventHandler
	private void onTickPost(TickEvent.Post event) {
		currentMode.onTickPost(event);
	}
	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		currentMode.onSendPacket(event);
	}

	@Override
	public void onDeactivate() {
		currentMode.onDeactivate();
	}

	@Override
	public void onActivate() {
		currentMode.onActivate();
	}

	@Override
	public String getInfoString() {
		return currentMode.getInfoString();
	}
}
