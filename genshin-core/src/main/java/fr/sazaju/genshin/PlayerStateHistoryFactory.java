package fr.sazaju.genshin;

import static fr.sazaju.genshin.FlatMappingSpliterator.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.recipe.Recipe;

public class PlayerStateHistoryFactory {
	public static interface RecipesProvider {
		Stream<Recipe> streamRecipes();
	}

	public Stream<PlayerStateHistory> naiveSearch(PlayerState source, PlayerState target,
			RecipesProvider recipesProvider) {
		return naiveSearchRecursive(PlayerStateHistory.from(source), target, recipesProvider);
	}

	private Stream<PlayerStateHistory> naiveSearchRecursive(PlayerStateHistory currentHistory, PlayerState target,
			RecipesProvider recipesProvider) {
		PlayerState available = currentHistory.getResultingData();

		PlayerState missing = PlayerState.fromItemEntries(target.stream()//
				.map(entry -> entry.removeQuantity(available.getQuantity(entry.getItem())))//
				.filter(entry -> entry.getQuantity() > 0)//
		);

		if (missing.isEmpty()) {
			return Stream.of(currentHistory);
		} else {
			Stream<PlayerStateHistory> partialHistories = missing.stream()//
					.map(entry -> entry.getItem())//
					.flatMap(item -> recipesProvider.streamRecipes().filter(recipe -> recipe.getProducedQuantity(item) > 0))//
					.filter(recipe -> available.contains(recipe.streamCosts()))//
					.map(recipe -> currentHistory.appendRecipe(recipe));
			// Use optimized flatMap for recursive call
			return flatMap(partialHistories,
					partialHistory -> naiveSearchRecursive(partialHistory, target, recipesProvider));
		}
	}
}
