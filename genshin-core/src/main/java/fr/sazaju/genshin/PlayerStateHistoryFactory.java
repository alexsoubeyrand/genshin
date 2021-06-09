package fr.sazaju.genshin;

import static fr.sazaju.genshin.FlatMappingSpliterator.*;

import java.util.stream.Stream;

import fr.sazaju.genshin.recipe.Recipe;

public class PlayerStateHistoryFactory {
	public static interface RecipesProvider {
		Stream<Recipe> streamRecipes();
	}
	
	private final RecipesProvider recipesProvider;
	
	public PlayerStateHistoryFactory(RecipesProvider recipesProvider) {
		this.recipesProvider=recipesProvider;
	}

	public Stream<PlayerStateHistory> naiveSearch(PlayerState source, PlayerState target) {
		return naiveSearchRecursive(PlayerStateHistory.fromState(source), target);
	}

	private Stream<PlayerStateHistory> naiveSearchRecursive(PlayerStateHistory currentHistory, PlayerState target) {
		PlayerState available = currentHistory.getResultingState();

		PlayerState missing = PlayerState.fromItemEntries(target.stream()//
				.map(entry -> entry.removeQuantity(available.getQuantity(entry.getItem())))//
				.filter(entry -> entry.getQuantity() > 0)//
		);

		if (missing.isEmpty()) {
			return Stream.of(currentHistory);
		} else {
			Stream<PlayerStateHistory> partialHistories = missing.stream()//
					.map(entry -> entry.getItem())//
					.flatMap(item -> recipesProvider.streamRecipes()
							.filter(recipe -> recipe.getProducedQuantity(item) > 0))//
					.filter(recipe -> available.contains(recipe.streamCosts()))//
					.map(recipe -> currentHistory.appendRecipe(recipe));
			// Use optimized flatMap for recursive call
			return flatMap(partialHistories,
					partialHistory -> naiveSearchRecursive(partialHistory, target));
		}
	}
}
