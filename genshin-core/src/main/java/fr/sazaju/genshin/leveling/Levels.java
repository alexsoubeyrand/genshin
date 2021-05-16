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
				.reduce(MaterialStack::addStack)//
				.orElse(MaterialStack.empty());
	}

	public static Levels forCharacterTalent(Book book, CommonMobDrop mobDrop, BossDrop bossDrop) {
		return Levels.fromMap(Map.of(//
				2, Level.create(minimumAscensionLevel(2), MaterialStack.fromMap(Map.of(//
						MORA.material(), 12500, //
						book.material(TWO_STARS), 3, //
						mobDrop.material(ONE_STAR), 6//
				))), //
				3, Level.create(minimumAscensionLevel(3), MaterialStack.fromMap(Map.of(//
						MORA.material(), 17500, //
						book.material(THREE_STARS), 2, //
						mobDrop.material(TWO_STARS), 3//
				))), //
				4, Level.create(minimumAscensionLevel(3), MaterialStack.fromMap(Map.of(//
						MORA.material(), 25000, //
						book.material(THREE_STARS), 4, //
						mobDrop.material(TWO_STARS), 4//
				))), //
				5, Level.create(minimumAscensionLevel(4), MaterialStack.fromMap(Map.of(//
						MORA.material(), 30000, //
						book.material(THREE_STARS), 6, //
						mobDrop.material(TWO_STARS), 6//
				))), //
				6, Level.create(minimumAscensionLevel(4), MaterialStack.fromMap(Map.of(//
						MORA.material(), 37500, //
						book.material(THREE_STARS), 9, //
						mobDrop.material(TWO_STARS), 9//
				))), //
				7, Level.create(minimumAscensionLevel(5), MaterialStack.fromMap(Map.of(//
						MORA.material(), 120000, //
						book.material(FOUR_STARS), 4, //
						mobDrop.material(THREE_STARS), 4, //
						bossDrop.material(), 1//
				))), //
				8, Level.create(minimumAscensionLevel(5), MaterialStack.fromMap(Map.of(//
						MORA.material(), 260000, //
						book.material(FOUR_STARS), 6, //
						mobDrop.material(THREE_STARS), 6, //
						bossDrop.material(), 1//
				))), //
				9, Level.create(minimumAscensionLevel(6), MaterialStack.fromMap(Map.of(//
						MORA.material(), 450000, //
						book.material(FOUR_STARS), 12, //
						mobDrop.material(THREE_STARS), 9, //
						bossDrop.material(), 2//
				))), //
				10, Level.create(minimumAscensionLevel(6), MaterialStack.fromMap(Map.of(//
						MORA.material(), 700000, //
						book.material(FOUR_STARS), 16, //
						mobDrop.material(THREE_STARS), 12, //
						bossDrop.material(), 2, //
						CROWN_OF_INSIGHT.material, 1//
				))) //
		));
	}

	public static Levels forCharacterAscension(CharacterAscensionMaterial ascensionMaterial, BossDrop bossDrop,
			LocalSpecialty localSpecialty, CommonMobDrop commonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), MaterialStack.fromMap(Map.of(//
						MORA.material(), 20000, //
						ascensionMaterial.material(TWO_STARS), 1, //
						localSpecialty.material(), 3, //
						commonMaterial.material(ONE_STAR), 3//
				))), //
				2, Level.create(minimumAdventureRank(25), MaterialStack.fromMap(Map.of(//
						MORA.material(), 40000, //
						ascensionMaterial.material(THREE_STARS), 3, //
						bossDrop.material(), 2, //
						localSpecialty.material(), 10, //
						commonMaterial.material(ONE_STAR), 15//
				))), //
				3, Level.create(minimumAdventureRank(30), MaterialStack.fromMap(Map.of(//
						MORA.material(), 60000, //
						ascensionMaterial.material(THREE_STARS), 6, //
						bossDrop.material(), 4, //
						localSpecialty.material(), 20, //
						commonMaterial.material(TWO_STARS), 12//
				))), //
				4, Level.create(minimumAdventureRank(35), MaterialStack.fromMap(Map.of(//
						MORA.material(), 80000, //
						ascensionMaterial.material(FOUR_STARS), 3, //
						bossDrop.material(), 8, //
						localSpecialty.material(), 30, //
						commonMaterial.material(TWO_STARS), 18//
				))), //
				5, Level.create(minimumAdventureRank(40), MaterialStack.fromMap(Map.of(//
						MORA.material(), 100000, //
						ascensionMaterial.material(FOUR_STARS), 6, //
						bossDrop.material(), 12, //
						localSpecialty.material(), 45, //
						commonMaterial.material(THREE_STARS), 12//
				))), //
				6, Level.create(minimumAdventureRank(50), MaterialStack.fromMap(Map.of(//
						MORA.material(), 120000, //
						ascensionMaterial.material(FIVE_STARS), 6, //
						bossDrop.material(), 20, //
						localSpecialty.material(), 60, //
						commonMaterial.material(THREE_STARS), 24//
				))) //
		));
	}

	public static Levels forWeaponAscension(WeaponAscensionMaterial ascensionMaterial, EliteMobDrop eliteMaterial,
			CommonMobDrop CommonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), MaterialStack.fromMap(Map.of(//
						MORA.material(), 5000, //
						ascensionMaterial.material(TWO_STARS), 3, //
						eliteMaterial.material(TWO_STARS), 3, //
						CommonMaterial.material(ONE_STAR), 2//
				))), //
				2, Level.create(minimumAdventureRank(25), MaterialStack.fromMap(Map.of(//
						MORA.material(), 15000, //
						ascensionMaterial.material(THREE_STARS), 3, //
						eliteMaterial.material(TWO_STARS), 12, //
						CommonMaterial.material(ONE_STAR), 8//
				))), //
				3, Level.create(minimumAdventureRank(30), MaterialStack.fromMap(Map.of(//
						MORA.material(), 20000, //
						ascensionMaterial.material(THREE_STARS), 6, //
						eliteMaterial.material(THREE_STARS), 6, //
						CommonMaterial.material(TWO_STARS), 6//
				))), //
				4, Level.create(minimumAdventureRank(35), MaterialStack.fromMap(Map.of(//
						MORA.material(), 30000, //
						ascensionMaterial.material(FOUR_STARS), 3, //
						eliteMaterial.material(THREE_STARS), 12, //
						CommonMaterial.material(TWO_STARS), 9//
				))), //
				5, Level.create(minimumAdventureRank(40), MaterialStack.fromMap(Map.of(//
						MORA.material(), 35000, //
						ascensionMaterial.material(FOUR_STARS), 6, //
						eliteMaterial.material(FOUR_STARS), 9, //
						CommonMaterial.material(THREE_STARS), 6//
				))), //
				6, Level.create(minimumAdventureRank(50), MaterialStack.fromMap(Map.of(//
						MORA.material(), 45000, //
						ascensionMaterial.material(FIVE_STARS), 4, //
						eliteMaterial.material(FOUR_STARS), 18, //
						CommonMaterial.material(THREE_STARS), 12//
				))) //
		));
	}

	public interface Filter {
		boolean test(Entry<Integer, Level> entry);

		public static Filter atMostLevel(int level) {
			return entry -> entry.getKey() <= level;
		}

		public static Filter remainingForTalent(int talentLevel) {
			return entry -> entry.getKey() > talentLevel;
		}

		public static Filter remainingForCharacterAscension(Character character) {
			return entry -> entry.getKey() > character.ascensionLevel;
		}

		public static Filter remainingForWeaponAscension(Weapon weapon) {
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
