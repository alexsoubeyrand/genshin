package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.item.simple.Mora;
import fr.sazaju.genshin.recipe.Recipe;

@TestInstance(Lifecycle.PER_CLASS)
interface PlayerDataHistoryTest {

	Stream<PlayerDataHistory> histories();
	default Stream<PlayerDataHistory> historiesWithRecipe() {
		return histories().filter(history -> !history.getResultingRecipe().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("histories")
	default void testEqualsIsTrueOnSameHistory(PlayerDataHistory history) {
		assertTrue(history.equals(history));
	}
	
	

	@ParameterizedTest
	@MethodSource("histories")
	default void testEqualsIsTrueOnHistoryWithSameRecipesSequence(PlayerDataHistory history) {
		assertTrue(history.equals(history));
	}

	@ParameterizedTest
	@MethodSource("histories")
	default void testResultingDataIsInitialDataWithResultingRecipe(PlayerDataHistory history) {
		PlayerData initialData = history.getInitialData();
		Recipe recipe = history.getResultingRecipe();
		PlayerData resultingData = history.getResultingData();
		PlayerData expectedData = initialData.update(recipe);
		assertEquals(expectedData, resultingData);
	}

	static class FromPlayerData implements PlayerDataHistoryTest {

		@Override
		public Stream<PlayerDataHistory> histories() {
			PlayerData emptyData = PlayerData.empty();
			return Stream.of(//
					PlayerDataHistory.from(emptyData), //
					PlayerDataHistory.from(emptyData.update(Mora.MORA.itemState(), 10))//
			);
		}

	}
}
