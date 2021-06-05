package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum Potion implements ItemType.WithSingleRarity {
	/********/
	/* OILS */
	/********/
	FLAMING_ESSENTIAL_OIL(), //
	FROSTING_ESSENTIAL_OIL(), //
	GUSHING_ESSENTIAL_OIL(), //
	SHOCKING_ESSENTIAL_OIL(), //
	STREAMING_ESSENTIAL_OIL(), //
	UNMOVING_ESSENTIAL_OIL(),
	/***********/
	/* POTIONS */
	/***********/
	DESICCANT_POTION(), //
	DUSTPROOF_POTION(), //
	FROSTSHIELD_POTION(), //
	HEATSHIELD_POTION(), //
	INSULATION_POTION(), //
	WINDBARRIER_POTION(),//
	;

	@Override
	public Rarity getRarity() {
		return THREE_STARS;
	}

	@Override
	public ItemState<Potion> itemState() {
		return new ItemState<Potion>(this, getRarity());
	}
}
