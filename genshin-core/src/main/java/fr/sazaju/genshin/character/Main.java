package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.simple.Billet.*;
import static fr.sazaju.genshin.item.simple.BossDrop.*;
import static fr.sazaju.genshin.item.simple.CharacterAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.ForgingMaterial.*;
import static fr.sazaju.genshin.item.simple.LocalSpecialty.*;
import static fr.sazaju.genshin.item.simple.Mora.*;
import static fr.sazaju.genshin.item.simple.OriginalResin.*;
import static fr.sazaju.genshin.item.simple.TalentLevelUpMaterial.*;
import static fr.sazaju.genshin.item.simple.WeaponAscensionMaterial.*;
import static fr.sazaju.genshin.leveling.Levels.Filter.*;
import static java.util.Comparator.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerState;
import fr.sazaju.genshin.PlayerStateHistory;
import fr.sazaju.genshin.PlayerStateHistoryFactory;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.item.weapon.WeaponType;
import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.recipe.Recipe;
import fr.sazaju.genshin.recipe.Recipes;
import fr.sazaju.genshin.tab.Slot;
import fr.sazaju.genshin.tab.Tab;

public class Main {

	public static void main(String[] args) {
		PlayerState data = PlayerState.empty();

		Weapon weapon = WeaponType.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		Character character = CharacterProfile.KEQING.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();
		System.out.println("[Character]");
		System.out.println(character);
		// TODO Add to data

		int adventureRank = 40;
		Levels characterAscensionLevels = character.profile.ascensionLevels//
				.filter(remainingForCharacterAscension(character).and(allowedForAdventureRank(adventureRank)));
		ItemStack characterAscensionsCost = characterAscensionLevels.getCost();
		displayLevels("Ascension", characterAscensionLevels);

		int characterTalentLevelLimit = 6;
		ItemStack characterTalentsCost = Stream.of(character.normalAttackLevel)//
				.map(talentLevel -> {
					return character.profile.talentLevels//
							.filter(remainingForTalent(talentLevel).and(atMostLevel(characterTalentLevelLimit))
									.and(allowedForAdventureRank(adventureRank)))
							.getCost();
				})//
				.reduce(ItemStack::addStack).get();
		System.out.println("[Talents total]");
		displayStack(characterTalentsCost);

		int weaponLevelLimit = 4;
		Levels weaponAscensionLevels = weapon.type.ascensionLevels//
				.filter(remainingForWeaponAscension(weapon).and(atMostLevel(weaponLevelLimit))
						.and(allowedForAdventureRank(adventureRank)));
		ItemStack weaponAscensionCost = weaponAscensionLevels.getCost();
		System.out.println("[Weapon]");
		displayLevels("Ascension", weaponAscensionLevels);

		Recipe totalCost = Recipe.fromDiff(characterAscensionsCost//
				.addStack(characterTalentsCost)//
				.addStack(weaponAscensionCost)//
				.getMap()//
		).reverse();
		System.out.println("[Total cost]");
		displayEntries(totalCost.streamCosts());

		data = data.update(Stream.of(//
				ItemEntry.of(weapon), //
				ItemEntry.of(MORA.itemState(), Integer.MAX_VALUE), //
				ItemEntry.of(LAPIS.itemState(), 200), //
				ItemEntry.of(PRISM.itemState(), 26), //
				ItemEntry.of(VAJRADA.itemState(TWO_STARS), 26), //
				ItemEntry.of(VAJRADA.itemState(THREE_STARS), 29), //
				ItemEntry.of(NECTAR.itemState(ONE_STAR), 196), //
				ItemEntry.of(NECTAR.itemState(TWO_STARS), 46), //
				ItemEntry.of(NECTAR.itemState(THREE_STARS), 12), //
				ItemEntry.of(GUYUN_PILAR.itemState(TWO_STARS), 14), //
				ItemEntry.of(GUYUN_PILAR.itemState(THREE_STARS), 15), //
				ItemEntry.of(GUYUN_PILAR.itemState(FOUR_STARS), 2), //
				ItemEntry.of(PROSPERITY.itemState(TWO_STARS), 41), //
				ItemEntry.of(PROSPERITY.itemState(THREE_STARS), 53), //
				ItemEntry.of(SACRIFICIAL_KNIFE.itemState(TWO_STARS), 78), //
				ItemEntry.of(SACRIFICIAL_KNIFE.itemState(THREE_STARS), 15), //
				ItemEntry.of(SACRIFICIAL_KNIFE.itemState(FOUR_STARS), 4), //
				ItemEntry.of(INSIGNIA.itemState(ONE_STAR), 357), //
				ItemEntry.of(INSIGNIA.itemState(TWO_STARS), 79), //
				ItemEntry.of(INSIGNIA.itemState(THREE_STARS), 12), //
				ItemEntry.of(WHITE_IRON_CHUNK.itemState(), 50), //
				ItemEntry.of(NORTHLANDER_CATALYST_BILLET.itemState(), 1), //
				ItemEntry.of(CRYSTAL_CHUNK.itemState(), 50)//
		));
		System.out.println("[Available]");
		displayPlayerData(data);

		System.out.println("[Recipes]");
		Recipes.streamMihoyoRecipes().forEach(recipe -> {
			displayRecipe(recipe);
		});

		PlayerState target = PlayerState.fromItemEntries(totalCost.streamCosts());
		PlayerStateHistory conversionHistory = new PlayerStateHistoryFactory(Recipes::streamMihoyoRecipes)
				.naiveSearch(data, target).findFirst().get();
		System.out.println("[Required Conversions]");
		conversionHistory.streamRecipes().forEach(Main::displayRecipe);

		PlayerState dataAfterConversion = conversionHistory.getResultingState();
		System.out.println("[Available After Conversion]");
		displayPlayerData(dataAfterConversion);

		System.out.println("[Consumed]");
		displayEntries(totalCost.streamCosts());

		PlayerState dataRemaining = dataAfterConversion.update(totalCost.stream());
		System.out.println("[Remaining]");
		displayPlayerData(dataRemaining);

		// TODO Expose on service
		// TODO Test on service
		// TODO Expose on website
		// TODO Resolve TODOs
	}

