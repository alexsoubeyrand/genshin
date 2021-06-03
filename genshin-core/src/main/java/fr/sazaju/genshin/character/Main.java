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

import fr.sazaju.genshin.PlayerData;
import fr.sazaju.genshin.PlayerDataHistory;
import fr.sazaju.genshin.PlayerDataHistoryFactory;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.item.weapon.WeaponType;
import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.recipe.Recipe;

public class Main {

	public static void main(String[] args) {
		PlayerData data = PlayerData.empty();

		Weapon weapon = WeaponType.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		data.add(weapon);
		Character character = CharacterProfile.KEQING.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();
		// TODO Add to data

		int adventureRank = 40;
		Levels characterAscensionLevels = character.profile.ascensionLevels//
				.filter(remainingForCharacterAscension(character).and(allowedForAdventureRank(adventureRank)));
		ItemStack characterAscensionsCost = characterAscensionLevels.getCost();

		int characterTalentLevelLimit = 6;
		ItemStack characterTalentsCost = Stream.of(character.normalAttackLevel)//
				.map(talentLevel -> {
					return character.profile.talentLevels//
							.filter(remainingForTalent(talentLevel).and(atMostLevel(characterTalentLevelLimit))
									.and(allowedForAdventureRank(adventureRank)))
							.getCost();
				})//
				.reduce(ItemStack::addStack).get();

		int weaponLevelLimit = 4;
		Levels weaponAscensionLevels = weapon.type.ascensionLevels//
				.filter(remainingForWeaponAscension(weapon).and(atMostLevel(weaponLevelLimit))
						.and(allowedForAdventureRank(adventureRank)));
		ItemStack weaponAscensionCost = weaponAscensionLevels.getCost();

		Recipe totalCost = Recipe
				.fromDiff(characterAscensionsCost.addStack(characterTalentsCost).addStack(weaponAscensionCost))
				.reverse();
		data = data.addAll(List.of(//
				ItemEntry.of(weapon), //
				ItemEntry.of(MORA.item(), Integer.MAX_VALUE), //
				ItemEntry.of(LAPIS.item(), 200), //
				ItemEntry.of(PRISM.item(), 26), //
				ItemEntry.of(VAJRADA.item(TWO_STARS), 26), //
				ItemEntry.of(VAJRADA.item(THREE_STARS), 29), //
				ItemEntry.of(NECTAR.item(ONE_STAR), 196), //
				ItemEntry.of(NECTAR.item(TWO_STARS), 46), //
				ItemEntry.of(NECTAR.item(THREE_STARS), 12), //
				ItemEntry.of(GUYUN_PILAR.item(TWO_STARS), 14), //
				ItemEntry.of(GUYUN_PILAR.item(THREE_STARS), 15), //
				ItemEntry.of(GUYUN_PILAR.item(FOUR_STARS), 2), //
				ItemEntry.of(PROSPERITY.item(TWO_STARS), 41), //
				ItemEntry.of(PROSPERITY.item(THREE_STARS), 53), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(TWO_STARS), 78), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(THREE_STARS), 15), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(FOUR_STARS), 4), //
				ItemEntry.of(INSIGNIA.item(ONE_STAR), 357), //
				ItemEntry.of(INSIGNIA.item(TWO_STARS), 79), //
				ItemEntry.of(INSIGNIA.item(THREE_STARS), 12), //
				ItemEntry.of(WHITE_IRON_CHUNK.item(), 50), //
				ItemEntry.of(NORTHLANDER_CATALYST_BILLET.item(), 1), //
				ItemEntry.of(CRYSTAL_CHUNK.item(), 50)//
		));
		PlayerData target = PlayerData.fromItemEntries(totalCost.streamCosts());
		PlayerDataHistory conversionHistory = new PlayerDataHistoryFactory()
				.naiveSearch(data, target, Recipe::streamMihoyoRecipes).findFirst().get();
		PlayerData dataAfterConversion = conversionHistory.getResultingData();

		///////////////////////////////////////
		System.out.println("[Weapon]");
		displayLevels("Ascension", weaponAscensionLevels);

		System.out.println("[Character]");
		System.out.println(character);
		displayLevels("Ascension", characterAscensionLevels);
		System.out.println("[Talents total]");
		displayStack(characterTalentsCost);
		System.out.println("[Total cost]");
		displayEntries(totalCost.streamCosts());

		System.out.println("[Available]");
		displayPlayerData(data);

		System.out.println("[Recipes]");
		Recipe.streamMihoyoRecipes().forEach(recipe -> {
			displayRecipe(recipe);
		});

		System.out.println("[Required Conversions]");
		conversionHistory.streamRecipes().forEach(Main::displayRecipe);

		System.out.println("[Available After Conversion]");
		displayPlayerData(dataAfterConversion);

		System.out.println("[Consumed]");
		displayEntries(totalCost.streamCosts());

		System.out.println("[Remaining]");
		displayPlayerData(dataAfterConversion.update(totalCost));

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
				.sorted(Comparator.comparing(entry -> entry.getItem(), Item.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}
	
	private static void displayPlayerData(PlayerData data) {
		data.stream()//
				.sorted(comparing(entry -> entry.getItem(), Item.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}
}
