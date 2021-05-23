package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

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
	public StackableItem<EnhancementOre> item() {
		return new StackableItem<EnhancementOre>(this, getRarity());
	}
}
