package fr.sazaju.genshin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerDataHistory.Internal.AbstractHistory;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.recipe.Recipe;

public interface PlayerDataHistory {

	PlayerData getInitialData();

	PlayerData getResultingData();

	Recipe getResultingRecipe();

	Stream<PlayerData> streamData();

	Stream<Recipe> streamRecipes();

	default PlayerDataHistory appendData(PlayerData data) {
		PlayerDataHistory history = this;
		return new AbstractHistory() {
			private Recipe getRecipe() {
				PlayerData previousData = history.getResultingData();
				ItemStack previousStack = ItemStack.fromPlayerData(previousData);
				ItemStack nextStack = ItemStack.fromPlayerData(data);
				ItemStack diffStack = nextStack.minusStack(previousStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
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
				ItemStack lastStack = ItemStack.fromPlayerData(data);
				PlayerData initialData = history.getInitialData();
				ItemStack initialStack = ItemStack.fromPlayerData(initialData);
				ItemStack diffStack = lastStack.minusStack(initialStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
			}

			@Override
			public Stream<PlayerData> streamData() {
				return Stream.concat(history.streamData(), Stream.of(getResultingData()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(getRecipe()));
			}
		};
	}

	default PlayerDataHistory appendRecipe(Recipe recipe) {
		PlayerDataHistory history = this;
		return new AbstractHistory() {
			@Override
			public PlayerData getInitialData() {
				return history.getInitialData();
			}

			@Override
			public PlayerData getResultingData() {
				return history.getResultingData().update(recipe);
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
		};
	}

	public static PlayerDataHistory from(PlayerData data) {
		return new AbstractHistory() {
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
				return Recipe.empty();
			}

			@Override
			public Stream<PlayerData> streamData() {
				return Stream.of(data);
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.empty();
			}
		};
	}

	public static class Internal {

		public static abstract class AbstractHistory implements PlayerDataHistory {

			@Override
			public boolean equals(Object obj) {
				if (obj == this) {
					return true;
				} else if (obj instanceof PlayerDataHistory) {
					PlayerDataHistory that = (PlayerDataHistory) obj;
					return Objects.equals(this.getResultingData(), that.getResultingData())//
							&& Objects.equals(collect(this.streamRecipes()), collect(that.streamRecipes()));
				} else {
					return false;
				}
			}

			@Override
			public int hashCode() {
				return Objects.hash(getResultingData(), collect(streamRecipes()));
			}

			private List<Recipe> collect(Stream<Recipe> stream) {
				return stream.collect(Collectors.toList());
			}
		}
	}
}
