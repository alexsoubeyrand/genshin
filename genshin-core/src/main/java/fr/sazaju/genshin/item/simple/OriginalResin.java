package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum OriginalResin implements ItemType.WithSingleRarity {
	ORIGINAL_RESIN;

	@Override
	public Rarity getRarity() {
		return FOUR_STARS;
	}

	@Override
	public ItemState<OriginalResin> itemState() {
		return new ItemState<>(this, getRarity());
	}
}
