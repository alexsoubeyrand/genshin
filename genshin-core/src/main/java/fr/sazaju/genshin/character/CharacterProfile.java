package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.character.Element.*;
import static fr.sazaju.genshin.character.Rarity.*;
import static fr.sazaju.genshin.character.Weapon.*;
import static fr.sazaju.genshin.material.AscensionMaterial.*;
import static fr.sazaju.genshin.material.Book.*;
import static fr.sazaju.genshin.material.BossDrop.*;
import static fr.sazaju.genshin.material.LocalSpecialty.*;
import static fr.sazaju.genshin.material.MobDrop.*;

public enum CharacterProfile {
	KEQING("Keqing", FIVE_STARS, ELECTRO, SWORD, //
			Levels.forCharacterTalent(PROSPERITY, NECTAR, RING_OF_BOREAS), //
			AscensionLevels.basedOn(VAJRADA, PRISM, LAPIS, NECTAR)//
	);

	public final String name;
	public final Rarity rarity;
	public final Element element;
	public final Weapon weapon;
	public final Levels talentLevels;
	public final AscensionLevels ascensionLevels;

	private CharacterProfile(//
			String name, //
			Rarity rarity, //
			Element element, //
			Weapon weapon, //
			Levels talentLevels, //
			AscensionLevels characterLevels) {
		this.name = name;
		this.rarity = rarity;
		this.element = element;
		this.weapon = weapon;
		this.talentLevels = talentLevels;
		this.ascensionLevels = characterLevels;
	}
}
