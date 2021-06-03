package fr.sazaju.genshin.recipe;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.NonStackableItem;
import fr.sazaju.genshin.item.StackableItem;

// TODO Complete test coverage
class RecipeTest {
	static Stream<Arguments> testFromDiffReturnsProvidedQuantity() {
		StackableItem<?> stackableItem = mockStackableItem();
		NonStackableItem<?> nonStackableItem = mockNonStackableItem();
		return Stream.of(//
				arguments(stackableItem, 0),//
				arguments(stackableItem, 1),//
				arguments(stackableItem, 123),//
				arguments(stackableItem, -1),//
				arguments(stackableItem, -123),//
				
				arguments(nonStackableItem, 0),//
				arguments(nonStackableItem, 1),//
				arguments(nonStackableItem, -1)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testFromDiffReturnsProvidedQuantity(Item<?> item, int quantity) {
		assertEquals(quantity, Recipe.fromDiff(Map.of(item, quantity)).getQuantity(item));
	}

	static Stream<Map<Item<?>, Integer>> testGetDiffReturnsContent() {
		return Stream.of(//
				// Empty
				Map.of(), //

				// Products only
				Map.of(mockNonStackableItem(), 1), //
				Map.of(mockStackableItem(), 10), //
				Map.of(//
						mockNonStackableItem(), 1, //
						mockStackableItem(), 10//
				), //

				// Costs only
				Map.of(mockNonStackableItem(), -1), //
				Map.of(mockStackableItem(), -10), //
				Map.of(//
						mockNonStackableItem(), -1, //
						mockStackableItem(), -10//
				), //

				// Products and costs
				Map.of(//
						mockNonStackableItem(), -1, //
						mockStackableItem(), -5, //
						mockNonStackableItem(), 1, //
						mockStackableItem(), 10//
				)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testGetDiffReturnsContent(Map<Item<?>, Integer> map) {
		assertTrue(Recipe.fromDiff(map).getDiff().equals(map));
	}

	@Test
	void testGetDiffDoesNotReturnZeros() {
		Map<Item<?>, Integer> map = Map.of(//
				mockNonStackableItem(), 0, //
				mockStackableItem(), 0//
		);
		assertTrue(Recipe.fromDiff(map).getDiff().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeIsEmpty(Recipe emptyRecipe) {
		assertTrue(emptyRecipe.isEmpty());
	}

	@Test
	void testNonEmptyRecipeIsNotEmpty() {
		Recipe recipe = Recipe.fromDiff(Map.of(mockStackableItem(), 10));
		assertFalse(recipe.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeHasNoCost(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@Test
	void testStreamCostsReturnsRecipeCostsOnly() {
		StackableItem<?> cost1 = mockStackableItem();
		NonStackableItem<?> cost2 = mockNonStackableItem();
		StackableItem<?> product1 = mockStackableItem();
		NonStackableItem<?> product2 = mockNonStackableItem();
		Recipe recipe = Recipe.fromDiff(Map.of(//
				cost1, -10, //
				cost2, -1, //
				product1, 10, //
				product2, 1//
		));
		Set<ItemEntry> costs = Set.of(//
				ItemEntry.of(cost1, 10), //
				ItemEntry.of(cost2, 1)//
		);
		assertEquals(costs, recipe.streamCosts().collect(toSet()));
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeHasNoProducts(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@Test
	void testStreamProductsReturnsRecipeProductsOnly() {
		StackableItem<?> cost1 = mockStackableItem();
		NonStackableItem<?> cost2 = mockNonStackableItem();
		StackableItem<?> product1 = mockStackableItem();
		NonStackableItem<?> product2 = mockNonStackableItem();
		Recipe recipe = Recipe.fromDiff(Map.of(//
				cost1, -10, //
				cost2, -1, //
				product1, 10, //
				product2, 1//
		));
		Set<ItemEntry> products = Set.of(//
				ItemEntry.of(product1, 10), //
				ItemEntry.of(product2, 1)//
		);
		assertEquals(products, recipe.streamProducts().collect(toSet()));
	}

	@ParameterizedTest
	@MethodSource("recipeUpdates")
	void testConsumesAddsCost(Recipe recipe, Item<?> item, int addedCost) {
		assertEquals(recipe.getQuantity(item) - addedCost, recipe.consumes(item, addedCost).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("recipeUpdates")
	void testProducesAddsProduct(Recipe recipe, Item<?> item, int addedCost) {
		assertEquals(recipe.getQuantity(item) + addedCost, recipe.produces(item, addedCost).getQuantity(item));
	}

	static Stream<Arguments> itemQuantities() {
		StackableItem<?> stackableItem = mockStackableItem();
		NonStackableItem<?> nonStackableItem = mockNonStackableItem();
		return Stream.of(//
				arguments(stackableItem, 0), //
				arguments(stackableItem, 1), //
				arguments(stackableItem, 123), //

				arguments(nonStackableItem, 0), //
				arguments(nonStackableItem, 1)//
		);
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testProducedQuantityIsZeroIfConsumed(Item<?> item, int quantity) {
		assertEquals(0, Recipe.empty().consumes(item, quantity).getProducedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testProducedQuantityIsQuantityProduced(Item<?> item, int quantity) {
		assertEquals(quantity, Recipe.empty().produces(item, quantity).getProducedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testConsumedQuantityIsZeroIfProduced(Item<?> item, int quantity) {
		assertEquals(0, Recipe.empty().produces(item, quantity).getConsumedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testConsumedQuantityIsQuantityConsumed(Item<?> item, int quantity) {
		assertEquals(quantity, Recipe.empty().consumes(item, quantity).getConsumedQuantity(item));
	}

	static Stream<Arguments> testAddSumsRecipes() {
		NonStackableItem<?> cost1 = mockNonStackableItem();
		StackableItem<?> cost2 = mockStackableItem();
		NonStackableItem<?> product1 = mockNonStackableItem();
		StackableItem<?> product2 = mockStackableItem();
		Recipe emptyRecipe = Recipe.empty();
		Recipe costsOnlyRecipe = Recipe.fromDiff(Map.of(//
				cost1, -1, //
				cost2, -10 //
		));
		Recipe productsOnlyRecipe = Recipe.fromDiff(Map.of(//
				product1, 1, //
				product2, 10//
		));
		Recipe mix1Recipe = Recipe.fromDiff(Map.of(//
				cost1, -1, //
				product2, 10//
		));
		Recipe mix2Recipe = Recipe.fromDiff(Map.of(//
				cost2, -10, //
				product1, 1 //
		));
		Recipe half1Recipe = Recipe.fromDiff(Map.of(//
				cost1, -1, //
				cost2, -3, //
				product1, 0, //
				product2, 4//
		));
		Recipe half2Recipe = Recipe.fromDiff(Map.of(//
				cost1, 0, //
				cost2, -7, //
				product1, 1, //
				product2, 6//
		));
		Recipe completeRecipe = Recipe.fromDiff(Map.of(//
				cost1, -1, //
				cost2, -10, //
				product1, 1, //
				product2, 10//
		));
		return Stream.of(//
				arguments(emptyRecipe, emptyRecipe, emptyRecipe), //
				arguments(completeRecipe, emptyRecipe, completeRecipe), //
				arguments(emptyRecipe, completeRecipe, completeRecipe), //
				arguments(costsOnlyRecipe, productsOnlyRecipe, completeRecipe), //
				arguments(mix1Recipe, mix2Recipe, completeRecipe), //
				arguments(half1Recipe, half2Recipe, completeRecipe)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testAddSumsRecipes(Recipe recipe1, Recipe recipe2, Recipe recipeSum) {
		assertEquals(recipeSum, recipe1.add(recipe2));
	}

	static Stream<Arguments> testTimesMultipliesQuantitiesOfStackableItems() {
		// Zero = item present with zero quantity
		// Null = no item at all
		return Stream.of(//
				// Zero multiplier
				arguments(0, 0, null), //
				arguments(null, 0, null), //
				arguments(2, 0, null), //

				// Positive multipliers
				arguments(0, 3, null), //
				arguments(null, 3, null), //
				arguments(2, 1, 2), //
				arguments(1, 3, 3), //
				arguments(3, 2, 6), //
				arguments(-3, 2, -6), //

				// Negative multipliers
				arguments(0, -3, null), //
				arguments(null, -3, null), //
				arguments(3, -1, -3), //
				arguments(3, -2, -6), //
				arguments(-3, -2, 6)//
		);
	}

	@ParameterizedTest(name = "{0} x {1} = {2}")
	@MethodSource
	void testTimesMultipliesQuantitiesOfStackableItems(Integer initialQuantity, int multiplier, Integer finalQuantity) {
		StackableItem<?> item = mockStackableItem();
		Map<Item<?>, Integer> content = initialQuantity == null ? Map.of() : Map.of(item, initialQuantity);
		assertEquals(finalQuantity, Recipe.fromDiff(content).times(multiplier).getDiff().get(item));
	}

	static Stream<Arguments> testTimesMultipliesQuantitiesOfNonStackableItems() {
		// Zero = item present with zero quantity
		// Null = no item at all
		return Stream.of(//
				// Zero multiplier
				arguments(0, 0, null, 0), //
				arguments(null, 0, null, 0), //
				arguments(1, 0, null, 0), //

				// Positive multipliers
				arguments(0, 3, null, 0), //
				arguments(null, 3, null, 0), //
				arguments(1, 1, 1, 1), //
				arguments(1, 3, 1, 3), //
				arguments(-1, 2, -1, 2), //

				// Negative multipliers
				arguments(0, -3, null, 0), //
				arguments(null, -3, null, 0), //
				arguments(1, -1, -1, 1), //
				arguments(1, -2, -1, 2), //
				arguments(-1, -2, 1, 2)//
		);
	}

	@ParameterizedTest(name = "item at {0} x {1} = {3} items at {2}")
	@MethodSource
	void testTimesMultipliesQuantitiesOfNonStackableItems(Integer initialQuantity, int multiplier,
			Integer finalQuantity, int occurrences) {
		NonStackableItem<?> item = mockNonStackableItem();
		Map<Item<?>, Integer> content = initialQuantity == null ? Map.of() : Map.of(item, initialQuantity);
		Recipe recipe = Recipe.fromDiff(content).times(multiplier);
		assertEquals(occurrences, recipe.getDiff().keySet().size());
		assertEquals(finalQuantity, recipe.getDiff().get(item));// TODO Check each item
	}

	static Stream<Arguments> testReverseTakesOppositeQuantities() {
		NonStackableItem<?> nonStackableItem = mockNonStackableItem();
		StackableItem<?> stackableItem = mockStackableItem();
		// Zero = item present with zero quantity
		// Null = no item at all
		return Stream.of(//
				// Stackable item
				arguments(stackableItem, 0, null), //
				arguments(stackableItem, null, null), //
				arguments(stackableItem, 3, -3), //
				arguments(stackableItem, -3, 3), //

				// Non stackable item
				arguments(nonStackableItem, 0, null), //
				arguments(nonStackableItem, null, null), //
				arguments(nonStackableItem, 1, -1), //
				arguments(nonStackableItem, -1, 1)//
		);
	}

	@ParameterizedTest(name = "{0} x {1} => {2}")
	@MethodSource
	void testReverseTakesOppositeQuantities(Item<?> item, Integer initialQuantity, Integer finalQuantity) {
		Map<Item<?>, Integer> content = initialQuantity == null ? Map.of() : Map.of(item, initialQuantity);
		assertEquals(finalQuantity, Recipe.fromDiff(content).reverse().getDiff().get(item));
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
		NonStackableItem<?> nonStackableItem = mockNonStackableItem();

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

	private static NonStackableItem<?> mockNonStackableItem() {
		NonStackableItem<?> item = Mockito.mock(NonStackableItem.class, "non stackable item");
		Mockito.when(item.duplicate()).then(invocation -> mockNonStackableItem());
		return item;
	}

	private static StackableItem<?> mockStackableItem() {
		return Mockito.mock(StackableItem.class, "stackable item");
	}
}
