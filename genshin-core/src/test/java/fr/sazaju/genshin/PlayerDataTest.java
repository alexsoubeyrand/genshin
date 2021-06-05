package fr.sazaju.genshin;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.artifact.ArtifactCategory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;
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

	// TODO Test fromMap

	interface ItemsExtractor {
		Set<ItemEntry> apply(PlayerData data);
	}

	@ParameterizedTest
	@MethodSource("itemsExtractors")
	void testEmptyPlayerDataIsEmpty(ItemsExtractor itemsExtractor) {
		assertTrue(itemsExtractor.apply(PlayerData.empty()).isEmpty());
	}

	@Test
	void testEmptyPlayerDataHasZeroQuantity() {
		assertEquals(0, PlayerData.empty().getQuantity(mockItem()));
	}

	static Stream<Arguments> testDataFromMapHasProvidedQuantity() {
		ItemState<?> item1 = mockItem();
		ItemState<?> item2 = mockItem();
		ItemState<?> item3 = mockItem();
		int quantity1 = 1;
		int quantity2 = 123;
		int quantity3 = 0;
		Map<?, Integer> map = Map.of(//
				item1, quantity1, //
				item2, quantity2, //
				item3, quantity3//
		);
		ItemState<ItemType> unknownItem = mockItem();
		return Stream.of(//
				arguments(map, item1, quantity1), //
				arguments(map, item2, quantity2), //
				arguments(map, item3, quantity3), //
				arguments(map, unknownItem, 0)//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testDataFromMapHasProvidedQuantity(Map<ItemState<?>, Integer> map, ItemState<?> item, int quantity) {
		assertEquals(quantity, PlayerData.fromMap(map).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractors")
	void testDataFromEmptyMapIsEmpty(ItemsExtractor itemsExtractor) {
		assertTrue(itemsExtractor.apply(PlayerData.fromMap(Map.of())).isEmpty());
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndItems")
	void testDataFromNonEmptyMapIsNotEmpty(ItemsExtractor itemsExtractor, ItemState<?> item) {
		assertFalse(itemsExtractor.apply(PlayerData.fromMap(Map.of(item, 1))).isEmpty());
	}

	static Stream<Arguments> testUpdateItemRejectsRemovingMoreThanAvailable() {
		return itemStates().flatMap(itemState -> //
		Stream.of(//
				arguments(itemState, 0, 1), //
				arguments(itemState, 123, 124)//
		));
	}

	@ParameterizedTest
	@MethodSource
	void testUpdateItemRejectsRemovingMoreThanAvailable(ItemState<?> itemState, int initialQuantity,
			int removedQuantity) {
		PlayerData data = PlayerData.empty().update(itemState, initialQuantity);
		assertThrows(IllegalArgumentException.class, () -> data.update(itemState, -removedQuantity));
	}

	static Stream<Arguments> testUpdateItemChangesQuantity() {
		return itemStates().flatMap(itemState -> //
		Stream.of(//
				arguments(itemState, 0, 1, 1), //
				arguments(itemState, 0, 123, 123), //
				arguments(itemState, 123, 654, 777), //
				arguments(itemState, 123, -1, 122), //
				arguments(itemState, 123, -23, 100), //
				arguments(itemState, 123, -123, 0)//
		));
	}

	@ParameterizedTest
	@MethodSource
	void testUpdateItemChangesQuantity(ItemState<?> item, int quantity, int update, int result) {
		PlayerData initialData = PlayerData.fromMap(Map.of(item, quantity));
		PlayerData updatedData = initialData.update(item, update);
		assertEquals(result, updatedData.getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemsStatesNonRedundantPairs")
	void testUpdateItemDoesNotChangeOtherQuantity(ItemState<?> item1, ItemState<?> item2) {
		PlayerData initialData = PlayerData.empty();
		PlayerData updatedData = initialData.update(item1, 123);
		assertEquals(0, updatedData.getQuantity(item2));
	}

	static Stream<Arguments> testUpdateRejectsRecipeWhichConsumesMoreThanAvailableItems() {
		// TODO Test Recipe to ensure it works as intended
		PlayerData emptyData = PlayerData.empty();
		return itemStates().flatMap(item -> {
			return Stream.concat(//
					Stream.of(arguments(emptyData, Recipe.fromDiff(Map.of(item, -1)))), //
					Stream.of(arguments(emptyData.update(item, 10), Recipe.fromDiff(Map.of(item, -11))))//
			);
		});
	}

	@ParameterizedTest
	@MethodSource
	void testUpdateRejectsRecipeWhichConsumesMoreThanAvailableItems(PlayerData data, Recipe recipe) {
		assertThrows(IllegalArgumentException.class, () -> data.update(recipe));
	}

	static Stream<Arguments> testUpdateSumsItemsOfRecipe() {
		PlayerData emptyData = PlayerData.empty();
		return itemStates().flatMap(item -> {
			return Stream.of(//
					arguments(emptyData, Recipe.fromDiff(Map.of(item, 10)), item, 10), //
					arguments(emptyData.update(item, 123), Recipe.fromDiff(Map.of(item, 654)), item, 777), //
					arguments(emptyData.update(item, 10), Recipe.fromDiff(Map.of(item, -3)), item, 7), //
					arguments(emptyData.update(item, 10), Recipe.fromDiff(Map.of(item, -10)), item, 0)//
			);
		});
	}

	@ParameterizedTest(name = "data {0} with recipe {1} results in {3} x {2}")
	@MethodSource
	void testUpdateSumsItemsOfRecipe(PlayerData data, Recipe recipe, ItemState<?> item, int finalQuantity) {
		assertEquals(finalQuantity, data.update(recipe).getQuantity(item));
	}

	@ParameterizedTest
	@MethodSource("itemsExtractorAndItemsPairs")
	void testPlayerDataFromItemEntriesEquivalentToUpdateSequence(ItemsExtractor itemsExtractor, ItemState<?> item1,
			ItemState<?> item2) {
		PlayerData sequenceData = PlayerData.empty().update(item1, 123).update(item2, 456);
		PlayerData entriesData = PlayerData.fromItemEntries(Stream.of(//
				ItemEntry.of(item1, 123), //
				ItemEntry.of(item2, 456)//
		));
		assertEquals(itemsExtractor.apply(sequenceData), itemsExtractor.apply(entriesData));
	}

	// TODO Clean sources below (some may be unused)

	static Stream<ItemsExtractor> itemsExtractors() {
		return Stream.of(//
				name("stream", data -> data.stream().collect(Collectors.toSet())), //
				name("iterator", data -> {
					Set<ItemEntry> entries = new HashSet<>();
					Iterator<ItemEntry> iterator = data.iterator();
					while (iterator.hasNext()) {
						entries.add(iterator.next());
					}
					return entries;
				}), //
				name("forEach", data -> {
					Set<ItemEntry> entries = new HashSet<>();
					data.forEach(entries::add);
					return entries;
				})//
		);
	}

	static Stream<ItemState<?>> itemStates() {
		return Stream.of(//
				// One item of each type
				WeaponType.APPRENTICE_S_NOTES.itemState(), //
				ArtifactSet.MAIDEN_BELOVED.ofCategory(CIRCLET_OF_LOGOS).itemState(FOUR_STARS), //
				Billet.NORTHLANDER_BOW_BILLET.itemState(), //
				BossDrop.PRISM.itemState(), //
				CharacterAscensionMaterial.AGNIDUS.itemState(THREE_STARS), //
				CommonAscensionMaterial.NECTAR.itemState(THREE_STARS), //
				EliteCommonAscensionMaterial.CHAOS.itemState(THREE_STARS), //
				EnhancementOre.ENHANCEMENT_ORE.itemState(), //
				EventMaterial.CROWN_OF_INSIGHT.itemState(), //
				Food.APPLE.itemState(), //
				ForgingMaterial.FROG.itemState(), //
				Gadget.NRE_MENU_30.itemState(), //
				LocalSpecialty.CECILIA.itemState(), //
				Mora.MORA.itemState(), //
				OriginalResin.ORIGINAL_RESIN.itemState(), //
				Potion.FROSTING_ESSENTIAL_OIL.itemState(), //
				TalentLevelUpMaterial.GOLD.itemState(THREE_STARS), //
				WeaponAscensionMaterial.GUYUN_PILAR.itemState(THREE_STARS)//
		);
	}

	static Stream<Arguments> itemsStatesNonRedundantPairs() {
		return itemStates().flatMap(item1 -> //
		itemStates().flatMap(item2 -> //
		item1.equals(item2) //
				? Stream.empty()//
				: Stream.of(arguments(item1, item2))//
		));
	}

	static Stream<Arguments> itemsExtractorAndItems() {
		return itemsExtractors().flatMap(extractor -> //
		itemStates().flatMap(item -> //
		Stream.of(arguments(extractor, item))//
		));
	}

	static Stream<Arguments> itemsExtractorAndItemsPairs() {
		return itemsExtractors().flatMap(extractor -> //
		itemStates().flatMap(item1 -> //
		itemStates().flatMap(item2 -> //
		Stream.of(arguments(extractor, item1, item2))//
		)));
	}

	private static ItemsExtractor name(String name, ItemsExtractor itemsExtractor) {
		return new ItemsExtractor() {

			@Override
			public Set<ItemEntry> apply(PlayerData data) {
				return itemsExtractor.apply(data);
			}

			@Override
			public String toString() {
				return name;
			}
		};
	}

	private static ItemType mockItemType() {
		return new ItemType() {

			@Override
			public boolean hasRarity(Rarity rarity) {
				return true;
			}
		};
	}

	private static ItemState<ItemType> mockItem() {
		return new ItemState<ItemType>(mockItemType(), ONE_STAR);
	}
}
