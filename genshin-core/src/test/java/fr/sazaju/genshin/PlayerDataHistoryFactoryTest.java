package fr.sazaju.genshin;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import fr.sazaju.genshin.PlayerDataHistoryFactory.RecipesProvider;
import fr.sazaju.genshin.recipe.Recipe;

class PlayerDataHistoryFactoryTest {

	// TODO Test

	@Test
	void testNaiveSearchReturnsOnlyEmptyHistoryForSameTargetThanSource() {
		// GIVEN
		PlayerData data = PlayerData.empty();
		RecipesProvider recipesProvider = () -> {
			throw new RuntimeException("It should not be called because not needed");
		};
		PlayerDataHistoryFactory factory = new PlayerDataHistoryFactory();
		
		// WHEN
		List<PlayerDataHistory> histories = factory.naiveSearch(data, data, recipesProvider).collect(toList());
		
		// THEN
		assertEquals(1, histories.size());
		PlayerDataHistory history = histories.get(0);
		assertEquals(0, history.streamRecipes().count());
	}

}
