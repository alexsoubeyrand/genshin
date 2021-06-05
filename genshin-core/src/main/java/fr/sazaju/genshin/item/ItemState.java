package fr.sazaju.genshin.item;

import java.util.Comparator;
import java.util.Objects;

import fr.sazaju.genshin.Rarity;

// An item may evolve in time.
// For example, a weapon can grow in experience.
// An item is thus fundamentally mutable.
// It can change its state during time.
//
// We deal here with item states themselves.
// Each state instance represent a single state.
// An item state is thus immutable.
// 
// This immutability is a requirement.
// Item states are thus relevant keys for maps.
// We can count the number of items in the same state.
public class ItemState<T extends ItemType> {
	private final T type;
	private final Rarity rarity;

	public ItemState(T type, Rarity rarity) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ItemState<?>) {
			ItemState<?> that = (ItemState<?>) obj;
			return Objects.equals(this.type, that.type) //
					&& Objects.equals(this.rarity, that.rarity);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, rarity);
	}

	public static Comparator<ItemState<?>> syntaxicComparator() {
		Comparator<ItemState<?>> type = Comparator.comparing(item -> item.type.toString());
		Comparator<ItemState<?>> rarity = Comparator.comparing(item -> item.rarity.stars);
		Comparator<ItemState<?>> itemComparator = type.thenComparing(rarity);
		return itemComparator;
	}
	
}
