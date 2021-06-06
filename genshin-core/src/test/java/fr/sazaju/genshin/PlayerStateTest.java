package fr.sazaju.genshin;

import static fr.sazaju.genshin.Rarity.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.PlayerState.NotEnoughItemsException;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

class PlayerStateTest implements EqualHashCodeTest<PlayerState> {

	@Override
	public Stream<Comparison<PlayerState>> equalityComparisons() {
		ItemState<?> item = mockItem();
		PlayerState state = PlayerState.fromMap(Map.of(item, 1));
		PlayerState identicalState = PlayerState.fromMap(Map.of(item, 1));
		PlayerState equivalentState = PlayerState.empty()//
				.update(Stream.of(ItemEntry.of(item, 5)))//
				.update(Stream.of(ItemEntry.of(item, -4)));
		PlayerState differentQuantity = PlayerState.fromMap(Map.of(item, 2));
		PlayerState differentItem = PlayerState.fromMap(Map.of(mockItem(), 1));
		PlayerState differentContent = PlayerState.empty();
		return Stream.of(//
				new Comparison<>(state, state, true), //
				new Comparison<>(state, identicalState, true), //
				new Comparison<>(state, equivalentState, true), //
				new Comparison<>(state, differentQuantity, false), //
				new Comparison<>(state, differentItem, false), //
				new Comparison<>(state, differentContent, false), //
				new Comparison<>(state, new Object(), false), //
				new Comparison<>(state, null, false)//
		);
	}

