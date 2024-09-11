package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.systems.modules.world.InfinityMiner;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = AutoTool.class, remap = false, priority = 1001)
public class AutoToolMixin extends Module
{
	public AutoToolMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Final
	@Shadow
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	@Unique
	private final SettingGroup sgMeteorPlus = settings.createGroup(MeteorPlusAddon.HUD_TITLE);

	@Final
	@Shadow
	private final Setting<AutoTool.EnchantPreference> prefer = (Setting<AutoTool.EnchantPreference>) sgGeneral.get("prefer");

	@Final
	@Shadow
	private final Setting<Boolean> silkTouchForEnderChest = (Setting<Boolean>) sgGeneral.get("silk-touch-for-ender-chest");

	@Final
	@Shadow
	private final Setting<Boolean> fortuneForOresCrops = (Setting<Boolean>) sgGeneral.get("fortune-for-ores-and-crops");

	@Unique
	private final Setting<Boolean> silkTouchForGlowstone = sgMeteorPlus.add(new BoolSetting.Builder()
		.name("silk-touch-for-glowstone")
		.description("Mines Glowstone only with the Silk Touch enchantment.")
		.defaultValue(true)
		.build()
	);

	@Final
	@Shadow
	private final Setting<Boolean> antiBreak = (Setting<Boolean>) sgGeneral.get("anti-break");

	@Final
	@Shadow
	private final Setting<Integer> breakDurability = (Setting<Integer>) sgGeneral.get("anti-break-percentage");

	@Final
	@Shadow
	private final Setting<Integer> switchDelay =( Setting<Integer>) sgGeneral.get("switch-delay");


	@Final
	@Shadow
	private final SettingGroup sgWhitelist = settings.getGroup("Whitelist");

	@Final
	@Shadow
	private final Setting<AutoTool.ListMode> listMode = (Setting<AutoTool.ListMode>) sgWhitelist.get("list-mode");

	@Final
	@Shadow
	private final Setting<List<Item>> whitelist = (Setting<List<Item>>) sgWhitelist.get("whitelist");

	@Final
	@Shadow
	private final Setting<List<Item>> blacklist = sgWhitelist.add(new ItemListSetting.Builder()
		.name("blacklist")
		.description("The tools you don't want to use.")
		.visible(() -> listMode.get() == AutoTool.ListMode.Blacklist)
		.filter(AutoTool::isTool)
		.build()
	);

	@Shadow
	private boolean shouldSwitch;
	@Shadow
	private int ticks;
	@Shadow
	private int bestSlot;

	@Inject(method = "onStartBreakingBlock", at = @At("HEAD"), cancellable = true)
	private void onStartBreakingBlock(StartBreakingBlockEvent event, CallbackInfo ci) {
		if (PlayerUtils.getGameMode() == GameMode.CREATIVE || PlayerUtils.getGameMode() == GameMode.SPECTATOR)
		{
			if (ci.isCancellable()) {
				ci.cancel();
				return;
			}
		}

		if (Modules.get().isActive(InfinityMiner.class)) return;

		// Get blockState
		BlockState blockState = mc.world.getBlockState(event.blockPos);
		if (!BlockUtils.canBreak(event.blockPos, blockState)) return;

		// Check if we should switch to a better tool
		ItemStack currentStack = mc.player.getMainHandStack();

		double bestScore = -1;
		bestSlot = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = mc.player.getInventory().getStack(i);

			if (listMode.get() == AutoTool.ListMode.Whitelist && !whitelist.get().contains(itemStack.getItem())) continue;
			if (listMode.get() == AutoTool.ListMode.Blacklist && blacklist.get().contains(itemStack.getItem())) continue;

			double score = getScore2(itemStack, blockState, silkTouchForEnderChest.get(), silkTouchForGlowstone.get(), fortuneForOresCrops.get(), prefer.get(), itemStack2 -> !shouldStopUsing2(itemStack2));
			if (score < 0) continue;

			if (score > bestScore) {
				bestScore = score;
				bestSlot = i;
			}
		}

