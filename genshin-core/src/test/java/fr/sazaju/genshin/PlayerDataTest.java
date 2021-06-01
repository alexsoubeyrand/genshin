package fr.sazaju.genshin;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.artifact.ArtifactCategory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;
import fr.sazaju.genshin.item.artifact.ArtifactSet;
import fr.sazaju.genshin.item.simple.Billet;
import fr.sazaju.genshin.item.simple.BossDrop;
import fr.sazaju.genshin.item.simple.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.simple.CommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EnhancementOre;
import fr.sazaju.genshin.item.simple.EventMaterial;
import fr.sazaju.genshin.item.simple.Food;
import fr.sazaju.genshin.item.simple.ForgingMaterial;
import fr.sazaju.genshin.item.simple.Gadget;
import fr.sazaju.genshin.item.simple.LocalSpecialty;
import fr.sazaju.genshin.item.simple.Mora;
import fr.sazaju.genshin.item.simple.OriginalResin;
import fr.sazaju.genshin.item.simple.Potion;
import fr.sazaju.genshin.item.simple.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.simple.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.weapon.WeaponType;
import fr.sazaju.genshin.recipe.Recipe;

class PlayerDataTest {

	interface ItemsExtractor {
		List<ItemEntry> apply(PlayerData data);
	}

