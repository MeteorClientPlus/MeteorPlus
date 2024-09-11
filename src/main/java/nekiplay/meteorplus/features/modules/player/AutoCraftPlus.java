package nekiplay.meteorplus.features.modules.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.mixin.minecraft.CraftingScreenHandlerAccessor;
import nekiplay.meteorplus.utils.BlockHelper;
import nekiplay.meteorplus.utils.TickTimer;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import nekiplay.meteorplus.mixin.minecraft.ShapelessRecipeAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ibm.icu.util.LocalePriorityList.add;

public class AutoCraftPlus extends Module {
	public AutoCraftPlus() {
		super(Categories.Player, "Auto Craft", "Automatically craft items.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Boolean> autoOpen = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-open")
		.description("Auto open crafting table.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Integer> delayMs = sgGeneral.add(new IntSetting.Builder()
		.name("delay-ms")
		.description("Click delay")
		.defaultValue(100)
		.sliderMin(15)
		.sliderMax(1000)
		.build()
	);

	private final Setting<Boolean> autoPlace = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-place")
		.description("Auto place crafting table.")
		.defaultValue(false)
		.visible(autoOpen::get)
		.build()
	);

	private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
		.name("auto-place")
		.description("Auto place crafting table.")
		.defaultValue(5)
		.visible(autoOpen::get)
		.build()
	);

	private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
		.name("items")
		.description("Items you want to get crafted.")
		.defaultValue(List.of())
		.onChanged(list -> this.itemsListChanged.set(true))
		.build()
	);

	private final Setting<Boolean> antiDesync = sgGeneral.add(new BoolSetting.Builder()
		.name("anti-desync")
		.description("Try to prevent inventory desync.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> craftAll = sgGeneral.add(new BoolSetting.Builder()
		.name("craft-all")
		.description("Crafts maximum possible amount amount per craft (shift-clicking)")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> recursive = sgGeneral.add(new BoolSetting.Builder()
		.name("recursive")
		.description("Recursively resources for ingredients and craft them if possible")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> drop = sgGeneral.add(new BoolSetting.Builder()
		.name("drop")
		.description("Automatically drops crafted items (useful for when not enough inventory space)")
		.defaultValue(false)
		.build()
	);

	private final SettingGroup sgSpecial = settings.createGroup("Special crafts");

	private final Setting<Boolean> tier3Fireworks = sgSpecial.add(new BoolSetting.Builder()
		.name("tier-3-fireworks")
		.description("In recipe book there is only tier-1 fireworks recipe, which is useless.")
		.defaultValue(false)
		.build()
	);

	public boolean replaceTier3FireworksRecipe(){
		return tier3Fireworks.get();
	}

	private BlockPos findCraftingTable() {
		assert mc.player != null;
		List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), radius.get(), radius.get());
		for (BlockPos block : nearbyBlocks) if (BlockHelper.getBlock(block) == Blocks.CRAFTING_TABLE) return block;
		return null;
	}

	private void openCraftingTable(BlockPos tablePos) {
		Vec3d tableVec = new Vec3d(tablePos.getX(), tablePos.getY(), tablePos.getZ());
		BlockHitResult table = new BlockHitResult(tableVec, Direction.UP, tablePos, false);
		assert mc.interactionManager != null;
		mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, table);
	}

	private void placeCraftingTable(FindItemResult craftTable) {
		assert mc.player != null;
		List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), radius.get(), radius.get());
		for (BlockPos block : nearbyBlocks) {
			if (BlockHelper.getBlock(block) == Blocks.AIR) {
				BlockUtils.place(block, craftTable, 0, true);
				break;
			}
		}
	}
	@Override
	public void onActivate() {
		timer = new TickTimer(delayMs.get());
		opened = false;
		if (autoOpen.get()) {
			BlockPos craftingTable = findCraftingTable();
			if (craftingTable != null) {
				openCraftingTable(craftingTable);
			}
			else {
				FindItemResult craftTableFind = InvUtils.findInHotbar(Blocks.CRAFTING_TABLE.asItem());
				if (craftTableFind.found() && autoPlace.get()) {
					placeCraftingTable(craftTableFind);
				}
			}
		}
	}

	private boolean hasIngredients(Recipe<?> recipe)
	{
		Multiset<Item> ingredients = HashMultiset.create();
		recipe.getIngredients().forEach(ing -> {
			for (var stack : ing.getMatchingStacks()){
				ingredients.add(stack.getItem());
			}
		});

		return ingredients.stream().allMatch(ing -> {
			FindItemResult findRes = InvUtils.find(item -> ing == item.getItem());
			return findRes.found() && findRes.count() >= ingredients.count(ing);
		});
	}
	private boolean opened = false;

	private TickTimer timer = new TickTimer(delayMs.get());

	private interface Click {
		boolean click();
	}

	Random rand = new Random();

	private final Queue<Click> clicksQueue = new LinkedList<>();
	private final AtomicBoolean itemsListChanged = new AtomicBoolean(true);
	private final Multimap<Item,RecipeEntry<?>> recipesCache = HashMultimap.create();

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (mc.interactionManager == null) return;
		if (items.get().isEmpty()) return;

		if (mc.player != null && mc.player.currentScreenHandler instanceof CraftingScreenHandler) {
			if (antiDesync.get())
				mc.player.getInventory().updateItems();
			opened = true;

			// continue only if all clicks are performed
			if (!clicksQueue.isEmpty()){
				// wait for timer
				if (!timer.elapsed(delayMs.get() + Math.abs(rand.nextInt())%50)) return;
				// click
				if (clicksQueue.element().click()){
					clicksQueue.remove();
				}
				return;
			}
			// if list changed -> update cache
			if (itemsListChanged.compareAndExchange(true, false)) {
				recipesCache.clear();
				updateCache(items.get());
			}

			for (var item : items.get()) {
				findRecipes(item);
			}

		}
		else if (!opened) {
			if (autoOpen.get()) {
				BlockPos crafting_table = findCraftingTable();
				if (crafting_table != null) {
					openCraftingTable(crafting_table);
				}
			}
		}
	}

	private void updateCache(List<Item> targets) {
		var recipeResultCollectionList = mc.player.getRecipeBook().getOrderedResults();
		for (var recipeResultCollection : recipeResultCollectionList) {
			for (var recipe : recipeResultCollection.getAllRecipes()) {
				var output = recipe.value().getResult(mc.getNetworkHandler().getRegistryManager()).getItem();
				if (!targets.contains(output)) continue;

				var ingredients = getIngredientsIfNotRecursive(recipe.value(), targets);

				if (tier3Fireworks.get() &&
					output.asItem() == Items.FIREWORK_ROCKET &&
					recipe.value() instanceof ShapelessRecipe
				){
					// basically it's just for generalised calculation of items needed
					recipe = new RecipeEntry(recipe.id(), new ShapelessRecipe(recipe.value().getGroup(),
						((ShapelessRecipeAccessor)recipe.value()).getCategory(),
						new ItemStack(Items.FIREWORK_ROCKET, 3),
						DefaultedList.copyOf(null,
							Ingredient.ofItems(Items.GUNPOWDER),
							Ingredient.ofItems(Items.GUNPOWDER),
							Ingredient.ofItems(Items.GUNPOWDER),
							Ingredient.ofItems(Items.PAPER)
						)));
				}

				if (ingredients != null){
					recipesCache.put(output, recipe);
					updateCache(ingredients);
				}
			}
		}
	}

	private @Nullable List<Item> getIngredientsIfNotRecursive(Recipe<?> recipe, List<Item> targets){
		List<Item> ingredients = new ArrayList<>();
		for (var i : recipe.getIngredients()) {
			for (var stack : i.getMatchingStacks()) {
				var item = stack.getItem();
				if (targets.contains(item) || recipesCache.containsKey(item)){
					return null;
				}
				ingredients.add(stack.getItem());
			}
		}
		return ingredients;
	}

	private boolean findRecipes(Item target){
		var recipes = recipesCache.get(target);
		for (var recipe : recipes){
			if (hasIngredients(recipe.value())) {
				var output = recipe.value().getResult(mc.getNetworkHandler().getRegistryManager()).getItem();

				if (tier3Fireworks.get() && output.asItem() == Items.FIREWORK_ROCKET){
					fillFireworkTier3Clicks();
					return true;
				}
				clicksQueue.add(() -> {
					mc.interactionManager.clickRecipe(mc.player.currentScreenHandler.syncId, recipe, craftAll.get());
					return true;
				});

				clicksQueue.add(() -> {
					(drop.get() ? InvUtils.drop() : InvUtils.shiftClick()).slotId(0);
					return true;
				});

				return true;
			}
		}

		if (!recursive.get())
			return false;

		for (var recipe : recipes){
			for (var i : recipe.value().getIngredients()) {
				for (var stack : i.getMatchingStacks()) {
					if (findRecipes(stack.getItem()))
						return true;
				}
			}
		}

		return false;
	}

	private void fillFireworkTier3Clicks(){
		int gunpowderSlots = 3;
		int paperSlots = 1;

		for (int i = SlotUtils.HOTBAR_START;
			 i <= SlotUtils.MAIN_END && !(gunpowderSlots == 0 && paperSlots == 0);
			 i++
		) {
			int finalI = i;

			var item = mc.player.getInventory().getStack(i).getItem();
			if (item == Items.GUNPOWDER && gunpowderSlots > 0){
				gunpowderSlots--;
				clicksQueue.add(() -> {
					var sh = (CraftingScreenHandlerAccessor)mc.player.currentScreenHandler;
					if (sh.getInput().count(Items.GUNPOWDER) >= 3*64)
						return false;

					InvUtils.shiftClick().slot(finalI);
					return true;
				});
			}
			else if (item == Items.PAPER && paperSlots > 0){
				paperSlots--;
				clicksQueue.add(() -> {
					var sh = (CraftingScreenHandlerAccessor)mc.player.currentScreenHandler;
					if (sh.getInput().count(Items.PAPER) >= 64)
						return false;

					InvUtils.shiftClick().slot(finalI);
					return true;
				});
			}
		}

		clicksQueue.add(() -> {
			var sh = (CraftingScreenHandlerAccessor)mc.player.currentScreenHandler;
			if (sh.getInput().count(Items.GUNPOWDER) < 3 && sh.getInput().count(Items.PAPER) < 1)
				return false;

			(drop.get() ? InvUtils.drop() : InvUtils.shiftClick()).slotId(0);
			return true;
		});

	}

}
