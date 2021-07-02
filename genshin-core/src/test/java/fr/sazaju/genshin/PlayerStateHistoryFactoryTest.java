package fr.sazaju.genshin;

import static fr.sazaju.genshin.Rarity.*;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.PlayerStateHistoryFactory.RecipesProvider;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.recipe.Recipe;

class PlayerStateHistoryFactoryTest {

	static Stream<PlayerState> testNaiveSearchReturnsOnlyEmptyHistoryForSameTargetThanSource() {
		return Stream.of(//
				PlayerState.empty(), //
				PlayerState.fromMap(Map.of(mockItem(), 123)), //
				PlayerState.fromMap(Map.of(//
						mockItem(), 123, //
						mockItem(), 456, //
						mockItem(), 789//
				))//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testNaiveSearchReturnsOnlyEmptyHistoryForSameTargetThanSource(PlayerState state) {
		// GIVEN
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

	static Stream<Arguments> testNaiveSearchReturnsAllHistoriesWhenSeveralPossibleRecipesSequences() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		PlayerState emptyState = PlayerState.empty();

		List<Arguments> cases = new LinkedList<>();
		{
			// Simplest case of non-empty history
			PlayerState target = PlayerState.fromMap(Map.of(item1, 123));
			Recipe recipe1 = Recipe.fromDiff(Map.of(item1, 123));
			RecipesProvider recipesProvider = name(() -> Stream.of(recipe1));
			Set<PlayerStateHistory> possibleHistories = new HashSet<>();
			possibleHistories.add(//
					PlayerStateHistory//
							.fromState(emptyState)//
							.appendRecipe(recipe1)//
			);
			cases.add(arguments(emptyState, target, name(recipesProvider), possibleHistories));
		}
		{
			// Constrained recipes which can only be taken in one order
			PlayerState target = PlayerState.fromMap(Map.of(item1, 1, item2, 1, item3, 1));
			Recipe recipe1 = Recipe.fromDiff(Map.of(item1, 2));
			Recipe recipe2 = Recipe.fromDiff(Map.of(item1, -1, item2, 2));
			Recipe recipe3 = Recipe.fromDiff(Map.of(item2, -1, item3, 1));
			RecipesProvider recipesProvider = name(() -> Stream.of(recipe1, recipe2, recipe3));
			Set<PlayerStateHistory> possibleHistories = new HashSet<>();
			possibleHistories.add(PlayerStateHistory//
					.fromState(emptyState)//
					.appendRecipe(recipe1)//
					.appendRecipe(recipe2)//
					.appendRecipe(recipe3)//
			);
			cases.add(arguments(emptyState, target, name(recipesProvider), possibleHistories));
		}
		{
			// TOOD Support this case
			// Constrained recipes where we need to guess intermediary steps
			PlayerState target = PlayerState.fromMap(Map.of(item3, 1));
			Recipe recipe1 = Recipe.fromDiff(Map.of(item1, 1));
			Recipe recipe2 = Recipe.fromDiff(Map.of(item1, -1, item2, 1));
			Recipe recipe3 = Recipe.fromDiff(Map.of(item2, -1, item3, 1));
			RecipesProvider recipesProvider = name(() -> Stream.of(recipe1, recipe2, recipe3));
			Set<PlayerStateHistory> possibleHistories = new HashSet<>();
			possibleHistories.add(PlayerStateHistory//
					.fromState(emptyState)//
					.appendRecipe(recipe1)//
					.appendRecipe(recipe2)//
					.appendRecipe(recipe3)//
			);
			cases.add(arguments(emptyState, target, name(recipesProvider), possibleHistories));
		}
		{
			// Unconstrained recipes which can be taken in any order
			PlayerState target = PlayerState.fromMap(Map.of(item1, 123, item2, 456, item3, 789));
			Recipe recipe1 = Recipe.fromDiff(Map.of(item1, 123));
			Recipe recipe2 = Recipe.fromDiff(Map.of(item2, 456));
			Recipe recipe3 = Recipe.fromDiff(Map.of(item3, 789));
			RecipesProvider recipesProvider = name(() -> Stream.of(recipe1, recipe2, recipe3));
			Set<PlayerStateHistory> possibleHistories = new HashSet<>();
			// Add all possible combinations
			Stream.of(recipe1, recipe2, recipe3).forEach(first -> {
				Stream.of(recipe1, recipe2, recipe3).filter(except(first)).forEach(second -> {
					Stream.of(recipe1, recipe2, recipe3).filter(except(first, second)).forEach(third -> {
						possibleHistories.add(PlayerStateHistory//
								.fromState(emptyState)//
								.appendRecipe(first)//
								.appendRecipe(second)//
								.appendRecipe(third)//
						);
					});
				});
			});
			cases.add(arguments(emptyState, target, name(recipesProvider), possibleHistories));
		}
		return cases.stream();
	}

	@ParameterizedTest(name = "From {0} to {1} with recipes {2} should provide histories {3}")
	@MethodSource
	@Disabled
	void testNaiveSearchReturnsAllHistoriesWhenSeveralPossibleRecipesSequences(PlayerState source, PlayerState target,
			RecipesProvider recipesProvider, Set<PlayerStateHistory> possibleHistories) {
		// GIVEN
		PlayerStateHistoryFactory factory = new PlayerStateHistoryFactory(name(recipesProvider));

		// WHEN
		Stream<PlayerStateHistory> search = factory.naiveSearch(source, target);

		// THEN
		Set<PlayerStateHistory> histories = search.collect(toSet());
		assertEquals(possibleHistories, histories);
	}

	// TODO Complete coverage

	private static ItemState<?> mockItem() {
		ItemType mockItemType = new ItemType() {

			@Override
			public boolean hasRarity(Rarity rarity) {
				return true;
			}
		};
		return new ItemState<ItemType>(mockItemType, ONE_STAR) {
			@Override
			public String toString() {
				// Append some random content to differentiate them
				return "mock" + Math.abs(hashCode()) % 1000;
			}
		};
	}

	private static Predicate<? super Recipe> except(Recipe... rejected) {
		return recipe -> !Arrays.asList(rejected).contains(recipe);
	}

	private static RecipesProvider name(RecipesProvider recipesProvider) {
		return new RecipesProvider() {

			@Override
			public Stream<Recipe> streamRecipes() {
				return recipesProvider.streamRecipes();
			}

			@Override
			public String toString() {
				return streamRecipes().collect(Collectors.toList()).toString();
			}
		};
	}
}
