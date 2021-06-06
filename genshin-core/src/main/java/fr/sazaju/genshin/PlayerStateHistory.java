package fr.sazaju.genshin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerStateHistory.Internal.AbstractHistory;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.recipe.Recipe;

public interface PlayerStateHistory {

	PlayerState getInitialData();

	PlayerState getResultingData();

	Recipe getResultingRecipe();

	Stream<PlayerState> streamData();

	Stream<Recipe> streamRecipes();

	default PlayerStateHistory appendData(PlayerState data) {
		PlayerStateHistory history = this;
		return new AbstractHistory() {
			private Recipe getRecipe() {
				PlayerState previousData = history.getResultingData();
				ItemStack previousStack = ItemStack.fromPlayerData(previousData);
				ItemStack nextStack = ItemStack.fromPlayerData(data);
				ItemStack diffStack = nextStack.minusStack(previousStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
			}

			@Override
			public PlayerState getInitialData() {
				return history.getInitialData();
			}

			@Override
			public PlayerState getResultingData() {
				return data;
			}

			@Override
			public Recipe getResultingRecipe() {
				ItemStack lastStack = ItemStack.fromPlayerData(data);
				PlayerState initialData = history.getInitialData();
				ItemStack initialStack = ItemStack.fromPlayerData(initialData);
				ItemStack diffStack = lastStack.minusStack(initialStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
			}

			@Override
			public Stream<PlayerState> streamData() {
				return Stream.concat(history.streamData(), Stream.of(getResultingData()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(getRecipe()));
			}
		};
	}

	default PlayerStateHistory appendRecipe(Recipe recipe) {
		PlayerStateHistory history = this;
		return new AbstractHistory() {
			@Override
			public PlayerState getInitialData() {
				return history.getInitialData();
			}

			@Override
			public PlayerState getResultingData() {
				return history.getResultingData().update(recipe.getDiff().entrySet().stream().map(ItemEntry::fromMapEntry));
			}

			@Override
			public Recipe getResultingRecipe() {
				return history.getResultingRecipe().add(recipe);
			}

			@Override
			public Stream<PlayerState> streamData() {
				return Stream.concat(history.streamData(), Stream.of(getResultingData()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(recipe));
			}
		};
	}

	public static PlayerStateHistory from(PlayerState data) {
		return new AbstractHistory() {
			@Override
			public PlayerState getInitialData() {
				return data;
			}

			@Override
			public PlayerState getResultingData() {
				return data;
			}

			@Override
			public Recipe getResultingRecipe() {
				return Recipe.empty();
			}

			@Override
			public Stream<PlayerState> streamData() {
				return Stream.of(data);
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.empty();
			}
		};
	}

	public static class Internal {

		public static abstract class AbstractHistory implements PlayerStateHistory {

			@Override
			public boolean equals(Object obj) {
				if (obj == this) {
					return true;
				} else if (obj instanceof PlayerStateHistory) {
					PlayerStateHistory that = (PlayerStateHistory) obj;
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
