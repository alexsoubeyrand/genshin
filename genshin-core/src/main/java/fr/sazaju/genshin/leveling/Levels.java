package fr.sazaju.genshin.leveling;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.simple.EventMaterial.*;
import static fr.sazaju.genshin.item.simple.Mora.*;
import static fr.sazaju.genshin.leveling.Level.Requirement.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fr.sazaju.genshin.character.Character;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.simple.BossDrop;
import fr.sazaju.genshin.item.simple.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.simple.CommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.LocalSpecialty;
import fr.sazaju.genshin.item.simple.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.simple.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.weapon.Weapon;

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

	default ItemStack getCost() {
		return toMap().values().stream()//
				.map(Level::getCost)//
				.reduce(ItemStack::addStack)//
				.orElse(ItemStack.empty());
	}

	public static Levels forCharacterTalent(TalentLevelUpMaterial talentLevelUpMaterial, CommonAscensionMaterial mobDrop, BossDrop bossDrop) {
		return Levels.fromMap(Map.of(//
				2, Level.create(minimumAscensionLevel(2), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 12500, //
						talentLevelUpMaterial.itemState(TWO_STARS), 3, //
						mobDrop.itemState(ONE_STAR), 6//
				))), //
				3, Level.create(minimumAscensionLevel(3), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 17500, //
						talentLevelUpMaterial.itemState(THREE_STARS), 2, //
						mobDrop.itemState(TWO_STARS), 3//
				))), //
				4, Level.create(minimumAscensionLevel(3), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 25000, //
						talentLevelUpMaterial.itemState(THREE_STARS), 4, //
						mobDrop.itemState(TWO_STARS), 4//
				))), //
				5, Level.create(minimumAscensionLevel(4), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 30000, //
						talentLevelUpMaterial.itemState(THREE_STARS), 6, //
						mobDrop.itemState(TWO_STARS), 6//
				))), //
				6, Level.create(minimumAscensionLevel(4), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 37500, //
						talentLevelUpMaterial.itemState(THREE_STARS), 9, //
						mobDrop.itemState(TWO_STARS), 9//
				))), //
				7, Level.create(minimumAscensionLevel(5), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 120000, //
						talentLevelUpMaterial.itemState(FOUR_STARS), 4, //
						mobDrop.itemState(THREE_STARS), 4, //
						bossDrop.itemState(), 1//
				))), //
				8, Level.create(minimumAscensionLevel(5), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 260000, //
						talentLevelUpMaterial.itemState(FOUR_STARS), 6, //
						mobDrop.itemState(THREE_STARS), 6, //
						bossDrop.itemState(), 1//
				))), //
				9, Level.create(minimumAscensionLevel(6), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 450000, //
						talentLevelUpMaterial.itemState(FOUR_STARS), 12, //
						mobDrop.itemState(THREE_STARS), 9, //
						bossDrop.itemState(), 2//
				))), //
				10, Level.create(minimumAscensionLevel(6), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 700000, //
						talentLevelUpMaterial.itemState(FOUR_STARS), 16, //
						mobDrop.itemState(THREE_STARS), 12, //
						bossDrop.itemState(), 2, //
						CROWN_OF_INSIGHT.itemState(), 1//
				))) //
		));
	}

	public static Levels forCharacterAscension(CharacterAscensionMaterial ascensionMaterial, BossDrop bossDrop,
			LocalSpecialty localSpecialty, CommonAscensionMaterial commonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 20000, //
						ascensionMaterial.itemState(TWO_STARS), 1, //
						localSpecialty.itemState(), 3, //
						commonMaterial.itemState(ONE_STAR), 3//
				))), //
				2, Level.create(minimumAdventureRank(25), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 40000, //
						ascensionMaterial.itemState(THREE_STARS), 3, //
						bossDrop.itemState(), 2, //
						localSpecialty.itemState(), 10, //
						commonMaterial.itemState(ONE_STAR), 15//
				))), //
				3, Level.create(minimumAdventureRank(30), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 60000, //
						ascensionMaterial.itemState(THREE_STARS), 6, //
						bossDrop.itemState(), 4, //
						localSpecialty.itemState(), 20, //
						commonMaterial.itemState(TWO_STARS), 12//
				))), //
				4, Level.create(minimumAdventureRank(35), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 80000, //
						ascensionMaterial.itemState(FOUR_STARS), 3, //
						bossDrop.itemState(), 8, //
						localSpecialty.itemState(), 30, //
						commonMaterial.itemState(TWO_STARS), 18//
				))), //
				5, Level.create(minimumAdventureRank(40), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 100000, //
						ascensionMaterial.itemState(FOUR_STARS), 6, //
						bossDrop.itemState(), 12, //
						localSpecialty.itemState(), 45, //
						commonMaterial.itemState(THREE_STARS), 12//
				))), //
				6, Level.create(minimumAdventureRank(50), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 120000, //
						ascensionMaterial.itemState(FIVE_STARS), 6, //
						bossDrop.itemState(), 20, //
						localSpecialty.itemState(), 60, //
						commonMaterial.itemState(THREE_STARS), 24//
				))) //
		));
	}

	public static Levels forWeaponAscension(WeaponAscensionMaterial ascensionMaterial, EliteCommonAscensionMaterial eliteMaterial,
			CommonAscensionMaterial CommonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 5000, //
						ascensionMaterial.itemState(TWO_STARS), 3, //
						eliteMaterial.itemState(TWO_STARS), 3, //
						CommonMaterial.itemState(ONE_STAR), 2//
				))), //
				2, Level.create(minimumAdventureRank(25), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 15000, //
						ascensionMaterial.itemState(THREE_STARS), 3, //
						eliteMaterial.itemState(TWO_STARS), 12, //
						CommonMaterial.itemState(ONE_STAR), 8//
				))), //
				3, Level.create(minimumAdventureRank(30), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 20000, //
						ascensionMaterial.itemState(THREE_STARS), 6, //
						eliteMaterial.itemState(THREE_STARS), 6, //
						CommonMaterial.itemState(TWO_STARS), 6//
				))), //
				4, Level.create(minimumAdventureRank(35), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 30000, //
						ascensionMaterial.itemState(FOUR_STARS), 3, //
						eliteMaterial.itemState(THREE_STARS), 12, //
						CommonMaterial.itemState(TWO_STARS), 9//
				))), //
				5, Level.create(minimumAdventureRank(40), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 35000, //
						ascensionMaterial.itemState(FOUR_STARS), 6, //
						eliteMaterial.itemState(FOUR_STARS), 9, //
						CommonMaterial.itemState(THREE_STARS), 6//
				))), //
				6, Level.create(minimumAdventureRank(50), ItemStack.fromItemsMap(Map.of(//
						MORA.itemState(), 45000, //
						ascensionMaterial.itemState(FIVE_STARS), 4, //
						eliteMaterial.itemState(FOUR_STARS), 18, //
						CommonMaterial.itemState(THREE_STARS), 12//
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
