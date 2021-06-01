package fr.sazaju.genshin.item;

import java.util.Objects;

public class ItemEntry {
	private final Item<?> item;
	private final int quantity;

	ItemEntry(Item<?> item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public Item<?> getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public ItemEntry addQuantity(int quantity) {
		return ItemEntry.of(getItem(), getQuantity() + quantity);
	}

	public ItemEntry removeQuantity(int quantity) {
		return ItemEntry.of(getItem(), getQuantity() - quantity);
	}

	@Override
	public String toString() {
		return quantity + "x " + item;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ItemEntry) {
			ItemEntry that = (ItemEntry) obj;
			return Objects.equals(this.item, that.item) //
					&& Objects.equals(this.quantity, that.quantity);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(item, quantity);
	}

	public static ItemEntry of(Item<?> item) {
		return new ItemEntry(item, 1);
	}

	public static ItemEntry of(Item<?> item, int quantity) {
		return new ItemEntry(item, quantity);
	}
}
