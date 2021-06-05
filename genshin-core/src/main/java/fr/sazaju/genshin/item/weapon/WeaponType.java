package fr.sazaju.genshin.item.weapon;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.simple.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.WeaponAscensionMaterial.*;
import static fr.sazaju.genshin.item.weapon.WeaponCategory.*;

import java.util.Map;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.leveling.Levels;

public enum WeaponType implements ItemType.WithSingleRarity {
	/********/
	/* BOWS */
	/********/
	COMPOUND_BOW(BOW, FOUR_STARS, 41), //
	PROTOTYPE_CRESCENT(BOW, FOUR_STARS, 42), //
	/*************/
	/* CATALYSTS */
	/*************/
	AMBER_CATALYST(CATALYST, THREE_STARS, 40), //
	APPRENTICE_S_NOTES(CATALYST, ONE_STAR, 23), //
	BLACKCLIFF_AGATE(CATALYST, FOUR_STARS, 42), //
	EMERALD_ORB(CATALYST, THREE_STARS, 40), //
	EYE_OF_PERCEPTION(CATALYST, FOUR_STARS, 41), //
	FAVONIUS_CODEX(CATALYST, FOUR_STARS, 42), //
	FROSTBEARER(CATALYST, FOUR_STARS, 42), //
	LOST_PRAYER_TO_THE_SACRED_WINDS(CATALYST, FIVE_STARS, 46), //
	MAGIC_GUIDE(CATALYST, THREE_STARS, 38), //
	MAPPA_MARE(CATALYST, FOUR_STARS, 44), //
	MEMORY_OF_DUST(CATALYST, FIVE_STARS, 46), //
	OTHERWORLDLY_STORY(CATALYST, THREE_STARS, 39), //
	POCKET_GRIMOIRE(CATALYST, TWO_STARS, 33), //
	PROTOTYPE_AMBER(CATALYST, FOUR_STARS, 42), //
	ROYAL_GRIMOIRE(CATALYST, FOUR_STARS, 44), //
	SACRIFICIAL_FRAGMENTS(CATALYST, FOUR_STARS, 41), //
	SKYWARD_ATLAS(CATALYST, FIVE_STARS, 48), //
	SOLAR_PEARL(CATALYST, FOUR_STARS, 42), //
	THE_WIDSITH(CATALYST, FOUR_STARS, 42), //
	THRILLING_TALES_OF_DRAGON_SLAYERS(CATALYST, THREE_STARS, 39), //
	TWIN_NEPHRITE(CATALYST, THREE_STARS, 40), //
	WINE_AND_SONG(CATALYST, FOUR_STARS, 44), //
	/*************/
	/* CLAYMORES */
	/*************/
	PROTOTYPE_ARCHAIC(CLAYMORE, FOUR_STARS, 44), //
	SNOW_TOMBED_STARSILVER(CLAYMORE, FOUR_STARS, 44), //
	WHITEBLIND(CLAYMORE, FOUR_STARS, 42), //
	/************/
	/* POLEARMS */
	/************/
	CRESCENT_PIKE(POLEARM, FOUR_STARS, 44), //
	DRAGONSPINE_SPEAR(POLEARM, FOUR_STARS, 41), //
	PROTOTYPE_STARGLITTER(POLEARM, FOUR_STARS, 42), //
	/**********/
	/* SWORDS */
	/**********/
	AQUILA_FAVONIA(SWORD, FIVE_STARS, 48), //
	BLACKCLIFF_LONGSWORD(SWORD, FOUR_STARS, 44), //
	COOL_STEEL(SWORD, THREE_STARS, 39), //
	DARK_IRON_SWORD(SWORD, THREE_STARS, 39), //
	DULL_BLADE(SWORD, ONE_STAR, 23), //
	FAVONIUS_SWORD(SWORD, FOUR_STARS, 41), //
	FESTERING_DESIRE(SWORD, FOUR_STARS, 42), //
	FILLET_BLADE(SWORD, THREE_STARS, 39), //
	HARBINGER_OF_DAWN(SWORD, THREE_STARS, 39), //
	IRON_STING(SWORD, FOUR_STARS, 42), //
	LION_S_ROAR(SWORD, FOUR_STARS, 42, //
			Levels.forWeaponAscension(GUYUN_PILAR, SACRIFICIAL_KNIFE, INSIGNIA)//
	), //
	PRIMORDIAL_JADE_CUTTER(SWORD, FIVE_STARS, 44), //
	PROTOTYPE_RANCOUR(SWORD, FOUR_STARS, 44), //
	ROYAL_LONGSWORD(SWORD, FOUR_STARS, 42), //
	SACRIFICIAL_SWORD(SWORD, FOUR_STARS, 41), //
	SILVER_SWORD(SWORD, TWO_STARS, 33), //
	SKYRIDER_SWORD(SWORD, THREE_STARS, 38), //
	SKYWARD_BLADE(SWORD, FIVE_STARS, 46), //
	SUMMIT_SHAPER(SWORD, FIVE_STARS, 46), //
	SWORD_OF_DESCENSION(SWORD, FOUR_STARS, 39), //
	THE_ALLEY_FLASH(SWORD, FOUR_STARS, 45), //
	THE_BLACK_SWORD(SWORD, FOUR_STARS, 42), //
	THE_FLUTE(SWORD, FOUR_STARS, 42), //
	TRAVELER_S_HANDY_SWORD(SWORD, THREE_STARS, 40), //
	;

	public final WeaponCategory category;
	public final Rarity rarity;
	public final int baseAttack;
	public final Levels ascensionLevels;

	WeaponType(WeaponCategory category, Rarity rarity, int baseAttack, Levels ascensionLevels) {
		this.category = category;
		this.rarity = rarity;
		this.baseAttack = baseAttack;
		this.ascensionLevels = ascensionLevels;
	}

	@Deprecated
	WeaponType(WeaponCategory type, Rarity rarity, int baseAttack) {
		this(type, rarity, baseAttack, Levels.fromMap(Map.of()));
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public Weapon itemState() {
		return createBasicInstance();
	}

	public Weapon.Builder buildInstance() {
		return new Weapon.Builder(this);
	}

	public Weapon createBasicInstance() {
		return buildInstance().create();
	}
}