	@ParameterizedTest
	@MethodSource("emptyStates")
	void testEmptyStateIsEmpty(Supplier<PlayerState> emptyState) {
		assertTrue(emptyState.get().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("nonEmptyStates")
	void testNonEmptyStateIsNotEmpty(Supplier<PlayerState> nonEmptyState) {
		assertFalse(nonEmptyState.get().isEmpty());
	}

	@ParameterizedTest
	@MethodSource("emptyStates")
	void testGetQuantityReturnsContentQuantity(Supplier<PlayerState> emptyState) {
		assertEquals(0, emptyState.get().getQuantity(mockItem()));
	}

	@ParameterizedTest
	@MethodSource("states")
	void testGetQuantityReturnsContentQuantity(Supplier<PlayerState> state, Content content) {
		content.map().entrySet().forEach(entry -> {
			ItemState<?> item = entry.getKey();
			Integer quantity = entry.getValue();
			assertEquals(quantity, state.get().getQuantity(item), item.toString());
		});
	}

	@ParameterizedTest
	@MethodSource("states")
	void testGetQuantityReturnsZeroForUnknownContent(Supplier<PlayerState> state) {
		assertEquals(0, state.get().getQuantity(mockItem()));
	}

	@ParameterizedTest
	@MethodSource("states")
	void testStateContainsItsContent(Supplier<PlayerState> state, Content content) {
		assertTrue(state.get().contains(content.set().stream()));
	}

	static Stream<Arguments> testStateDoesNotContainMoreThanItsContent() {
		ItemState<ItemType> unknownItem = mockItem();
		return states().flatMap(args -> {
			@SuppressWarnings("unchecked")
			Supplier<PlayerState> state = (Supplier<PlayerState>) args.get()[0];
			Content content = (Content) args.get()[1];
			Supplier<PlayerState> renamedState = name(state + " " + content, state);

			Map<ItemState<?>, Integer> actualContent = content.map();
			Stream<Map<ItemState<?>, Integer>> tooHighContents = content.set().stream().map(entry -> {
				Map<ItemState<?>, Integer> invalidContent = new HashMap<>(actualContent);
				ItemState<?> item = entry.getItem();
				int actualQuantity = entry.getQuantity();
				invalidContent.put(item, actualQuantity + 1);
				return invalidContent;
			});

			Map<ItemState<?>, Integer> unknownContent = new HashMap<>(actualContent);
			unknownContent.put(unknownItem, 1);

			return Stream.concat(tooHighContents, Stream.of(unknownContent))//
					.map(map -> map.entrySet().stream().map(ItemEntry::fromMapEntry).collect(Collectors.toList()))
					.flatMap(entries -> Stream.of(arguments(renamedState, entries)));
		});
	}

	@ParameterizedTest
	@MethodSource
	void testStateDoesNotContainMoreThanItsContent(Supplier<PlayerState> state, List<ItemEntry> invalidContent) {
		assertFalse(state.get().contains(invalidContent.stream()));
	}

	@ParameterizedTest
	@MethodSource("states")
	void testStateStreamsItsContent(Supplier<PlayerState> state, Content content) {
		assertEquals(content.set(), state.get().stream().collect(Collectors.toSet()));
	}

	@ParameterizedTest
	@MethodSource("validUpdates")
	void testUpdateChangesItemsQuantity(Supplier<PlayerState> state, List<ItemEntry> updates) {
		PlayerState initialState = state.get();
		PlayerState updatedState = initialState.update(updates.stream());

		updates.forEach(update -> {
			ItemState<?> item = update.getItem();
			int quantity = initialState.getQuantity(item);
			assertEquals(quantity + update.getQuantity(), updatedState.getQuantity(item), item.toString());
		});
	}

	@ParameterizedTest
	@MethodSource("validUpdates")
	void testUpdateDoesNotChangeOtherItemsQuantity(Supplier<PlayerState> state, List<ItemEntry> updates) {
		ItemState<ItemType> otherItem = mockItem();
		PlayerState initialState = state.get().update(Stream.of(ItemEntry.of(otherItem, 123)));
		PlayerState updatedState = initialState.update(updates.stream());
		assertEquals(123, updatedState.getQuantity(otherItem));
	}

	@ParameterizedTest
	@MethodSource("invalidUpdates")
	void testUpdateRejectsRemovingMoreThanAvailable(Supplier<PlayerState> state, List<ItemEntry> updates) {
		assertThrows(NotEnoughItemsException.class, () -> state.get().update(updates.stream()));
	}

	@ParameterizedTest
	@MethodSource("states")
	void testIteratorReturnsEachEntryOnce(Supplier<PlayerState> state, Content content) {
		Iterator<ItemEntry> iterator = state.get().iterator();
		List<ItemEntry> entries = new LinkedList<>();
		while (iterator.hasNext()) {
			entries.add(iterator.next());
		}
		assertEquals(new HashSet<>(entries).size(), entries.size());
	}

	@ParameterizedTest
	@MethodSource("states")
	void testIteratorReturnsAllContent(Supplier<PlayerState> state, Content content) {
		Iterator<ItemEntry> iterator = state.get().iterator();
		Set<ItemEntry> entries = new HashSet<>();
		while (iterator.hasNext()) {
			entries.add(iterator.next());
		}
		assertEquals(content.set(), entries);
	}

	public static interface Content {

		Map<ItemState<?>, Integer> map();

		Set<ItemEntry> set();

		static Content from(Map<ItemState<?>, Integer> map) {
			return new Content() {
				@Override
				public Map<ItemState<?>, Integer> map() {
					return map;
				}

				@Override
				public Set<ItemEntry> set() {
					return map.entrySet().stream().map(ItemEntry::fromMapEntry).collect(Collectors.toSet());
				}

				@Override
				public String toString() {
					return map.toString();
				}
			};
		}

		static Content from(Set<ItemEntry> set) {
			return new Content() {

				@Override
				public Map<ItemState<?>, Integer> map() {
					return set.stream().collect(Collectors.toMap(//
							entry -> entry.getItem(), //
							entry -> entry.getQuantity()//
					));
				}

				@Override
				public Set<ItemEntry> set() {
					return set;
				}

				@Override
				public String toString() {
					return set.toString();
				}
			};
		}

		static Content empty() {
			return from(Set.of());
		}

	}

	static Stream<Arguments> states() {
		ItemState<ItemType> item = mockItem();
		Map<ItemState<?>, Integer> map = Map.of(item, 123);
		Set<ItemEntry> set = Set.of(ItemEntry.of(item, 456));
		return Stream.of(//
				// Empty cases
				arguments(name("empty state", () -> PlayerState.empty()), Content.empty()), //
				arguments(name("map", () -> PlayerState.fromMap(Map.of())), Content.empty()), //
				arguments(name("entries", () -> PlayerState.fromItemEntries(Stream.empty())), Content.empty()), //

				// Non empty cases
				arguments(name("map", () -> PlayerState.fromMap(map)), Content.from(map)), //
				arguments(name("entries", () -> PlayerState.fromItemEntries(set.stream())), Content.from(set))//
		);
	}

	static Stream<Arguments> emptyStates() {
		return states().filter(args -> ((Content) args.get()[1]).set().isEmpty());
	}

	static Stream<Arguments> nonEmptyStates() {
		return states().filter(args -> !((Content) args.get()[1]).set().isEmpty());
	}

	static Stream<Arguments> validUpdates() {
		return states().flatMap(args -> {
			@SuppressWarnings("unchecked")
			Supplier<PlayerState> state = (Supplier<PlayerState>) args.get()[0];
			Content content = (Content) args.get()[1];
			Supplier<PlayerState> renamedState = name(state + " " + content, state);

			PlayerState initialState = state.get();
			Set<ItemState<?>> updatedItems = new HashSet<>(content.map().keySet());
			updatedItems.add(mockItem());
			Function<Function<Integer, Integer>, List<ItemEntry>> updatesFactory = quantityUpdater -> updatedItems
					.stream()//
					.map(item -> ItemEntry.of(item, quantityUpdater.apply(initialState.getQuantity(item))))//
					.collect(Collectors.toList());

			return Stream.of(//
					// Additions
					arguments(renamedState, updatesFactory.apply(quantity -> 0)), //
					arguments(renamedState, updatesFactory.apply(quantity -> 1)), //
					arguments(renamedState, updatesFactory.apply(quantity -> 123)), //

					// Valid removals
					arguments(renamedState, updatesFactory.apply(quantity -> quantity > 0 ? -1 : 0)), //
					arguments(renamedState, updatesFactory.apply(quantity -> quantity > 0 ? -quantity : 0))//
			);
		});
	}

	static Stream<Arguments> invalidUpdates() {
		return validUpdates().flatMap(args -> {
			@SuppressWarnings("unchecked")
			Supplier<PlayerState> state = (Supplier<PlayerState>) args.get()[0];
			@SuppressWarnings("unchecked")
			List<ItemEntry> validUpdates = (List<ItemEntry>) args.get()[1];

			PlayerState initialState = state.get();
			return validUpdates.stream().flatMap(update -> {
				List<ItemEntry> invalidUpdates = new ArrayList<>(validUpdates);
				ItemState<?> item = update.getItem();
				int invalidRemoval = -(initialState.getQuantity(item) + 1);
				invalidUpdates.set(invalidUpdates.indexOf(update), ItemEntry.of(item, invalidRemoval));
				return Stream.of(arguments(state, invalidUpdates));
			});

		})//
			// Remove redundant cases
				.map(args -> Arrays.asList(args.get()))//
				.distinct()//
				.map(args -> arguments(args.toArray()));
	}

	private static ItemState<ItemType> mockItem() {
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

	private static <T> Supplier<T> name(String name, Supplier<T> supplier) {
		return new Supplier<T>() {

			@Override
			public T get() {
				return supplier.get();
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}
}