		if ((bestSlot != -1 && (bestScore > getScore2(currentStack, blockState, silkTouchForEnderChest.get(), silkTouchForGlowstone.get(), fortuneForOresCrops.get(), prefer.get(), itemStack -> !shouldStopUsing2(itemStack))) || shouldStopUsing2(currentStack) || !isTool2(currentStack))) {
			ticks = switchDelay.get();

			if (ticks == 0) InvUtils.swap(bestSlot, true);
			else shouldSwitch = true;
		}

		// Anti break
		currentStack = mc.player.getMainHandStack();

		if (shouldStopUsing2(currentStack) && isTool2(currentStack)) {
			mc.options.attackKey.setPressed(false);
			event.cancel();
		}

		ci.cancel();
	}

	@Unique
	private boolean shouldStopUsing2(ItemStack itemStack) {
		return antiBreak.get() && (itemStack.getMaxDamage() - itemStack.getDamage()) < (itemStack.getMaxDamage() * breakDurability.get() / 100);
	}

	@Unique
	private static double getScore2(ItemStack itemStack, BlockState state, boolean silkTouchEnderChest, boolean silkTouchGlowstone, boolean fortuneOre, AutoTool.EnchantPreference enchantPreference, Predicate<ItemStack> good) {
		if (!good.test(itemStack) || !isTool2(itemStack)) return -1;
		if (!itemStack.isSuitableFor(state) && !(itemStack.getItem() instanceof SwordItem && (state.getBlock() instanceof BambooBlock || state.getBlock() instanceof BambooShootBlock)) && !(itemStack.getItem() instanceof ShearsItem && state.getBlock() instanceof LeavesBlock || state.isIn(BlockTags.WOOL))) return -1;

		if (silkTouchEnderChest
			&& state.getBlock() == Blocks.ENDER_CHEST
			&& Utils.getEnchantmentLevel(itemStack, Enchantments.SILK_TOUCH) == 0) {
			return -1;
		}

		if (silkTouchGlowstone
			&& state.getBlock() == Blocks.GLOWSTONE
			&& Utils.getEnchantmentLevel(itemStack, Enchantments.SILK_TOUCH) == 0) {
			return -1;
		}

		if (fortuneOre
			&& isFortunable2(state.getBlock())
			&& Utils.getEnchantmentLevel(itemStack, Enchantments.FORTUNE) == 0) {
			return -1;
		}

		double score = 0;

		score += itemStack.getMiningSpeedMultiplier(state) * 1000;
		score += Utils.getEnchantmentLevel(itemStack, Enchantments.UNBREAKING);
		score += Utils.getEnchantmentLevel(itemStack, Enchantments.EFFICIENCY);
		score += Utils.getEnchantmentLevel(itemStack, Enchantments.MENDING);

		if (enchantPreference == AutoTool.EnchantPreference.Fortune) score += Utils.getEnchantmentLevel(itemStack, Enchantments.FORTUNE);
		if (enchantPreference == AutoTool.EnchantPreference.SilkTouch) score += Utils.getEnchantmentLevel(itemStack, Enchantments.SILK_TOUCH);

		if (itemStack.getItem() instanceof SwordItem item && (state.getBlock() instanceof BambooBlock || state.getBlock() instanceof BambooShootBlock))
			score += 9000 + (item.getMaterial().getMiningSpeedMultiplier() * 1000);


		return score;
	}

	@Unique
	private static boolean isFortunable2(Block block) {
		if (block == Blocks.ANCIENT_DEBRIS) return false;
		return Xray.ORES.contains(block) || block instanceof CropBlock;
	}
	@Unique
	private static boolean isTool2(Item item) {
		return item instanceof ToolItem || item instanceof ShearsItem;
	}
	@Unique
	private static boolean isTool2(ItemStack itemStack) {
		return isTool2(itemStack.getItem());
	}
}
