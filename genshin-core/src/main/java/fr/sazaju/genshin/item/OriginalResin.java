package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum OriginalResin implements ItemType.WithSingleRarity {
	ORIGINAL_RESIN;

	@Override
	public Rarity getRarity() {
		return FOUR_STARS;
	}

	@Override
	public StackableItem<OriginalResin> item() {
		return new StackableItem<>(this, getRarity());
	}
}
