package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

public enum EventMaterial implements ItemType.WithSingleRarity {
	CROWN_OF_INSIGHT;

	@Override
	public Rarity getRarity() {
		return FIVE_STARS;
	}

	@Override
	public StackableItem<EventMaterial> item() {
		return new StackableItem<>(this, getRarity());
	}
}
