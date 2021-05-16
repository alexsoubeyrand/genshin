package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.leveling.Levels.Filter.*;
import static fr.sazaju.genshin.material.Book.*;
import static fr.sazaju.genshin.material.BossDrop.*;
import static fr.sazaju.genshin.material.CharacterAscensionMaterial.*;
import static fr.sazaju.genshin.material.CommonMobDrop.*;
import static fr.sazaju.genshin.material.EliteMobDrop.*;
import static fr.sazaju.genshin.material.LocalSpecialty.*;
import static fr.sazaju.genshin.material.MaterialStack.*;
import static fr.sazaju.genshin.material.MaterialStack.Filter.*;
import static fr.sazaju.genshin.material.MaterialStack.HistorySelector.*;
import static fr.sazaju.genshin.material.Mora.*;
import static fr.sazaju.genshin.material.WeaponAscensionMaterial.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.material.Material;
import fr.sazaju.genshin.material.MaterialStack;
import fr.sazaju.genshin.material.MaterialStackHistory;
import fr.sazaju.genshin.weapon.Weapon;
import fr.sazaju.genshin.weapon.WeaponProfile;

public class Main {

	public static void main(String[] args) {
		Weapon weapon = WeaponProfile.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		Character character = CharacterProfile.KEQING.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();

		int adventureRank = 40;
		Levels characterAscensionLevels = character.profile.ascensionLevels//
				.filter(remainingForCharacterAscension(character).and(allowedForAdventureRank(adventureRank)));
		MaterialStack characterAscensionsCost = characterAscensionLevels.getCost();

		int characterTalentLevelLimit = 6;
		MaterialStack characterTalentsCost = Stream.of(character.normalAttackLevel)//
				.map(talentLevel -> {
					return character.profile.talentLevels//
							.filter(remainingForTalent(talentLevel).and(atMostLevel(characterTalentLevelLimit))
									.and(allowedForAdventureRank(adventureRank)))
							.getCost();
				})//
				.reduce(MaterialStack::addStack).get();

		int weaponLevelLimit = 4;
		Levels weaponAscensionLevels = weapon.profile.ascensionLevels//
				.filter(remainingForWeaponAscension(weapon).and(atMostLevel(weaponLevelLimit))
						.and(allowedForAdventureRank(adventureRank)));
		MaterialStack weaponAscensionCost = weaponAscensionLevels.getCost();

		MaterialStack totalCost = characterAscensionsCost.addStack(characterTalentsCost).addStack(weaponAscensionCost);
		MaterialStack availableMaterial = MaterialStack.empty().addStack(List.of(//
				entry(MORA.material(), Integer.MAX_VALUE), //
				entry(LAPIS.material(), 200), //
				entry(PRISM.material(), 26), //
				entry(VAJRADA.material(TWO_STARS), 26), //
				entry(VAJRADA.material(THREE_STARS), 29), //
				entry(NECTAR.material(ONE_STAR), 196), //
				entry(NECTAR.material(TWO_STARS), 46), //
				entry(NECTAR.material(THREE_STARS), 12), //
				entry(GUYUN_PILAR.material(TWO_STARS), 14), //
				entry(GUYUN_PILAR.material(THREE_STARS), 15), //
				entry(GUYUN_PILAR.material(FOUR_STARS), 2), //
				entry(PROSPERITY.material(TWO_STARS), 41), //
				entry(PROSPERITY.material(THREE_STARS), 53), //
				entry(SACRIFICIAL_KNIFE.material(TWO_STARS), 78), //
				entry(SACRIFICIAL_KNIFE.material(THREE_STARS), 15), //
				entry(SACRIFICIAL_KNIFE.material(FOUR_STARS), 4), //
				entry(INSIGNIA.material(ONE_STAR), 357), //
				entry(INSIGNIA.material(TWO_STARS), 79), //
				entry(INSIGNIA.material(THREE_STARS), 12)//
		));
		MaterialStackHistory conversionHistory = availableMaterial.createRecipeHistory(totalCost, ONLY_IF_SUCCESSFUL);
		MaterialStack availableMaterialAfterConversion = conversionHistory.getResultingStack().filter(nonZero());

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

	private static void displayDiff(MaterialStack diff) {
		MaterialStack consumed = diff.filter(strictlyNegative()).times(-1);
		MaterialStack produced = diff.filter(strictlyPositive());
		Function<MaterialStack, String> formater = stack -> {
			return stack.stream().map(entry -> {
				Material<?> material = entry.getMaterial();
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
		displayStack(levels.toMap().values().stream().map(Level::getCost).reduce(MaterialStack::addStack)
				.orElse(MaterialStack.empty()));
	}

	private static <K extends Comparable<K>, T> List<Entry<K, T>> sortedByKeys(Set<Entry<K, T>> entrySet) {
		return entrySet.stream().sorted(Comparator.comparing(Entry::getKey)).collect(Collectors.toList());
	}

	private static void displayStack(MaterialStack stack) {
		stack.stream()//
				.sorted(Comparator.comparing(entry -> entry.getMaterial(), Material.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getMaterial() + " x" + entry.getQuantity());
				});
	}

}
