package fr.sazaju.genshin.item;

import java.util.Objects;

public class ItemEntry {
	private final ItemState<?> itemState;
	private final int quantity;

	ItemEntry(ItemState<?> item, int quantity) {
		this.itemState = item;
		this.quantity = quantity;
	}

	public ItemState<?> getItem() {
		return itemState;
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
	
	public ItemEntry reverse() {
		return ItemEntry.of(getItem(), -getQuantity());
	}

	@Override
	public String toString() {
		return quantity + "x " + itemState;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ItemEntry) {
			ItemEntry that = (ItemEntry) obj;
			return Objects.equals(this.itemState, that.itemState) //
					&& Objects.equals(this.quantity, that.quantity);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemState, quantity);
	}

	public static ItemEntry of(ItemState<?> item) {
		return new ItemEntry(item, 1);
	}

	public static ItemEntry of(ItemState<?> item, int quantity) {
		return new ItemEntry(item, quantity);
	}
}
