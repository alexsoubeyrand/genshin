package fr.sazaju.genshin;

import static fr.sazaju.genshin.FlatMappingSpliterator.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.recipe.Recipe;

public class PlayerDataHistoryFactory {
	public static interface RecipesProvider {
		Stream<Recipe> streamRecipes();
	}

	public Stream<PlayerDataHistory> naiveSearch(PlayerData source, PlayerData target,
			RecipesProvider recipesProvider) {
		return naiveSearchRecursive(PlayerDataHistory.from(source), target, recipesProvider);
	}

	private Stream<PlayerDataHistory> naiveSearchRecursive(PlayerDataHistory currentHistory, PlayerData target,
			RecipesProvider recipesProvider) {
		PlayerData available = currentHistory.getResultingData();

		PlayerData missing = PlayerData.fromItemEntries(target.stream()//
				.map(entry -> entry.removeQuantity(available.getQuantity(entry.getItem())))//
				.filter(entry -> entry.getQuantity() > 0)//
				.collect(Collectors.toList())//
		);

		if (missing.isEmpty()) {
			return Stream.of(currentHistory);
		} else {
			Stream<PlayerDataHistory> partialHistories = missing.stream()//
					.map(entry -> entry.getItem())//
					.flatMap(item -> recipesProvider.streamRecipes().filter(recipe -> recipe.getProducedQuantity(item) > 0))//
					.filter(recipe -> available.contains(recipe.streamCosts().collect(Collectors.toList())))//
					.map(recipe -> currentHistory.appendRecipe(recipe));
			// Use optimized flatMap for recursive call
			return flatMap(partialHistories,
					partialHistory -> naiveSearchRecursive(partialHistory, target, recipesProvider));
		}
	}
}
