package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum EnhancementOre implements ItemType.WithSingleRarity {
	ENHANCEMENT_ORE(ONE_STAR), //
	FINE_ENHANCEMENT_ORE(TWO_STARS), //
	MYSTIC_ENHANCEMENT_ORE(THREE_STARS), //
	;

	private final Rarity rarity;

	EnhancementOre(Rarity rarity) {
		this.rarity = rarity;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public ItemState<EnhancementOre> itemState() {
		return new ItemState<EnhancementOre>(this, getRarity());
	}
}
