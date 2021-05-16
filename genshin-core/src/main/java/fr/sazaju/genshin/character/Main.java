package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.BossDrop.*;
import static fr.sazaju.genshin.item.CharacterAscensionMaterialMulti.*;
import static fr.sazaju.genshin.item.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.ItemStack.*;
import static fr.sazaju.genshin.item.ItemStack.Filter.*;
import static fr.sazaju.genshin.item.ItemStack.HistorySelector.*;
import static fr.sazaju.genshin.item.LocalSpecialty.*;
import static fr.sazaju.genshin.item.Mora.*;
import static fr.sazaju.genshin.item.TalentLevelUpMaterial.*;
import static fr.sazaju.genshin.item.WeaponAscensionMaterial.*;
import static fr.sazaju.genshin.leveling.Levels.Filter.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemStackHistory;
import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.weapon.Weapon;
import fr.sazaju.genshin.weapon.WeaponType;

public class Main {

	public static void main(String[] args) {
		Weapon weapon = WeaponType.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		Character character = CharacterProfile.KEQING.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();

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

		ItemStack totalCost = characterAscensionsCost.addStack(characterTalentsCost).addStack(weaponAscensionCost);
		ItemStack availableMaterial = ItemStack.empty().addStack(List.of(//
				entry(MORA.item(), Integer.MAX_VALUE), //
				entry(LAPIS.item(), 200), //
				entry(PRISM.item(), 26), //
				entry(VAJRADA.item(TWO_STARS), 26), //
				entry(VAJRADA.item(THREE_STARS), 29), //
				entry(NECTAR.item(ONE_STAR), 196), //
				entry(NECTAR.item(TWO_STARS), 46), //
				entry(NECTAR.item(THREE_STARS), 12), //
				entry(GUYUN_PILAR.item(TWO_STARS), 14), //
				entry(GUYUN_PILAR.item(THREE_STARS), 15), //
				entry(GUYUN_PILAR.item(FOUR_STARS), 2), //
				entry(PROSPERITY.item(TWO_STARS), 41), //
				entry(PROSPERITY.item(THREE_STARS), 53), //
				entry(SACRIFICIAL_KNIFE.item(TWO_STARS), 78), //
				entry(SACRIFICIAL_KNIFE.item(THREE_STARS), 15), //
				entry(SACRIFICIAL_KNIFE.item(FOUR_STARS), 4), //
				entry(INSIGNIA.item(ONE_STAR), 357), //
				entry(INSIGNIA.item(TWO_STARS), 79), //
				entry(INSIGNIA.item(THREE_STARS), 12)//
		));
		ItemStackHistory conversionHistory = availableMaterial.createRecipeHistory(totalCost, ONLY_IF_SUCCESSFUL);
		ItemStack availableMaterialAfterConversion = conversionHistory.getResultingStack().filter(nonZero());

		///////////////////////////////////////
		System.out.println("[Weapon]");
		displayLevels("Ascension", weaponAscensionLevels);

		System.out.println("[Character]");
		System.out.println(character);
		displayLevels("Ascension", characterAscensionLevels);
		System.out.println("[Talents total]");
		displayStack(characterTalentsCost);
		System.out.println("[Total cost]");
		displayStack(totalCost);

		System.out.println("[Available]");
		displayStack(availableMaterial);

		System.out.println("[Required Conversions]");
		conversionHistory.streamDiffs().forEach(Main::displayDiff);

		System.out.println("[Available After Conversion]");
		displayStack(availableMaterialAfterConversion);

		System.out.println("[Consumed]");
		displayStack(totalCost);

		System.out.println("[Remaining]");
		displayStack(availableMaterialAfterConversion.minusStack(totalCost).filter(nonZero()));

		// TODO Expose on service
		// TODO Test on service
		// TODO Expose on website
		// TODO Resolve TODOs
	}

	private static void displayDiff(ItemStack diff) {
		ItemStack consumed = diff.filter(strictlyNegative()).times(-1);
		ItemStack produced = diff.filter(strictlyPositive());
		Function<ItemStack, String> formater = stack -> {
			return stack.stream().map(entry -> {
				Item<?> material = entry.getItem();
				int quantity = entry.getQuantity();
				return (quantity == 1 ? "" : quantity + " x ") + material;
			}).collect(Collectors.joining(" + "));
		};
		System.out.println("  " + formater.apply(consumed) + " => " + formater.apply(produced));
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
		stack.stream()//
				.sorted(Comparator.comparing(entry -> entry.getItem(), Item.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}

}
