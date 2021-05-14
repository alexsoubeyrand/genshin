package fr.sazaju.genshin.leveling;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.leveling.Level.Requirement.*;
import static fr.sazaju.genshin.material.EventMaterial.*;
import static fr.sazaju.genshin.material.Mora.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fr.sazaju.genshin.character.Character;
import fr.sazaju.genshin.material.Book;
import fr.sazaju.genshin.material.BossDrop;
import fr.sazaju.genshin.material.CharacterAscensionMaterial;
import fr.sazaju.genshin.material.CommonMobDrop;
import fr.sazaju.genshin.material.EliteMobDrop;
import fr.sazaju.genshin.material.LocalSpecialty;
import fr.sazaju.genshin.material.MaterialStack;
import fr.sazaju.genshin.material.WeaponAscensionMaterial;
import fr.sazaju.genshin.weapon.Weapon;

public interface Levels {

	default Levels filter(Filter filter) {
		Map<Integer, Level> availableLevels = this.toMap();
		Map<Integer, Level> remainingLevels = availableLevels.entrySet().stream()//
				.filter(filter::test)//
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		return Levels.fromMap(remainingLevels);
	}

	Map<Integer, Level> toMap();

	public static Levels fromMap(Map<Integer, Level> levels) {
		return new Levels() {
			@Override
			public Map<Integer, Level> toMap() {
				return levels;
			}

			@Override
			public String toString() {
				return levels.toString();
			}
		};
	}

	default MaterialStack getCost() {
		return toMap().values().stream()//
				.map(Level::getCost)//
				.reduce(MaterialStack::add)//
				.orElse(MaterialStack.empty());
	}

