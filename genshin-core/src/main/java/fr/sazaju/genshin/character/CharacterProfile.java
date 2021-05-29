package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Element.*;
import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.simple.BossDrop.*;
import static fr.sazaju.genshin.item.simple.CharacterAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.LocalSpecialty.*;
import static fr.sazaju.genshin.item.simple.TalentLevelUpMaterial.*;
import static fr.sazaju.genshin.item.weapon.WeaponCategory.*;

import fr.sazaju.genshin.Element;
import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.item.weapon.WeaponCategory;
import fr.sazaju.genshin.leveling.Levels;

public enum CharacterProfile {
	KEQING("Keqing", FIVE_STARS, ELECTRO, SWORD, //
			Levels.forCharacterTalent(PROSPERITY, NECTAR, RING_OF_BOREAS), //
			Levels.forCharacterAscension(VAJRADA, PRISM, LAPIS, NECTAR)//
	),
	YANFEI("Yanfei", FOUR_STARS, PYRO, CATALYST, //
			Levels.forCharacterTalent(GOLD, INSIGNIA, BLOODJADE_BRANCH), //
			Levels.forCharacterAscension(AGNIDUS, JUVENILE_JADE, NOCTILUCOUS_JADE, INSIGNIA)//
	),
	;

	public final String name;
	public final Rarity rarity;
	public final Element element;
	public final WeaponCategory weaponCategory;
	public final Levels talentLevels;
	public final Levels ascensionLevels;

	private CharacterProfile(//
			String name, //
			Rarity rarity, //
			Element element, //
			WeaponCategory weaponCategory, //
			Levels talentLevels, //
			Levels characterLevels) {
		this.name = name;
		this.rarity = rarity;
		this.element = element;
		this.weaponCategory = weaponCategory;
		this.talentLevels = talentLevels;
		this.ascensionLevels = characterLevels;
	}

	public Character.Builder buildInstance(Weapon weapon) {
		return new Character.Builder(this, weapon);
	}

	public Character createBasicInstance(Weapon weapon) {
		return buildInstance(weapon).create();
	}
}
