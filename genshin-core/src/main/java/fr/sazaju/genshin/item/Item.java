package fr.sazaju.genshin.item;

import java.util.Comparator;

import fr.sazaju.genshin.Rarity;

public class Item<T extends ItemType> {
	private final T type;
	private final Rarity rarity;

	public Item(T type, Rarity rarity) {
		if (!type.hasRarity(rarity)) {
			throw new IllegalArgumentException(String.format("'%s' does not exist with rarity '%s'", type, rarity));
		}
		this.type = type;
		this.rarity = rarity;
	}

	public T getType() {
		return type;
	}

	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public String toString() {
		if (type instanceof ItemType.WithSingleRarity) {
			return type.toString();
		} else {
			return type + " " + rarity;
		}
	}

	public static Comparator<Item<?>> syntaxicComparator() {
		Comparator<Item<?>> type = Comparator.comparing(item -> item.type.toString());
		Comparator<Item<?>> rarity = Comparator.comparing(item -> item.rarity.stars);
		Comparator<Item<?>> itemComparator = type.thenComparing(rarity);
		return itemComparator;
	}
	
}
