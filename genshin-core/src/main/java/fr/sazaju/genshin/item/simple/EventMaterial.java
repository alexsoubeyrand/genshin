package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum EventMaterial implements ItemType.WithSingleRarity {
	CROWN_OF_INSIGHT;

	@Override
	public Rarity getRarity() {
		return FIVE_STARS;
	}

	@Override
	public ItemState<EventMaterial> itemState() {
		return new ItemState<>(this, getRarity());
	}
}
