package fr.sazaju.genshin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerStateHistory.Internal.AbstractHistory;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.recipe.Recipe;

public interface PlayerStateHistory {

	PlayerState getInitialState();

	PlayerState getResultingState();

	Recipe getResultingRecipe();

	Stream<PlayerState> streamStates();

	Stream<Recipe> streamRecipes();

	default PlayerStateHistory appendState(PlayerState state) {
		PlayerStateHistory history = this;
		return new AbstractHistory() {
			private Recipe getRecipe() {
				PlayerState previousState = history.getResultingState();
				ItemStack previousStack = ItemStack.fromPlayerState(previousState);
				ItemStack nextStack = ItemStack.fromPlayerState(state);
				ItemStack diffStack = nextStack.minusStack(previousStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
			}

			@Override
			public PlayerState getInitialState() {
				return history.getInitialState();
			}

			@Override
			public PlayerState getResultingState() {
				return state;
			}

			@Override
			public Recipe getResultingRecipe() {
				ItemStack lastStack = ItemStack.fromPlayerState(state);
				PlayerState initialState = history.getInitialState();
				ItemStack initialStack = ItemStack.fromPlayerState(initialState);
				ItemStack diffStack = lastStack.minusStack(initialStack);
				Map<ItemState<?>, Integer> diffMap = diffStack.getMap();
				return Recipe.fromDiff(diffMap);
			}

			@Override
			public Stream<PlayerState> streamStates() {
				return Stream.concat(history.streamStates(), Stream.of(getResultingState()));
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
			public PlayerState getInitialState() {
				return history.getInitialState();
			}

			@Override
			public PlayerState getResultingState() {
				return history.getResultingState().update(recipe.stream());
			}

			@Override
			public Recipe getResultingRecipe() {
				return history.getResultingRecipe().add(recipe);
			}

			@Override
			public Stream<PlayerState> streamStates() {
				return Stream.concat(history.streamStates(), Stream.of(getResultingState()));
			}

			@Override
			public Stream<Recipe> streamRecipes() {
				return Stream.concat(history.streamRecipes(), Stream.of(recipe));
			}
		};
	}

	public static PlayerStateHistory fromState(PlayerState state) {
		return new AbstractHistory() {
			@Override
			public PlayerState getInitialState() {
				return state;
			}

			@Override
			public PlayerState getResultingState() {
				return state;
			}

			@Override
			public Recipe getResultingRecipe() {
				return Recipe.empty();
			}

			@Override
			public Stream<PlayerState> streamStates() {
				return Stream.of(state);
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
					return Objects.equals(this.getResultingState(), that.getResultingState())//
							&& Objects.equals(collect(this.streamRecipes()), collect(that.streamRecipes()));
				} else {
					return false;
				}
			}

			@Override
			public int hashCode() {
				return Objects.hash(getResultingState(), collect(streamRecipes()));
			}

			private List<Recipe> collect(Stream<Recipe> stream) {
				return stream.collect(Collectors.toList());
			}
		}
	}
}
