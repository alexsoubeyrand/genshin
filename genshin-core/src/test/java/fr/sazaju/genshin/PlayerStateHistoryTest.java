package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.recipe.Recipe;

@TestInstance(Lifecycle.PER_CLASS)
class PlayerStateHistoryTest implements EqualHashCodeTest<PlayerStateHistory> {

	@Override
	public Stream<Comparison<PlayerStateHistory>> equalityComparisons() {
		PlayerState state1 = PlayerState.fromMap(Map.of(mockItem(), 123));
		PlayerStateHistory minimalHistory = PlayerStateHistory.fromState(state1);
		PlayerState state2 = PlayerState.fromMap(Map.of(mockItem(), 456));
		PlayerStateHistory smallHistory = minimalHistory.appendState(state2);
		PlayerState state3 = PlayerState.fromMap(Map.of(mockItem(), 789));
		PlayerStateHistory greatHistory = smallHistory.appendState(state3);
		PlayerStateHistory equivalentHistory = minimalHistory.appendState(state2).appendState(state3);
		return Stream.of(//
				new Comparison<>(minimalHistory, minimalHistory, true), //
				new Comparison<>(smallHistory, smallHistory, true), //
				new Comparison<>(greatHistory, greatHistory, true), //
				new Comparison<>(greatHistory, equivalentHistory, true), //

				new Comparison<>(greatHistory, smallHistory, false), //
				new Comparison<>(smallHistory, greatHistory, false), //
				new Comparison<>(greatHistory, new Object(), false), //
				new Comparison<>(greatHistory, null, false)//
		);
	}

	@Test
	void testHistoryFromStateReturnsProvidedStateAsInitialState() {
		PlayerState state = mockPlayerState();
		assertEquals(state, PlayerStateHistory.fromState(state).getInitialState());
	}

	@Test
	void testHistoryFromStateReturnsProvidedStateAsResultingState() {
		PlayerState state = mockPlayerState();
		assertEquals(state, PlayerStateHistory.fromState(state).getResultingState());
	}

	@Test
	void testHistoryFromStateReturnsEmptyResultingRecipe() {
		PlayerState state = mockPlayerState();
		assertEquals(Recipe.empty(), PlayerStateHistory.fromState(state).getResultingRecipe());
	}

	@Test
	void testHistoryFromStateStreamsProvidedState() {
		PlayerState state = mockPlayerState();
		PlayerStateHistory history = PlayerStateHistory.fromState(state);
		List<PlayerState> states = history.streamStates().collect(Collectors.toList());
		assertEquals(List.of(state), states);
	}

	@Test
	void testHistoryFromStateStreamsNoRecipe() {
		PlayerState state = mockPlayerState();
		PlayerStateHistory history = PlayerStateHistory.fromState(state);
		List<Recipe> recipes = history.streamRecipes().collect(Collectors.toList());
		assertEquals(List.of(), recipes);
	}

	@Test
	void testAppendStateKeepsInitialState() {
		PlayerState state1 = PlayerState.fromMap(Map.of(mockItem(), 123));
		PlayerState state2 = PlayerState.fromMap(Map.of(mockItem(), 456));
		PlayerState state3 = PlayerState.fromMap(Map.of(mockItem(), 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendState(state2)//
				.appendState(state3);
		assertEquals(state1, history.getInitialState());
	}

	@Test
	void testAppendStateUpdatesResultingState() {
		PlayerState state1 = PlayerState.fromMap(Map.of(mockItem(), 123));
		PlayerState state2 = PlayerState.fromMap(Map.of(mockItem(), 456));
		PlayerState state3 = PlayerState.fromMap(Map.of(mockItem(), 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendState(state2)//
				.appendState(state3);
		assertEquals(state3, history.getResultingState());
	}

	@Test
	void testAppendStateUpdatesResultingRecipe() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		PlayerState state2 = PlayerState.fromMap(Map.of(item2, 456));
		PlayerState state3 = PlayerState.fromMap(Map.of(item3, 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendState(state2)//
				.appendState(state3);
		Recipe recipe = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item3, 789//
		));
		assertEquals(recipe, history.getResultingRecipe());
	}

	@Test
	void testAppendStateAppendsNewStateInStreamStates() {
		PlayerState state1 = PlayerState.fromMap(Map.of(mockItem(), 123));
		PlayerState state2 = PlayerState.fromMap(Map.of(mockItem(), 456));
		PlayerState state3 = PlayerState.fromMap(Map.of(mockItem(), 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendState(state2)//
				.appendState(state3);
		List<PlayerState> states = history.streamStates().collect(Collectors.toList());
		assertEquals(List.of(state1, state2, state3), states);
	}

	@Test
	void testAppendStateAppendsRecipeInStreamRecipe() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		PlayerState state2 = PlayerState.fromMap(Map.of(item2, 456));
		PlayerState state3 = PlayerState.fromMap(Map.of(item3, 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendState(state2)//
				.appendState(state3);
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		List<Recipe> recipes = history.streamRecipes().collect(Collectors.toList());
		assertEquals(List.of(recipe1, recipe2), recipes);
	}

	@Test
	void testAppendRecipeKeepsInitialState() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendRecipe(recipe1)//
				.appendRecipe(recipe2);
		assertEquals(state1, history.getInitialState());
	}

	@Test
	void testAppendRecipeUpdatesResultingState() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		PlayerState state3 = PlayerState.fromMap(Map.of(item3, 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendRecipe(recipe1)//
				.appendRecipe(recipe2);
		assertEquals(state3, history.getResultingState());
	}

	@Test
	void testAppendRecipeUpdatesResultingRecipe() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendRecipe(recipe1)//
				.appendRecipe(recipe2);
		assertEquals(recipe1.add(recipe2), history.getResultingRecipe());
	}

	@Test
	void testAppendRecipeAppendsResultingStateInStreamStates() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		PlayerState state2 = PlayerState.fromMap(Map.of(item2, 456));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		PlayerState state3 = PlayerState.fromMap(Map.of(item3, 789));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendRecipe(recipe1)//
				.appendRecipe(recipe2);
		List<PlayerState> states = history.streamStates().collect(Collectors.toList());
		assertEquals(List.of(state1, state2, state3), states);
	}

	@Test
	void testAppendRecipeAppendsRecipeInStreamRecipe() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState state1 = PlayerState.fromMap(Map.of(item1, 123));
		Recipe recipe1 = Recipe.fromDiff(Map.of(//
				item1, -123, //
				item2, 456//
		));
		Recipe recipe2 = Recipe.fromDiff(Map.of(//
				item2, -456, //
				item3, 789//
		));
		PlayerStateHistory history = PlayerStateHistory//
				.fromState(state1)//
				.appendRecipe(recipe1)//
				.appendRecipe(recipe2);
		List<Recipe> recipes = history.streamRecipes().collect(Collectors.toList());
		assertEquals(List.of(recipe1, recipe2), recipes);
	}

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
