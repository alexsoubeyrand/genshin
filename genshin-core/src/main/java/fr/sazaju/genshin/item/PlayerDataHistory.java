package fr.sazaju.genshin.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerData;
import fr.sazaju.genshin.recipe.Recipe;

public interface PlayerDataHistory {

	PlayerData getInitialData();

	PlayerData getResultingData();

	Recipe getResultingRecipe();

	Stream<PlayerData> streamData();

	Stream<Recipe> streamRecipes();

	default PlayerDataHistory appendData(PlayerData data) {
		PlayerDataHistory history = this;
		return new PlayerDataHistory() {
			private Recipe getRecipe() {
				return Recipe.fromDiff(ItemStack.fromPlayerData(data)
						.minusStack(ItemStack.fromPlayerData(history.getResultingData())));
			}

			@Override
			public PlayerData getInitialData() {
				return history.getInitialData();
			}

			@Override
			public PlayerData getResultingData() {
				return data;
			}

			@Override
			public Recipe getResultingRecipe() {
				return Recipe.fromDiff(
						ItemStack.fromPlayerData(data).minusStack(ItemStack.fromPlayerData(history.getInitialData())));
			}

			@Override
			public Stream<PlayerData> streamData() {
				return Stream.concat(history.streamData(), Stream.of(getResultingData()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(getRecipe()));
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}
		};
	}

	default PlayerDataHistory appendRecipe(Recipe recipe) {
		PlayerDataHistory history = this;
		return new PlayerDataHistory() {
			@Override
			public PlayerData getInitialData() {
				return history.getInitialData();
			}

			@Override
			public PlayerData getResultingData() {
				return history.getResultingData().apply(recipe);
			}

			@Override
			public Recipe getResultingRecipe() {
				return history.getResultingRecipe().add(recipe);
			}

			@Override
			public Stream<PlayerData> streamData() {
				return Stream.concat(history.streamData(), Stream.of(getResultingData()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(recipe));
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}
		};
	}

	public static PlayerDataHistory from(PlayerData data) {
		return new PlayerDataHistory() {
			@Override
			public PlayerData getInitialData() {
				return data;
			}

			@Override
			public PlayerData getResultingData() {
				return data;
			}

			@Override
			public Recipe getResultingRecipe() {
				return Recipe.fromDiff(ItemStack.empty());
			}

			@Override
			public Stream<PlayerData> streamData() {
				return Stream.of(data);
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.empty();
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}

		};
	}

	public static PlayerDataHistory search(PlayerData source, PlayerData target, HistorySelector historySelector) {
		return search(PlayerDataHistory.from(source), target, historySelector);
	}

	private static PlayerDataHistory search(PlayerDataHistory originalHistory, PlayerData target,
			HistorySelector historySelector) {
		PlayerDataHistory currentHistory = originalHistory;
		boolean isUpdated;
		do {
			isUpdated = false;
			for (Item<?> item : target) {
				int targetQuantity = target.getQuantity(item);
				PlayerDataHistory nextHistory = fillMaterialByConversions(currentHistory, item, targetQuantity,
						historySelector);
				isUpdated = isUpdated || !nextHistory.equals(currentHistory);
				currentHistory = nextHistory;
			}
		} while (isUpdated);
		return historySelector.select(originalHistory, currentHistory, target);
	}

	private static PlayerDataHistory fillMaterialByConversions(PlayerDataHistory history, Item<?> item,
			int targetQuantity, HistorySelector historySelector) {
		int currentQuantity = history.getResultingData().getQuantity(item);
		if (targetQuantity <= currentQuantity) {
			// Nothing to fill
			return history;
		} else {
			int requiredQuantity = targetQuantity - currentQuantity;
			Map<Recipe, PlayerDataHistory> recipesResult = new HashMap<>();
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
				if (recipe.getProducts().stream().mapToInt(ItemEntry::getQuantity).sum() > 1) {
					new RuntimeException("Managing multiple products is not implemented yet: " + recipe)
							.printStackTrace();
					return;// Ignore this case for now
				}

				Recipe conversions = recipe.times(requiredQuantity);
				ItemStack cost = conversions.getCost();
				PlayerDataHistory recipeHistory = search(history, PlayerData.fromItemEntries(cost), historySelector);
				if (ItemStack.fromPlayerData(recipeHistory.getResultingData()).contains(cost)) {
					recipesResult.put(recipe, recipeHistory.appendRecipe(conversions));
				} else {
					// Cannot reach target, ignore recipe
				}
			});
			// TODO Return all results instead of first one
			return recipesResult.isEmpty() ? history : recipesResult.values().iterator().next();
		}
	}

	public static interface HistorySelector {
		public static final HistorySelector BEST_EFFORT = (originalHistory, currentHistory, target) -> {
			// Use what we could do so far
			return currentHistory;
		};
		public static final HistorySelector ONLY_IF_SUCCESSFUL = (originalHistory, currentHistory, target) -> {
			PlayerData currentData = currentHistory.getResultingData();
			Optional<ItemEntry> unsatisfiedCase = target.stream().filter(targetItem -> {
				return targetItem.getQuantity() > currentData.getQuantity(targetItem.getItem());
			}).findAny();
			if (unsatisfiedCase.isPresent()) {
				// Cannot reach target, don't touch anything
				return originalHistory;
			} else {
				return currentHistory;
			}
		};

		PlayerDataHistory select(PlayerDataHistory originalHistory, PlayerDataHistory currentHistory,
				PlayerData target);
	}

	public static class Internal {
		private static List<Recipe> collect(Stream<Recipe> stream) {
			return stream.collect(Collectors.toList());
		}

		private static boolean areEqual(PlayerDataHistory history, Object obj) {
			if (obj == history) {
				return true;
			} else if (obj instanceof PlayerDataHistory) {
				PlayerDataHistory that = (PlayerDataHistory) obj;
				return Objects.equals(ItemStack.fromPlayerData(history.getResultingData()),
						ItemStack.fromPlayerData(that.getResultingData()))//
						&& Objects.equals(collect(history.streamRecipes()), collect(that.streamRecipes()));
			} else {
				return false;
			}
		}

		private static int hashCode(PlayerDataHistory history) {
			return Objects.hash(Recipe.fromDiff(ItemStack.fromPlayerData(history.getResultingData())),
					collect(history.streamRecipes()));
		}
	}

}
