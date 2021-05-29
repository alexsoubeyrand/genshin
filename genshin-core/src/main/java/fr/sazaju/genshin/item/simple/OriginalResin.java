package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

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
