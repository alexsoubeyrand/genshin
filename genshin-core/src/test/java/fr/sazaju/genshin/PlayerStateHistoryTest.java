package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.simple.Mora;
import fr.sazaju.genshin.recipe.Recipe;

@TestInstance(Lifecycle.PER_CLASS)
interface PlayerStateHistoryTest {

	Stream<PlayerStateHistory> histories();

	default Stream<PlayerStateHistory> historiesWithRecipe() {
		return histories().filter(history -> !history.getResultingRecipe().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("histories")
	default void testEqualsIsTrueOnSameHistory(PlayerStateHistory history) {
		assertTrue(history.equals(history));
	}

	@ParameterizedTest
	@MethodSource("histories")
	default void testEqualsIsTrueOnHistoryWithSameRecipesSequence(PlayerStateHistory history) {
		assertTrue(history.equals(history));
	}

	@ParameterizedTest
	@MethodSource("histories")
	default void testResultingDataIsInitialDataWithResultingRecipe(PlayerStateHistory history) {
		PlayerState initialData = history.getInitialData();
		Recipe recipe = history.getResultingRecipe();
		PlayerState resultingData = history.getResultingData();
		PlayerState expectedData = initialData
				.update(recipe.getDiff().entrySet().stream().map(ItemEntry::fromMapEntry));
		assertEquals(expectedData, resultingData);
	}

	static class FromPlayerData implements PlayerStateHistoryTest {

		@Override
		public Stream<PlayerStateHistory> histories() {
			PlayerState emptyData = PlayerState.empty();
			return Stream.of(//
					PlayerStateHistory.from(emptyData), //
					PlayerStateHistory.from(emptyData.update(Stream.of(ItemEntry.of(Mora.MORA.itemState(), 10))))//
			);
		}

	}
}
