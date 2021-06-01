package fr.sazaju.genshin.recipe;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.StackableItem;

// TODO Complete test coverage
class RecipeTest {

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeHasNoCost(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeHasNoProducts(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeIsEmpty(Recipe emptyRecipe) {
		assertTrue(emptyRecipe.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("recipeUpdates")
	void testConsumesAddsCost(Recipe recipe, Item<?> item, int addedCost) {
		assertEquals(recipe.getConsumedQuantity(item) + addedCost,
				recipe.consumes(item, addedCost).getConsumedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("recipeUpdates")
	void testProducesAddsProduct(Recipe recipe, Item<?> item, int addedCost) {
		assertEquals(recipe.getProducedQuantity(item) + addedCost,
				recipe.produces(item, addedCost).getProducedQuantity(item));
	}

	static Stream<Arguments> testProducedIsOppositeOfConsumed() {
		StackableItem<?> stackableItem = mockStackableItem();
		Item<?> nonStackableItem = mockNonStackableItem();
		Recipe emptyRecipe = Recipe.empty();
		return Stream.of(//
				arguments(emptyRecipe, stackableItem), //
				arguments(emptyRecipe.consumes(stackableItem, 10), stackableItem), //
				arguments(emptyRecipe.produces(stackableItem, 10), stackableItem), //
				arguments(emptyRecipe.consumes(nonStackableItem, 1), nonStackableItem), //
				arguments(emptyRecipe.produces(nonStackableItem, 1), nonStackableItem)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testProducedIsOppositeOfConsumed(Recipe recipe, Item<?> item) {
		assertEquals(-recipe.getProducedQuantity(item), recipe.getConsumedQuantity(item));
	}

	static Stream<Recipe> emptyRecipes() {
		StackableItem<?> item = mockStackableItem();
		int quantity = 10;
		return Stream.of(//
				Recipe.empty(), //
				Recipe.fromDiff(Map.of()), //
				Recipe.fromDiff(Map.of(item, 0)), //
				Recipe.empty().consumes(item, quantity).produces(item, quantity)//
		);
	}

	static Stream<Arguments> recipeUpdates() {
		StackableItem<?> stackableItem = mockStackableItem();
		Item<?> nonStackableItem = mockNonStackableItem();

		Recipe emptyRecipe = Recipe.empty();
		Recipe recipeWithoutItem = Recipe.fromDiff(Map.of(//
				mockStackableItem(), 50, //
				mockNonStackableItem(), 1//
		));
		Recipe recipeWithItem = Recipe.fromDiff(Map.of(stackableItem, 50));

		return Stream.of(//
				arguments(emptyRecipe, stackableItem, 1), //
				arguments(emptyRecipe, stackableItem, 10), //
				arguments(emptyRecipe, nonStackableItem, 1), //

				arguments(recipeWithoutItem, stackableItem, 1), //
				arguments(recipeWithoutItem, stackableItem, 10), //
				arguments(recipeWithoutItem, nonStackableItem, 1), //

				arguments(recipeWithItem, stackableItem, 1), //
				arguments(recipeWithItem, stackableItem, 10)//
		);
	}

	private static Item<?> mockNonStackableItem() {
		return Mockito.mock(Item.class, "non stackable item");
	}

	private static StackableItem<?> mockStackableItem() {
		return Mockito.mock(StackableItem.class, "stackable item");
	}
}
