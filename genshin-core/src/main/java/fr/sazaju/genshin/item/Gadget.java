package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum Gadget implements ItemType.WithSingleRarity {
	ADEPTI_SEEKER_S_STOVE(THREE_STARS), //
	ANEMOCULUS_RESONANCE_STONE(THREE_STARS), //
	GEOCULUS_RESONANCE_STONE(THREE_STARS), //
	WARMING_BOTTLE(THREE_STARS), //
	ANEMO_TREASURE_COMPASS(FOUR_STARS), //
	GEO_TREASURE_COMPASS(FOUR_STARS), //
	NRE_MENU_30(FOUR_STARS), //
	PORTABLE_WAYPOINT(FOUR_STARS), //
	WIND_CATCHER(FOUR_STARS);

	private final Rarity rarity;

	Gadget(Rarity rarity) {
		this.rarity = rarity;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public Item<Gadget> item() {
		return new Item<Gadget>(this, getRarity());
	}
}