	@ParameterizedTest
	@MethodSource("itemsExtractors")
	void testEmptyPlayerDataIsEmpty(ItemsExtractor itemsExtractor) {
		assertTrue(itemsExtractor.apply(PlayerData.empty()).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndItems")
	void testNonEmptyPlayerDataIsNotEmpty(ItemsExtractor itemsExtractor, Item<?> item) {
		assertFalse(itemsExtractor.apply(PlayerData.empty().add(item)).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("items")
	void testEmptyPlayerDataHasZeroQuantity(Item<?> item) {
		assertEquals(0, PlayerData.empty().getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("items")
	void testNonEmptyPlayerDataHasNonZeroQuantity(Item<?> item) {
		assertEquals(1, PlayerData.empty().add(item).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("items")
	void testAddIncludesOneItemOnFirstCall(Item<?> item) {
		assertEquals(1, PlayerData.empty().add(item).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testAddRejectsNonStackableItemIfPresent(Item<?> item) {
		PlayerData data = PlayerData.empty().add(item);
		assertThrows(IllegalArgumentException.class, () -> data.add(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItems")
	void testAddAggregatesStackableItem(StackableItem<?> item) {
		assertEquals(3, PlayerData.empty()//
				.add(item)//
				.add(item)//
				.add(item)//
				.getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItems")
	void testAddWithQuantityReturnsSumThroughGetQuantity(StackableItem<?> item) {
		assertEquals(6, PlayerData.empty()//
				.add(item, 1)//
				.add(item, 2)//
				.add(item, 3)//
				.getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItemsNonRedundantPairs")
	void testAddWithQuantityDoesNotChangeOtherQuantity(StackableItem<?> item1, StackableItem<?> item2) {
		assertEquals(0, PlayerData.empty().add(item1, 123).getQuantity(item2));
	}

	@ParameterizedTest
	@MethodSource("items")
	void testRemoveRejectsItemIfNotPresent(Item<?> item) {
		PlayerData data = PlayerData.empty();
		assertThrows(IllegalArgumentException.class, () -> data.remove(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItems")
	void testRemoveDecreasesStackableItem(StackableItem<?> item) {
		assertEquals(2, PlayerData.empty().add(item, 5)//
				.remove(item)//
				.remove(item)//
				.remove(item)//
				.getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItems")
	void testRemoveWithQuantityRejectsStackableItemIfNotEnough(StackableItem<?> item) {
		PlayerData data = PlayerData.empty().add(item, 123);
		assertThrows(IllegalArgumentException.class, () -> data.remove(item, 456));
	}

	@ParameterizedTest
	@MethodSource("stackableItems")
	void testRemoveWithQuantityDecreasesStackableItem(StackableItem<?> item) {
		assertEquals(4, PlayerData.empty().add(item, 10)//
				.remove(item, 1)//
				.remove(item, 2)//
				.remove(item, 3)//
				.getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("stackableItemsNonRedundantPairs")
	void testRemoveWithQuantityDoesNotChangeOtherQuantity(StackableItem<?> item1, StackableItem<?> item2) {
		assertEquals(123, PlayerData.empty().add(item1, 123).add(item2, 123).remove(item1, 23).getQuantity(item2));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testAddAllRejectsNonStackableItemIfPresent(Item<?> item) {
		PlayerData data = PlayerData.empty().add(item);
		List<ItemEntry> illegalEntries = List.of(ItemEntry.of(item));
		assertThrows(IllegalArgumentException.class, () -> data.addAll(illegalEntries));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testAddAllRejectsRedundantNonStackableItem(Item<?> item) {
		PlayerData data = PlayerData.empty();
		List<ItemEntry> illegalEntries = List.of(ItemEntry.of(item), ItemEntry.of(item));
		assertThrows(IllegalArgumentException.class, () -> data.addAll(illegalEntries));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndItemsPairs")
	void testAddAllEquivalentToAddSequence(ItemsExtractor itemsExtractor, Item<?> item1, Item<?> item2) {
		PlayerData originData = PlayerData.empty();
		PlayerData sequenceData = originData.add(item1).add(item2);
		PlayerData addAllData = originData.addAll(List.of(//
				ItemEntry.of(item1), //
				ItemEntry.of(item2)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(addAllData));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndStackableItemsPairs")
	void testAddAllEquivalentToAddWithQuantitySequence(ItemsExtractor itemsExtractor, StackableItem<?> item1,
			StackableItem<?> item2) {
		PlayerData originData = PlayerData.empty();
		PlayerData sequenceData = originData.add(item1, 123).add(item2, 456);
		PlayerData addAllData = originData.addAll(List.of(//
				ItemEntry.of(item1, 123), //
				ItemEntry.of(item2, 456)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(addAllData));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testRemoveAllRejectsNonStackableItemIfAbsent(Item<?> item) {
		PlayerData data = PlayerData.empty();
		List<ItemEntry> entries = List.of(ItemEntry.of(item));
		assertThrows(IllegalArgumentException.class, () -> data.removeAll(entries));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testRemoveAllRejectsRedundantNonStackableItem(Item<?> item) {
		PlayerData data = PlayerData.empty().add(item);
		List<ItemEntry> entries = List.of(ItemEntry.of(item), ItemEntry.of(item));
		assertThrows(IllegalArgumentException.class, () -> data.removeAll(entries));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndItemsPairs")
	void testRemoveAllEquivalentToRemoveSequence(ItemsExtractor itemsExtractor, Item<?> item1, Item<?> item2) {
		PlayerData originData = PlayerData.empty().add(item1).add(item2);
		PlayerData sequenceData = originData.remove(item1).remove(item2);
		PlayerData addAllData = originData.removeAll(List.of(//
				ItemEntry.of(item1), //
				ItemEntry.of(item2)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(addAllData));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndStackableItemsPairs")
	void testRemoveAllEquivalentToRemoveWithQuantitySequence(ItemsExtractor itemsExtractor, StackableItem<?> item1,
			StackableItem<?> item2) {
		PlayerData originData = PlayerData.empty().add(item1, 999).add(item2, 999);
		PlayerData sequenceData = originData.remove(item1, 123).remove(item2, 456);
		PlayerData addAllData = originData.removeAll(List.of(//
				ItemEntry.of(item1, 123), //
				ItemEntry.of(item2, 456)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(addAllData));
	}

	static Stream<Arguments> testUpdateRejectsRecipeWhichConsumesMoreThanAvailableItems() {
		PlayerData emptyData = PlayerData.empty();
		Stream<Arguments> nonStackableItemCases = nonStackableItems().flatMap(item -> {
			return Stream.of(arguments(emptyData, Recipe.fromDiff(Map.of(item, -1))));
		});
		Stream<Arguments> stackableItemCases = stackableItems().flatMap(item -> {
			return Stream.concat(//
					Stream.of(arguments(emptyData, Recipe.fromDiff(Map.of(item, -1)))), //
					Stream.of(arguments(emptyData.add(item, 10), Recipe.fromDiff(Map.of(item, -11))))//
			);
		});
		return Stream.concat(nonStackableItemCases, stackableItemCases);
	}

	@ParameterizedTest
	@MethodSource
	void testUpdateRejectsRecipeWhichConsumesMoreThanAvailableItems(PlayerData data, Recipe recipe) {
		assertThrows(IllegalArgumentException.class, () -> data.update(recipe));
	}

	static Stream<Arguments> testUpdateRejectsRecipeWhichProducesMoreThanOneNonStackableItems() {
		PlayerData emptyData = PlayerData.empty();
		return nonStackableItems().flatMap(item -> Stream.concat(//
				Stream.of(arguments(emptyData, Recipe.fromDiff(Map.of(item, 2)))), //
				Stream.of(arguments(emptyData.add(item), Recipe.fromDiff(Map.of(item, 1))))//
		));
	}

	@ParameterizedTest
	@MethodSource
	void testUpdateRejectsRecipeWhichProducesMoreThanOneNonStackableItems(PlayerData data, Recipe recipe) {
		assertThrows(IllegalArgumentException.class, () -> data.update(recipe));
	}

	static Stream<Arguments> testUpdateSumsItemsOfRecipe() {
		PlayerData emptyData = PlayerData.empty();
		Stream<Arguments> nonStackableCases = nonStackableItems().flatMap(item -> {
			return Stream.of(//
					arguments(emptyData, Recipe.fromDiff(Map.of(item, 1)), item, 1), //
					arguments(emptyData.add(item), Recipe.fromDiff(Map.of(item, -1)), item, 0)//
			);
		});
		Stream<Arguments> stackableCases = stackableItems().flatMap(item -> {
			return Stream.of(//
					arguments(emptyData, Recipe.fromDiff(Map.of(item, 10)), item, 10), //
					arguments(emptyData.add(item, 123), Recipe.fromDiff(Map.of(item, 654)), item, 777), //
					arguments(emptyData.add(item, 10), Recipe.fromDiff(Map.of(item, -3)), item, 7), //
					arguments(emptyData.add(item, 10), Recipe.fromDiff(Map.of(item, -10)), item, 0)//
			);
		});
		return Stream.concat(nonStackableCases, stackableCases);
	}

	@ParameterizedTest(name = "data {0} with recipe {1} results in {3} x {2}")
	@MethodSource
	void testUpdateSumsItemsOfRecipe(PlayerData data, Recipe recipe, Item<?> item, int finalQuantity) {
		assertEquals(finalQuantity, data.update(recipe).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("nonStackableItems")
	void testPlayerDataFromItemEntriesRejectsRedundantNonStackableItem(Item<?> item) {
		List<ItemEntry> illegalEntries = List.of(ItemEntry.of(item), ItemEntry.of(item));
		assertThrows(IllegalArgumentException.class, () -> PlayerData.fromItemEntries(illegalEntries));
	}
	
	@ParameterizedTest
	@MethodSource("itemsExtractorAndItemsPairs")
	void testPlayerDataFromItemEntriesEquivalentToAddSequence(ItemsExtractor itemsExtractor, Item<?> item1, Item<?> item2) {
		PlayerData sequenceData = PlayerData.empty().add(item1).add(item2);
		PlayerData entriesData = PlayerData.fromItemEntries(List.of(//
				ItemEntry.of(item1), //
				ItemEntry.of(item2)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(entriesData));
	}
	
	@ParameterizedTest
	@MethodSource("itemsExtractorAndStackableItemsPairs")
	void testPlayerDataFromItemEntriesEquivalentToAddWithQuantitySequence(ItemsExtractor itemsExtractor, StackableItem<?> item1,
			StackableItem<?> item2) {
		PlayerData sequenceData = PlayerData.empty().add(item1, 123).add(item2, 456);
		PlayerData entriesData = PlayerData.fromItemEntries(List.of(//
				ItemEntry.of(item1, 123), //
				ItemEntry.of(item2, 456)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(entriesData));
	}


	@Test
	void testGetQuantityRejectsUnknownItem() {
		PlayerData data = PlayerData.empty();
		Item<?> item = mockItem();
		assertThrows(IllegalArgumentException.class, () -> data.getQuantity(item));
	}

	@Test
	void testAddRejectsUnknownItem() {
		PlayerData data = PlayerData.empty();
		Item<?> item = mockItem();
		assertThrows(IllegalArgumentException.class, () -> data.add(item));
	}

	@Test
	void testRemoveRejectsUnknownItem() {
		PlayerData data = PlayerData.empty();
		Item<?> item = mockItem();
		assertThrows(IllegalArgumentException.class, () -> data.remove(item));
	}

	@Test
	void testAddWithQuantityRejectsUnknownStackableItem() {
		PlayerData data = PlayerData.empty();
		StackableItem<?> item = mockStackableItem();
		assertThrows(IllegalArgumentException.class, () -> data.add(item, 123));
	}

	@Test
	void testRemoveWithQuantityRejectsUnknownStackableItem() {
		PlayerData data = PlayerData.empty();
		StackableItem<?> item = mockStackableItem();
		assertThrows(IllegalArgumentException.class, () -> data.remove(item, 123));
	}

	@Test
	void testUpdateRejectsUnknownItem() {
		PlayerData data = PlayerData.empty();
		Recipe recipe = Recipe.fromDiff(Map.of(mockItem(), 1));
		assertThrows(IllegalArgumentException.class, () -> data.update(recipe));
	}

	static Stream<ItemsExtractor> itemsExtractors() {
		return Stream.of(//
				name("stream", data -> data.stream().collect(Collectors.toList())), //
				name("iterator", data -> {
					List<ItemEntry> entries = new LinkedList<>();
					Iterator<ItemEntry> iterator = data.iterator();
					while (iterator.hasNext()) {
						entries.add(iterator.next());
					}
					return entries;
				}), //
				name("forEach", data -> {
					List<ItemEntry> entries = new LinkedList<>();
					data.forEach(entries::add);
					return entries;
				})//
		);
	}

	static Stream<Item<?>> items() {
		return Stream.of(//
				// One item of each type
				WeaponType.APPRENTICE_S_NOTES.item(), //
				ArtifactSet.MAIDEN_BELOVED.ofCategory(CIRCLET_OF_LOGOS).item(FOUR_STARS), //
				Billet.NORTHLANDER_BOW_BILLET.item(), //
				BossDrop.PRISM.item(), //
				CharacterAscensionMaterial.AGNIDUS.item(THREE_STARS), //
				CommonAscensionMaterial.NECTAR.item(THREE_STARS), //
				EliteCommonAscensionMaterial.CHAOS.item(THREE_STARS), //
				EnhancementOre.ENHANCEMENT_ORE.item(), //
				EventMaterial.CROWN_OF_INSIGHT.item(), //
				Food.APPLE.item(), //
				ForgingMaterial.FROG.item(), //
				Gadget.NRE_MENU_30.item(), //
				LocalSpecialty.CECILIA.item(), //
				Mora.MORA.item(), //
				OriginalResin.ORIGINAL_RESIN.item(), //
				Potion.FROSTING_ESSENTIAL_OIL.item(), //
				TalentLevelUpMaterial.GOLD.item(THREE_STARS), //
				WeaponAscensionMaterial.GUYUN_PILAR.item(THREE_STARS)//
		);
	}

	static Stream<Arguments> itemsExtractorAndItems() {
		return itemsExtractors().flatMap(extractor -> //
		items().flatMap(item -> //
		Stream.of(arguments(extractor, item))//
		));
	}

	static Stream<Arguments> itemsExtractorAndItemsPairs() {
		return itemsExtractors().flatMap(extractor -> //
		items().flatMap(item1 -> //
		items().flatMap(item2 -> //
		Stream.of(arguments(extractor, item1, item2))//
		)));
	}

	static Stream<Arguments> itemsExtractorAndStackableItemsPairs() {
		return itemsExtractors().flatMap(extractor -> //
		stackableItems().flatMap(item1 -> //
		stackableItems().flatMap(item2 -> //
		Stream.of(arguments(extractor, item1, item2))//
		)));
	}

	static Stream<Item<?>> nonStackableItems() {
		return items().filter(item -> !(item instanceof StackableItem<?>));
	}

	static Stream<StackableItem<?>> stackableItems() {
		return items().filter(item -> item instanceof StackableItem<?>).map(item -> (StackableItem<?>) item);
	}

	static Stream<Arguments> stackableItemsNonRedundantPairs() {
		return stackableItems().flatMap(item1 -> //
		stackableItems().flatMap(item2 -> //
		item1.equals(item2) //
				? Stream.empty()//
				: Stream.of(arguments(item1, item2))//
		));
	}

	private static ItemsExtractor name(String name, ItemsExtractor itemsExtractor) {
		return new ItemsExtractor() {

			@Override
			public List<ItemEntry> apply(PlayerData data) {
				return itemsExtractor.apply(data);
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}

	private ItemType mockItemType() {
		return new ItemType() {

			@Override
			public boolean hasRarity(Rarity rarity) {
				return true;
			}
		};
	}

	private Item<ItemType> mockItem() {
		return new Item<ItemType>(mockItemType(), ONE_STAR);
	}

	private StackableItem<ItemType> mockStackableItem() {
		return new StackableItem<ItemType>(mockItemType(), ONE_STAR);
	}
}
