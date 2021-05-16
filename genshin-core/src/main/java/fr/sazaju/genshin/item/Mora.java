package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum Mora implements ItemType.WithSingleRarity {
	MORA;

	@Override
	public Rarity getRarity() {
		return THREE_STARS;
	}

	@Override
	public StackableItem<Mora> item() {
		return new StackableItem<>(this, getRarity());
	}
}