	private static void displayRecipe(Recipe recipe) {
		System.out.println("  " + recipe);
	}

	private static void displayLevels(String levelsType, Levels levels) {
		for (Entry<Integer, Level> levelEntry : sortedByKeys(levels.toMap().entrySet())) {
			System.out.println("[" + levelsType + " level " + levelEntry.getKey() + "]");
			displayStack(levelEntry.getValue().getCost());
		}
		String range;
		if (levels.toMap().isEmpty()) {
			range = "none";
		} else {
			int minIndex = levels.toMap().keySet().stream().mapToInt(i -> i).min().orElseThrow();
			int maxIndex = levels.toMap().keySet().stream().mapToInt(i -> i).max().orElseThrow();
			range = minIndex + "-" + maxIndex;
		}
		System.out.println("[" + levelsType + " total (" + range + ")]");
		displayStack(levels.toMap().values().stream().map(Level::getCost).reduce(ItemStack::addStack)
				.orElse(ItemStack.empty()));
	}

	private static <K extends Comparable<K>, T> List<Entry<K, T>> sortedByKeys(Set<Entry<K, T>> entrySet) {
		return entrySet.stream().sorted(Comparator.comparing(Entry::getKey)).collect(Collectors.toList());
	}

	private static void displayStack(ItemStack stack) {
		displayEntries(stack.stream());
	}

	private static void displayEntries(Stream<ItemEntry> entries) {
		entries//
				.sorted(Comparator.comparing(entry -> entry.getItem(), ItemState.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}

	private static void displayPlayerData(PlayerState data) {
		System.out.println(" <MORAS: " + data.getQuantity(MORA.itemState()) + ">");
		System.out.println(" <RESINS: " + data.getQuantity(ORIGINAL_RESIN.itemState()) + ">");
		Stream.of(Tab.values())//
				.flatMap(tab -> {
					System.out.println(" <" + tab + ">");
					return tab.on(data)//
							.streamSlots()//
							.sorted(comparing(Slot::getItem, ItemState.syntaxicComparator()));
				})//
				.forEach(slot -> {
					System.out.println("  " + slot);
				});
		;
	}
}
