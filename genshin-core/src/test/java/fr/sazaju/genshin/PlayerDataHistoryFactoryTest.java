package fr.sazaju.genshin;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import fr.sazaju.genshin.PlayerStateHistoryFactory.RecipesProvider;
import fr.sazaju.genshin.recipe.Recipe;

class PlayerDataHistoryFactoryTest {

	// TODO Test

	@Test
	void testNaiveSearchReturnsOnlyEmptyHistoryForSameTargetThanSource() {
		// GIVEN
		PlayerState data = PlayerState.empty();
		RecipesProvider recipesProvider = () -> {
			throw new RuntimeException("It should not be called because not needed");
		};
		PlayerStateHistoryFactory factory = new PlayerStateHistoryFactory();
		
		// WHEN
		List<PlayerStateHistory> histories = factory.naiveSearch(data, data, recipesProvider).collect(toList());
		
		// THEN
		assertEquals(1, histories.size());
		PlayerStateHistory history = histories.get(0);
		// TODO Test history to ensure it works as intended
		assertEquals(0, history.streamRecipes().count());
	}

}
