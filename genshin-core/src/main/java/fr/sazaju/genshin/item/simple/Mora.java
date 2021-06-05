package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum Mora implements ItemType.WithSingleRarity {
	MORA;

	@Override
	public Rarity getRarity() {
		return THREE_STARS;
	}

	@Override
	public ItemState<Mora> itemState() {
		return new ItemState<>(this, getRarity());
	}
}
