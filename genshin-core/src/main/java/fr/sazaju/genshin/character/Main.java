package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.leveling.Levels.Filter.*;
import static fr.sazaju.genshin.material.Book.*;
import static fr.sazaju.genshin.material.BossDrop.*;
import static fr.sazaju.genshin.material.CharacterAscensionMaterial.*;
import static fr.sazaju.genshin.material.CommonMobDrop.*;
import static fr.sazaju.genshin.material.EliteMobDrop.*;
import static fr.sazaju.genshin.material.MaterialStack.Filter.*;
import static fr.sazaju.genshin.material.WeaponAscensionMaterial.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.material.Material;
import fr.sazaju.genshin.material.MaterialStack;
import fr.sazaju.genshin.weapon.Weapon;
import fr.sazaju.genshin.weapon.WeaponProfile;

public class Main {

	public static void main(String[] args) {
		CharacterProfile characterProfile = CharacterProfile.KEQING;
		Set<Entry<String, Levels>> entrySet = Map
				.of("Talents", characterProfile.talentLevels, "Ascensions", characterProfile.ascensionLevels)
				.entrySet();
		for (Entry<String, Levels> levelsEntry : sortedByKeys(entrySet)) {
			String levelsType = levelsEntry.getKey();
			Levels levels = levelsEntry.getValue();
			displayLevels(levelsType, levels);
		}
		Weapon weapon = WeaponProfile.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		Character character = characterProfile.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();
		System.out.println("[Character]");
		System.out.println(character);
		System.out.println("Ascension level = " + character.ascensionLevel);

		int adventureRank = 40;
		Levels characterLevels = character.profile.ascensionLevels//
				.filter(remainingForCharacter(character).and(allowedForAdventureRank(adventureRank)));
		displayLevels("Remaining character ascension", characterLevels);

		Levels talentLevels = character.profile.talentLevels//
				.filter(remainingForCharacter(character).and(remainingForReachingLevel(6))
						.and(allowedForAdventureRank(adventureRank)));
		MaterialStack talentCost = talentLevels.getCost();
		MaterialStack allTalentsCost = talentCost.times(3);
		System.out.println("[All talents cost]");
		displayStack(allTalentsCost);

		Levels weaponLevels = weapon.profile.ascensionLevels//
				.filter(remainingForWeapon(weapon).and(remainingForReachingLevel(4))
						.and(allowedForAdventureRank(adventureRank)));
		displayLevels("Remaining weapon ascension", weaponLevels);

		MaterialStack totalCost = allTalentsCost.add(characterLevels.getCost()).add(weaponLevels.getCost());
		System.out.println("[TOTAL]");
		displayStack(totalCost);

		MaterialStack availableMaterial = MaterialStack.empty()//
				.add(TWO_STARS.of(VAJRADA), 26)//
				.add(THREE_STARS.of(VAJRADA), 29)//
				.add(FOUR_STARS.of(PRISM), 26)//
				.add(ONE_STAR.of(NECTAR), 196)//
				.add(TWO_STARS.of(NECTAR), 46)//
				.add(THREE_STARS.of(NECTAR), 12)//
				.add(TWO_STARS.of(GUYUN_PILAR), 14)//
				.add(THREE_STARS.of(GUYUN_PILAR), 15)//
				.add(FOUR_STARS.of(GUYUN_PILAR), 2)//
				.add(TWO_STARS.of(PROSPERITY), 41)//
				.add(THREE_STARS.of(PROSPERITY), 53)//
				.add(TWO_STARS.of(SACRIFICIAL_KNIFE), 78)//
				.add(THREE_STARS.of(SACRIFICIAL_KNIFE), 15)//
				.add(FOUR_STARS.of(SACRIFICIAL_KNIFE), 4)//
				.add(ONE_STAR.of(INSIGNIA), 357)//
				.add(TWO_STARS.of(INSIGNIA), 79)//
				.add(THREE_STARS.of(INSIGNIA), 12);

		System.out.println("[AVAILABLE]");
		displayStack(availableMaterial);

		MaterialStack differenceStrict = availableMaterial.minus(totalCost).filter(nonZero().and(noMora()));
		System.out.println("[REMAINING STRICT]");
		displayStack(differenceStrict);
		
		MaterialStack differenceWithConversion = differenceStrict.fillWithConversions();//.filter(nonZero());
		System.out.println("[REMAINING WITH CONVERSION]");
		displayStack(differenceWithConversion);
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
		displayStack(levels.toMap().values().stream().map(Level::getCost).reduce(MaterialStack::add)
				.orElse(MaterialStack.empty()));
	}

	private static <K extends Comparable<K>, T> List<Entry<K, T>> sortedByKeys(Set<Entry<K, T>> entrySet) {
		return entrySet.stream().sorted(Comparator.comparing(Entry::getKey)).collect(Collectors.toList());
	}

	private static void displayStack(MaterialStack stack) {
		stack.toMap().entrySet().stream()//
				.sorted(Comparator.comparing(entry -> entry.getKey(), Material.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getKey() + " x" + entry.getValue());
				});
	}

}
