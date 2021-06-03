package fr.sazaju.genshin.item;

import fr.sazaju.genshin.Rarity;

public abstract class NonStackableItem<T extends ItemType> extends Item<T> {

	public NonStackableItem(T type, Rarity rarity) {
		super(type, rarity);
	}

	public abstract NonStackableItem<T> duplicate();

}