	public static Levels forCharacterTalent(Book book, CommonMobDrop mobDrop, BossDrop bossDrop) {
		return Levels.fromMap(Map.of(//
				2, Level.create(minimumAscensionLevel(2), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 12500, //
						TWO_STARS.of(book), 3, //
						ONE_STAR.of(mobDrop), 6//
				))), //
				3, Level.create(minimumAscensionLevel(3), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 17500, //
						THREE_STARS.of(book), 2, //
						TWO_STARS.of(mobDrop), 3//
				))), //
				4, Level.create(minimumAscensionLevel(3), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 25000, //
						THREE_STARS.of(book), 4, //
						TWO_STARS.of(mobDrop), 4//
				))), //
				5, Level.create(minimumAscensionLevel(4), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 30000, //
						THREE_STARS.of(book), 6, //
						TWO_STARS.of(mobDrop), 6//
				))), //
				6, Level.create(minimumAscensionLevel(4), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 37500, //
						THREE_STARS.of(book), 9, //
						TWO_STARS.of(mobDrop), 9//
				))), //
				7, Level.create(minimumAscensionLevel(5), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 120000, //
						FOUR_STARS.of(book), 4, //
						THREE_STARS.of(mobDrop), 4, //
						FIVE_STARS.of(bossDrop), 1//
				))), //
				8, Level.create(minimumAscensionLevel(5), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 260000, //
						FOUR_STARS.of(book), 6, //
						THREE_STARS.of(mobDrop), 6, //
						FIVE_STARS.of(bossDrop), 1//
				))), //
				9, Level.create(minimumAscensionLevel(6), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 450000, //
						FOUR_STARS.of(book), 12, //
						THREE_STARS.of(mobDrop), 9, //
						FIVE_STARS.of(bossDrop), 2//
				))), //
				10, Level.create(minimumAscensionLevel(6), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 700000, //
						FOUR_STARS.of(book), 16, //
						THREE_STARS.of(mobDrop), 12, //
						FIVE_STARS.of(bossDrop), 2, //
						FIVE_STARS.of(CROWN_OF_INSIGHT), 1//
				))) //
		));
	}

	public static Levels forCharacterAscension(CharacterAscensionMaterial ascensionMaterial, BossDrop bossDrop,
			LocalSpecialty localSpecialty, CommonMobDrop commonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 20000, //
						TWO_STARS.of(ascensionMaterial), 1, //
						SPECIALTY.of(localSpecialty), 3, //
						ONE_STAR.of(commonMaterial), 3//
				))), //
				2, Level.create(minimumAdventureRank(25), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 40000, //
						THREE_STARS.of(ascensionMaterial), 3, //
						FOUR_STARS.of(bossDrop), 2, //
						SPECIALTY.of(localSpecialty), 10, //
						ONE_STAR.of(commonMaterial), 15//
				))), //
				3, Level.create(minimumAdventureRank(30), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 60000, //
						THREE_STARS.of(ascensionMaterial), 6, //
						FOUR_STARS.of(bossDrop), 4, //
						SPECIALTY.of(localSpecialty), 20, //
						TWO_STARS.of(commonMaterial), 12//
				))), //
				4, Level.create(minimumAdventureRank(35), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 80000, //
						FOUR_STARS.of(ascensionMaterial), 3, //
						FOUR_STARS.of(bossDrop), 8, //
						SPECIALTY.of(localSpecialty), 30, //
						TWO_STARS.of(commonMaterial), 18//
				))), //
				5, Level.create(minimumAdventureRank(40), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 100000, //
						FOUR_STARS.of(ascensionMaterial), 6, //
						FOUR_STARS.of(bossDrop), 12, //
						SPECIALTY.of(localSpecialty), 45, //
						THREE_STARS.of(commonMaterial), 12//
				))), //
				6, Level.create(minimumAdventureRank(50), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 120000, //
						FIVE_STARS.of(ascensionMaterial), 6, //
						FOUR_STARS.of(bossDrop), 20, //
						SPECIALTY.of(localSpecialty), 60, //
						THREE_STARS.of(commonMaterial), 24//
				))) //
		));
	}

	public static Levels forWeaponAscension(WeaponAscensionMaterial ascensionMaterial, EliteMobDrop eliteMaterial,
			CommonMobDrop CommonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 5000, //
						TWO_STARS.of(ascensionMaterial), 3, //
						TWO_STARS.of(eliteMaterial), 3, //
						ONE_STAR.of(CommonMaterial), 2//
				))), //
				2, Level.create(minimumAdventureRank(25), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 15000, //
						THREE_STARS.of(ascensionMaterial), 3, //
						TWO_STARS.of(eliteMaterial), 12, //
						ONE_STAR.of(CommonMaterial), 8//
				))), //
				3, Level.create(minimumAdventureRank(30), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 20000, //
						THREE_STARS.of(ascensionMaterial), 6, //
						THREE_STARS.of(eliteMaterial), 6, //
						TWO_STARS.of(CommonMaterial), 6//
				))), //
				4, Level.create(minimumAdventureRank(35), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 30000, //
						FOUR_STARS.of(ascensionMaterial), 3, //
						THREE_STARS.of(eliteMaterial), 12, //
						TWO_STARS.of(CommonMaterial), 9//
				))), //
				5, Level.create(minimumAdventureRank(40), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 35000, //
						FOUR_STARS.of(ascensionMaterial), 6, //
						FOUR_STARS.of(eliteMaterial), 9, //
						THREE_STARS.of(CommonMaterial), 6//
				))), //
				6, Level.create(minimumAdventureRank(50), MaterialStack.fromMap(Map.of(//
						THREE_STARS.of(MORA), 45000, //
						FIVE_STARS.of(ascensionMaterial), 4, //
						FOUR_STARS.of(eliteMaterial), 18, //
						THREE_STARS.of(CommonMaterial), 12//
				))) //
		));
	}

	public interface Filter {
		boolean test(Entry<Integer, Level> entry);

		public static Filter remainingForReachingLevel(int level) {
			return entry -> entry.getKey() <= level;
		}

		public static Filter remainingForCharacter(Character character) {
			return entry -> entry.getKey() > character.ascensionLevel;
		}

		public static Filter remainingForWeapon(Weapon weapon) {
			return entry -> entry.getKey() > weapon.ascensionLevel;
		}

		public static Filter allowedForCharacter(Character character) {
			return entry -> entry.getValue().getRequirement().testCharacter(character);
		}

		public static Filter allowedForAdventureRank(int adventureRank) {
			return entry -> entry.getValue().getRequirement().testAdventureRank(adventureRank);
		}

		default Filter and(Filter otherFilter) {
			return entry -> this.test(entry) && otherFilter.test(entry);
		}

		default Filter or(Filter otherFilter) {
			return entry -> this.test(entry) || otherFilter.test(entry);
		}
	}

}
