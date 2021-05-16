package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.item.Mora.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.recipe.Recipe;

public class ItemStack {
	private final Map<Item<?>, Integer> map;

	private ItemStack(Map<Item<?>, Integer> map) {
		this.map = map;
	}

	public static ItemStack fromItemsMap(Map<Item<?>, Integer> map) {
		return new ItemStack(map);
	}

	public static ItemStack fromTypesMap(Map<ItemType.WithSingleRarity, Integer> map) {
		return fromItemsMap(map.entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey().item(), entry.getValue()))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Set<Item<?>> getMaterials() {
		return map.keySet();
	}

	public int getQuantity(Item<?> item) {
		return map.getOrDefault(item, 0);
	}

	public ItemStack addStack(ItemStack that) {
		return ItemStack.fromItemsMap(Stream.of(this, that)//
				// Retrieve each unique key
				.map(ItemStack::getMaterials)//
				.flatMap(Set::stream)//
				.distinct()//
				// Retrieve the value of each key into a map
				.collect(Collectors.toMap(//
						key -> key, //
						key -> this.getQuantity(key) //
								+ that.getQuantity(key)//
				)));
	}

	public ItemStack addStack(Collection<Entry> stack) {
		return addStack(ItemStack.fromItemsMap(stack.stream()//
				.collect(Collectors.toMap(Entry::getItem, Entry::getQuantity))));
	}

	public ItemStack addMaterial(Item<?> item, int quantity) {
		return addStack(ItemStack.fromItemsMap(Map.of(item, quantity)));
	}

	public ItemStack minusStack(ItemStack stack) {
		return addStack(stack.times(-1));
	}

	public ItemStack minusStack(Collection<Entry> stack) {
		return minusStack(ItemStack.fromItemsMap(stack.stream()//
				.collect(Collectors.toMap(Entry::getItem, Entry::getQuantity))));
	}

	public ItemStack minusMaterial(Item<?> item, int quantity) {
		return minusStack(ItemStack.fromItemsMap(Map.of(item, quantity)));
	}

	public ItemStack times(int multiplier) {
		return ItemStack.fromItemsMap(map.entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey(), entry.getValue() * multiplier))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public ItemStack filter(Filter filter) {
		return ItemStack.fromItemsMap(map.entrySet().stream()//
				.filter(entry -> filter.test(entry.getKey(), entry.getValue()))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public boolean contains(ItemStack stack) {
		// TODO What about negative quantities?
		return map.entrySet().stream()//
				.allMatch(entry -> {
					Item<?> item = entry.getKey();
					int quantity = entry.getValue();
					int currentQuantity = getQuantity(item);
					return currentQuantity >= quantity;
				});
	}

	public boolean contains(Item<?> item) {
		// TODO What about negative quantities?
		return getQuantity(item) != 0;
	}

	public static interface HistorySelector {
		public static final HistorySelector BEST_EFFORT = (originalHistory, currentHistory,
				targetQuantities) -> currentHistory;
		public static final HistorySelector ONLY_IF_SUCCESSFUL = (originalHistory, currentHistory,
				targetQuantities) -> {
			for (Item<?> item : targetQuantities.getMaterials()) {
				if (currentHistory.getResultingStack().getQuantity(item) < targetQuantities.getQuantity(item)) {
					// Cannot reach target, don't touch anything
					return originalHistory;
				}
			}
			return currentHistory;
		};

		ItemStackHistory select(ItemStackHistory originalHistory, ItemStackHistory currentHistory,
				ItemStack targetQuantities);
	}

	public ItemStackHistory createRecipeHistory(ItemStack targetQuantities, HistorySelector historySelector) {
		return createRecipeHistory(ItemStackHistory.from(this), targetQuantities, historySelector);
	}

	private static ItemStackHistory createRecipeHistory(ItemStackHistory originalHistory, ItemStack targetQuantities,
			HistorySelector historySelector) {
		ItemStackHistory currentHistory = originalHistory;
		boolean isUpdated;
		do {
			isUpdated = false;
			for (Item<?> item : targetQuantities.getMaterials()) {
				int targetQuantity = targetQuantities.getQuantity(item);
				ItemStackHistory nextHistory = fillMaterialByConversions(currentHistory, item, targetQuantity,
						historySelector);
				isUpdated = isUpdated || !nextHistory.equals(currentHistory);
				currentHistory = nextHistory;
			}
		} while (isUpdated);
		return historySelector.select(originalHistory, currentHistory, targetQuantities);
	}

	private static ItemStackHistory fillMaterialByConversions(ItemStackHistory history, Item<?> item,
			int targetQuantity, HistorySelector historySelector) {
		int currentQuantity = history.getResultingStack().getQuantity(item);
		if (targetQuantity <= currentQuantity) {
			// Nothing to fill
			return history;
		} else {
			int requiredQuantity = targetQuantity - currentQuantity;
			Map<Recipe, ItemStackHistory> recipesResult = new HashMap<>();
			ItemType type = item.getType();
			Stream<Recipe> recipes;
			if (type instanceof ItemType.WithMultipleRarities) {
				recipes = ((ItemType.WithMultipleRarities) type).streamRecipesAt(item.getRarity());
			} else if (type instanceof ItemType.WithSingleRarity) {
				recipes = ((ItemType.WithSingleRarity) type).streamRecipes();
			} else {
				throw new RuntimeException("Not managed type: " + type);
			}
			recipes.forEach(recipe -> {
				// FIXME Consider cases where we produce more than one item at a time
				// Example: enhancement ore forging
				Recipe conversions = recipe.times(requiredQuantity);
				ItemStack cost = conversions.getCost();
				ItemStackHistory recipeHistory = createRecipeHistory(history, cost, historySelector);
				if (recipeHistory.getResultingStack().contains(cost)) {
					recipesResult.put(recipe, recipeHistory.appendDiff(conversions.getDiff()));
				} else {
					// Cannot reach target, ignore recipe
				}
			});
			// TODO Return best result instead of first one
			return recipesResult.isEmpty() ? history : recipesResult.values().iterator().next();
		}
	}

	public static ItemStack empty() {
		return ItemStack.fromItemsMap(Collections.emptyMap());
	}

	public static interface Filter {
		boolean test(Item<?> item, int quantity);

		default Filter and(Filter otherFilter) {
			return (item, quantity) -> this.test(item, quantity) && otherFilter.test(item, quantity);
		}

		default Filter or(Filter otherFilter) {
			return (item, quantity) -> this.test(item, quantity) || otherFilter.test(item, quantity);
		}

		public static Filter nonZero() {
			return (item, quantity) -> quantity != 0;
		}

		public static Filter strictlyPositive() {
			return (item, quantity) -> quantity > 0;
		}

		public static Filter strictlyNegative() {
			return (item, quantity) -> quantity < 0;
		}

		public static Filter noMora() {
			return (item, quantity) -> !item.getType().equals(MORA);
		}

		public static Filter items(Collection<Item<?>> items) {
			return (item, quantity) -> items.contains(item);
		}

		public static Filter items(Item<?>... items) {
			return items(Set.of(items));
		}

		public static Filter itemsIn(ItemStack stack) {
			return items(stack.getMaterials());
		}

	}

	public Stream<Entry> stream() {
		return map.entrySet().stream().map(entry -> entry(entry.getKey(), entry.getValue()));
	}

	public static interface Entry {
		Item<?> getItem();

		int getQuantity();
	}

	public static Entry entry(Item<?> item, int quantity) {
		return new Entry() {

			@Override
			public Item<?> getItem() {
				return item;
			}

			@Override
			public int getQuantity() {
				return quantity;
			}
		};
	}

}
