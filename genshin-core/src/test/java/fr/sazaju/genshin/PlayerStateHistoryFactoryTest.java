package fr.sazaju.genshin;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.sazaju.genshin.item.ItemState;

class PlayerStateHistoryFactoryTest {

	@Test
	void testNaiveSearchReturnsOnlyEmptyHistoryForSameTargetThanSource() {
		// GIVEN
		PlayerState state = mockPlayerState();
		PlayerStateHistoryFactory factory = new PlayerStateHistoryFactory(() -> {
			throw new RuntimeException("It should not require recipes");
		});

		// WHEN
		Stream<PlayerStateHistory> search = factory.naiveSearch(state, state);

		// THEN
		List<PlayerStateHistory> histories = search.collect(toList());
		assertEquals(1, histories.size());
		assertEquals(List.of(state), histories.get(0).streamStates().collect(Collectors.toList()));
	}

	// TODO Complete coverage

	private static ItemState<?> mockItem() {
		return Mockito.mock(ItemState.class);
	}

	private static PlayerState mockPlayerState() {
		return PlayerState.fromMap(Map.of(//
				mockItem(), 123, //
				mockItem(), 456, //
				mockItem(), 789//
		));
	}
}
