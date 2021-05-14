package fr.sazaju.genshin.weapon;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.material.CommonMobDrop.*;
import static fr.sazaju.genshin.material.EliteMobDrop.*;
import static fr.sazaju.genshin.material.WeaponAscensionMaterial.*;
import static fr.sazaju.genshin.weapon.WeaponType.*;

import java.util.Map;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.leveling.Levels;

public enum WeaponProfile {
	/*************/
	/* CATALYSTS */
	/*************/
	LOST_PRAYER_TO_THE_SACRED_WINDS_(CATALYST, FIVE_STARS, 46), //
	MEMORY_OF_DUST_(CATALYST, FIVE_STARS, 46), //
	SKYWARD_ATLAS_(CATALYST, FIVE_STARS, 48), //
	SOLAR_PEARL_(CATALYST, FOUR_STARS, 42), //
	FROSTBEARER_(CATALYST, FOUR_STARS, 42), //
	EYE_OF_PERCEPTION_(CATALYST, FOUR_STARS, 41), //
	THE_WIDSITH_(CATALYST, FOUR_STARS, 42), //
	WINE_AND_SONG_(CATALYST, FOUR_STARS, 44), //
	SACRIFICIAL_FRAGMENTS_(CATALYST, FOUR_STARS, 41), //
	ROYAL_GRIMOIRE_(CATALYST, FOUR_STARS, 44), //
	PROTOTYPE_AMBER_(CATALYST, FOUR_STARS, 42), //
	MAPPA_MARE_(CATALYST, FOUR_STARS, 44), //
	FAVONIUS_CODEX_(CATALYST, FOUR_STARS, 42), //
	BLACKCLIFF_AGATE_(CATALYST, FOUR_STARS, 42), //
	AMBER_CATALYST_(CATALYST, THREE_STARS, 40), //
	MAGIC_GUIDE_(CATALYST, THREE_STARS, 38), //
	THRILLING_TALES_OF_DRAGON_SLAYERS_(CATALYST, THREE_STARS, 39), //
	TWIN_NEPHRITE_(CATALYST, THREE_STARS, 40), //
	OTHERWORLDLY_STORY_(CATALYST, THREE_STARS, 39), //
	EMERALD_ORB_(CATALYST, THREE_STARS, 40), //
	POCKET_GRIMOIRE_(CATALYST, TWO_STARS, 33), //
	APPRENTICE_S_NOTES_(CATALYST, ONE_STAR, 23), //
	/**********/
	/* SWORDS */
	/**********/
	PRIMORDIAL_JADE_CUTTER(SWORD, FIVE_STARS, 44), //
	AQUILA_FAVONIA(SWORD, FIVE_STARS, 48), //
	SUMMIT_SHAPER(SWORD, FIVE_STARS, 46), //
	SKYWARD_BLADE(SWORD, FIVE_STARS, 46), //
	BLACKCLIFF_LONGSWORD(SWORD, FOUR_STARS, 44), //
	FAVONIUS_SWORD(SWORD, FOUR_STARS, 41), //
	IRON_STING(SWORD, FOUR_STARS, 42), //
	LION_S_ROAR(SWORD, FOUR_STARS, 42, //
			Levels.forWeaponAscension(GUYUN_PILAR, SACRIFICIAL_KNIFE, INSIGNIA)//
	), //
	PROTOTYPE_RANCOUR(SWORD, FOUR_STARS, 44), //
	ROYAL_LONGSWORD(SWORD, FOUR_STARS, 42), //
	SACRIFICIAL_SWORD(SWORD, FOUR_STARS, 41), //
	THE_FLUTE(SWORD, FOUR_STARS, 42), //
	FESTERING_DESIRE(SWORD, FOUR_STARS, 42), //
	SWORD_OF_DESCENSION(SWORD, FOUR_STARS, 39), //
	THE_ALLEY_FLASH(SWORD, FOUR_STARS, 45), //
	THE_BLACK_SWORD(SWORD, FOUR_STARS, 42), //
	COOL_STEEL(SWORD, THREE_STARS, 39), //
	DARK_IRON_SWORD(SWORD, THREE_STARS, 39), //
	TRAVELER_S_HANDY_SWORD(SWORD, THREE_STARS, 40), //
	HARBINGER_OF_DAWN(SWORD, THREE_STARS, 39), //
	SKYRIDER_SWORD(SWORD, THREE_STARS, 38), //
	FILLET_BLADE(SWORD, THREE_STARS, 39), //
	SILVER_SWORD(SWORD, TWO_STARS, 33), //
	DULL_BLADE(SWORD, ONE_STAR, 23), //
	;

	public final WeaponType type;
	public final Rarity rarity;
	public final int baseAttack;
	public final Levels ascensionLevels;

	WeaponProfile(WeaponType type, Rarity rarity, int baseAttack, Levels ascensionLevels) {
		this.type = type;
		this.rarity = rarity;
		this.baseAttack = baseAttack;
		this.ascensionLevels = ascensionLevels;
	}

	@Deprecated
	WeaponProfile(WeaponType type, Rarity rarity, int baseAttack) {
		this(type, rarity, baseAttack, Levels.fromMap(Map.of()));
	}

	public Weapon.Builder buildInstance() {
		return new Weapon.Builder(this);
	}

	public Weapon createBasicInstance() {
		return buildInstance().create();
	}
}
