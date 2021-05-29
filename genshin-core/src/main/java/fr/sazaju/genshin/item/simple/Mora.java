package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

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
