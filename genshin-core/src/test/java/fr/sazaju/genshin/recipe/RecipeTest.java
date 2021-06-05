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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import fr.sazaju.genshin.EqualHashCodeTest;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;

class RecipeTest implements EqualHashCodeTest<Recipe> {
	@Override
	public Stream<Comparison<Recipe>> equalityComparisons() {
		ItemState<?> item = mockItem();
		Recipe recipe = Recipe.fromDiff(Map.of(item, 1));
		Recipe identicalRecipe = Recipe.fromDiff(Map.of(item, 1));
		Recipe equivalentRecipe = Recipe.empty().consumes(item, 5).produces(item, 6);
		Recipe differentQuantity = Recipe.fromDiff(Map.of(item, 2));
		Recipe differentItem = Recipe.fromDiff(Map.of(mockItem(), 1));
		Recipe differentContent = Recipe.empty();
		return Stream.of(//
				new Comparison<>(recipe, recipe, true), //
				new Comparison<>(recipe, identicalRecipe, true), //
				new Comparison<>(recipe, equivalentRecipe, true), //
				new Comparison<>(recipe, differentQuantity, false), //
				new Comparison<>(recipe, differentItem, false), //
				new Comparison<>(recipe, differentContent, false), //
				new Comparison<>(recipe, new Object(), false), //
				new Comparison<>(recipe, null, false)//
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 123, -1, -123 })
	void testFromDiffReturnsProvidedQuantity(int quantity) {
		ItemState<?> item = mockItem();
		assertEquals(quantity, Recipe.fromDiff(Map.of(item, quantity)).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testEmptyRecipeIsEmpty(Recipe emptyRecipe) {
		assertTrue(emptyRecipe.isEmpty());
	}

	@Test
	void testNonEmptyRecipeIsNotEmpty() {
		Recipe recipe = Recipe.fromDiff(Map.of(mockItem(), 10));
		assertFalse(recipe.isEmpty());
	}

	static Stream<Map<ItemState<?>, Integer>> testGetDiffReturnsContent() {
		return Stream.of(//
				// Empty
				Map.of(), //

				// Products only
				Map.of(mockItem(), 1), //
				Map.of(mockItem(), 10), //
				Map.of(//
						mockItem(), 1, //
						mockItem(), 10//
				), //

				// Costs only
				Map.of(mockItem(), -1), //
				Map.of(mockItem(), -10), //
				Map.of(//
						mockItem(), -1, //
						mockItem(), -10//
				), //

				// Products and costs
				Map.of(//
						mockItem(), -1, //
						mockItem(), -5, //
						mockItem(), 1, //
						mockItem(), 10//
				)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testGetDiffReturnsContent(Map<ItemState<?>, Integer> map) {
		assertTrue(Recipe.fromDiff(map).getDiff().equals(map));
	}

	@Test
	void testGetDiffDoesNotReturnZeros() {
		Recipe recipe = Recipe.fromDiff(Map.of(//
				mockItem(), 0//
		));
		assertTrue(recipe.getDiff().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("emptyRecipes")
	void testStreamCostsReturnsNothingIfEmpty(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@Test
	void testStreamCostsReturnsRecipeCostsOnly() {
		ItemState<?> cost1 = mockItem();
		ItemState<?> cost2 = mockItem();
		ItemState<?> product1 = mockItem();
		ItemState<?> product2 = mockItem();
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
	void testStreamProductsReturnsNothingIfEmpty(Recipe emptyRecipe) {
		assertEquals(0, emptyRecipe.streamCosts().count());
	}

	@Test
	void testStreamProductsReturnsRecipeProductsOnly() {
		ItemState<?> cost1 = mockItem();
		ItemState<?> cost2 = mockItem();
		ItemState<?> product1 = mockItem();
		ItemState<?> product2 = mockItem();
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
	void testConsumesAddsCost(Recipe recipe, ItemState<?> item, int addedCost) {
		assertEquals(recipe.getQuantity(item) - addedCost, recipe.consumes(item, addedCost).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("recipeUpdates")
	void testProducesAddsProduct(Recipe recipe, ItemState<?> item, int addedCost) {
		assertEquals(recipe.getQuantity(item) + addedCost, recipe.produces(item, addedCost).getQuantity(item));
	}

	static Stream<Integer> itemQuantities() {
		return Stream.of(0, 1, 123);
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testProducedQuantityIsZeroIfConsumed(int quantity) {
		ItemState<?> item = mockItem();
		assertEquals(0, Recipe.empty().consumes(item, quantity).getProducedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testProducedQuantityIsQuantityProduced(int quantity) {
		ItemState<?> item = mockItem();
		assertEquals(quantity, Recipe.empty().produces(item, quantity).getProducedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testConsumedQuantityIsZeroIfProduced(int quantity) {
		ItemState<?> item = mockItem();
		assertEquals(0, Recipe.empty().produces(item, quantity).getConsumedQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemQuantities")
	void testConsumedQuantityIsQuantityConsumed(int quantity) {
		ItemState<?> item = mockItem();
		assertEquals(quantity, Recipe.empty().consumes(item, quantity).getConsumedQuantity(item));
	}

	static Stream<Arguments> testAddSumsRecipes() {
		ItemState<?> cost1 = mockItem();
		ItemState<?> cost2 = mockItem();
		ItemState<?> product1 = mockItem();
		ItemState<?> product2 = mockItem();
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

	static Stream<Arguments> testTimesMultipliesQuantitiesOfItems() {
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
	void testTimesMultipliesQuantitiesOfItems(Integer initialQuantity, int multiplier, Integer finalQuantity) {
		ItemState<?> item = mockItem();
		Map<ItemState<?>, Integer> map = initialQuantity == null ? Map.of() : Map.of(item, initialQuantity);
		assertEquals(finalQuantity, Recipe.fromDiff(map).times(multiplier).getDiff().get(item));
	}

	static Stream<Arguments> testReverseTakesOppositeQuantities() {
		// Zero = item present with zero quantity
		// Null = no item at all
		return Stream.of(//
				arguments(0, null), //
				arguments(null, null), //
				arguments(3, -3), //
				arguments(-3, 3) //
		);
	}

	@ParameterizedTest(name = "{1} becomes {2}")
	@MethodSource
	void testReverseTakesOppositeQuantities(Integer initialQuantity, Integer finalQuantity) {
		ItemState<?> item = mockItem();
		Map<ItemState<?>, Integer> content = initialQuantity == null ? Map.of() : Map.of(item, initialQuantity);
		assertEquals(finalQuantity, Recipe.fromDiff(content).reverse().getDiff().get(item));
	}

	static Stream<Recipe> emptyRecipes() {
		ItemState<?> item = mockItem();
		int quantity = 10;
		return Stream.of(//
				Recipe.empty(), //
				Recipe.fromDiff(Map.of()), //
				Recipe.fromDiff(Map.of(item, 0)), //
				Recipe.empty().consumes(item, quantity).produces(item, quantity)//
		);
	}

	static Stream<Arguments> recipeUpdates() {
		ItemState<?> item = mockItem();

		Recipe emptyRecipe = Recipe.empty();
		Recipe recipeWithoutItem = Recipe.fromDiff(Map.of(//
				mockItem(), 50 //
		));
		Recipe recipeWithItem = Recipe.fromDiff(Map.of(item, 50));

		return Stream.of(//
				arguments(emptyRecipe, item, 1), //
				arguments(emptyRecipe, item, 10), //

				arguments(recipeWithoutItem, item, 1), //
				arguments(recipeWithoutItem, item, 10), //

				arguments(recipeWithItem, item, 1), //
				arguments(recipeWithItem, item, 10)//
		);
	}

	private static ItemState<?> mockItem() {
		return Mockito.mock(ItemState.class);
	}
}
