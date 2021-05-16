package fr.sazaju.genshin.leveling;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.EventMaterial.*;
import static fr.sazaju.genshin.item.Mora.*;
import static fr.sazaju.genshin.leveling.Level.Requirement.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import fr.sazaju.genshin.character.Character;
import fr.sazaju.genshin.item.BossDrop;
import fr.sazaju.genshin.item.CharacterAscensionMaterialMulti;
import fr.sazaju.genshin.item.CommonAscensionMaterial;
import fr.sazaju.genshin.item.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.LocalSpecialty;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.WeaponAscensionMaterial;
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

	default ItemStack getCost() {
		return toMap().values().stream()//
				.map(Level::getCost)//
				.reduce(ItemStack::addStack)//
				.orElse(ItemStack.empty());
	}

	public static Levels forCharacterTalent(TalentLevelUpMaterial talentLevelUpMaterial, CommonAscensionMaterial mobDrop, BossDrop bossDrop) {
		return Levels.fromMap(Map.of(//
				2, Level.create(minimumAscensionLevel(2), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 12500, //
						talentLevelUpMaterial.item(TWO_STARS), 3, //
						mobDrop.item(ONE_STAR), 6//
				))), //
				3, Level.create(minimumAscensionLevel(3), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 17500, //
						talentLevelUpMaterial.item(THREE_STARS), 2, //
						mobDrop.item(TWO_STARS), 3//
				))), //
				4, Level.create(minimumAscensionLevel(3), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 25000, //
						talentLevelUpMaterial.item(THREE_STARS), 4, //
						mobDrop.item(TWO_STARS), 4//
				))), //
				5, Level.create(minimumAscensionLevel(4), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 30000, //
						talentLevelUpMaterial.item(THREE_STARS), 6, //
						mobDrop.item(TWO_STARS), 6//
				))), //
				6, Level.create(minimumAscensionLevel(4), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 37500, //
						talentLevelUpMaterial.item(THREE_STARS), 9, //
						mobDrop.item(TWO_STARS), 9//
				))), //
				7, Level.create(minimumAscensionLevel(5), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 120000, //
						talentLevelUpMaterial.item(FOUR_STARS), 4, //
						mobDrop.item(THREE_STARS), 4, //
						bossDrop.item(), 1//
				))), //
				8, Level.create(minimumAscensionLevel(5), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 260000, //
						talentLevelUpMaterial.item(FOUR_STARS), 6, //
						mobDrop.item(THREE_STARS), 6, //
						bossDrop.item(), 1//
				))), //
				9, Level.create(minimumAscensionLevel(6), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 450000, //
						talentLevelUpMaterial.item(FOUR_STARS), 12, //
						mobDrop.item(THREE_STARS), 9, //
						bossDrop.item(), 2//
				))), //
				10, Level.create(minimumAscensionLevel(6), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 700000, //
						talentLevelUpMaterial.item(FOUR_STARS), 16, //
						mobDrop.item(THREE_STARS), 12, //
						bossDrop.item(), 2, //
						CROWN_OF_INSIGHT.item(), 1//
				))) //
		));
	}

	public static Levels forCharacterAscension(CharacterAscensionMaterialMulti ascensionMaterial, BossDrop bossDrop,
			LocalSpecialty localSpecialty, CommonAscensionMaterial commonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 20000, //
						ascensionMaterial.item(TWO_STARS), 1, //
						localSpecialty.item(), 3, //
						commonMaterial.item(ONE_STAR), 3//
				))), //
				2, Level.create(minimumAdventureRank(25), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 40000, //
						ascensionMaterial.item(THREE_STARS), 3, //
						bossDrop.item(), 2, //
						localSpecialty.item(), 10, //
						commonMaterial.item(ONE_STAR), 15//
				))), //
				3, Level.create(minimumAdventureRank(30), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 60000, //
						ascensionMaterial.item(THREE_STARS), 6, //
						bossDrop.item(), 4, //
						localSpecialty.item(), 20, //
						commonMaterial.item(TWO_STARS), 12//
				))), //
				4, Level.create(minimumAdventureRank(35), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 80000, //
						ascensionMaterial.item(FOUR_STARS), 3, //
						bossDrop.item(), 8, //
						localSpecialty.item(), 30, //
						commonMaterial.item(TWO_STARS), 18//
				))), //
				5, Level.create(minimumAdventureRank(40), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 100000, //
						ascensionMaterial.item(FOUR_STARS), 6, //
						bossDrop.item(), 12, //
						localSpecialty.item(), 45, //
						commonMaterial.item(THREE_STARS), 12//
				))), //
				6, Level.create(minimumAdventureRank(50), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 120000, //
						ascensionMaterial.item(FIVE_STARS), 6, //
						bossDrop.item(), 20, //
						localSpecialty.item(), 60, //
						commonMaterial.item(THREE_STARS), 24//
				))) //
		));
	}

	public static Levels forWeaponAscension(WeaponAscensionMaterial ascensionMaterial, EliteCommonAscensionMaterial eliteMaterial,
			CommonAscensionMaterial CommonMaterial) {
		return Levels.fromMap(Map.of(//
				1, Level.create(minimumAdventureRank(15), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 5000, //
						ascensionMaterial.item(TWO_STARS), 3, //
						eliteMaterial.item(TWO_STARS), 3, //
						CommonMaterial.item(ONE_STAR), 2//
				))), //
				2, Level.create(minimumAdventureRank(25), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 15000, //
						ascensionMaterial.item(THREE_STARS), 3, //
						eliteMaterial.item(TWO_STARS), 12, //
						CommonMaterial.item(ONE_STAR), 8//
				))), //
				3, Level.create(minimumAdventureRank(30), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 20000, //
						ascensionMaterial.item(THREE_STARS), 6, //
						eliteMaterial.item(THREE_STARS), 6, //
						CommonMaterial.item(TWO_STARS), 6//
				))), //
				4, Level.create(minimumAdventureRank(35), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 30000, //
						ascensionMaterial.item(FOUR_STARS), 3, //
						eliteMaterial.item(THREE_STARS), 12, //
						CommonMaterial.item(TWO_STARS), 9//
				))), //
				5, Level.create(minimumAdventureRank(40), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 35000, //
						ascensionMaterial.item(FOUR_STARS), 6, //
						eliteMaterial.item(FOUR_STARS), 9, //
						CommonMaterial.item(THREE_STARS), 6//
				))), //
				6, Level.create(minimumAdventureRank(50), ItemStack.fromItemsMap(Map.of(//
						MORA.item(), 45000, //
						ascensionMaterial.item(FIVE_STARS), 4, //
						eliteMaterial.item(FOUR_STARS), 18, //
						CommonMaterial.item(THREE_STARS), 12//
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
