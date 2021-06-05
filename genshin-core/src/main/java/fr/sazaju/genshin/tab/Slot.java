package fr.sazaju.genshin.tab;

import fr.sazaju.genshin.item.ItemState;

public interface Slot {

	public ItemState<?> getItem();

	public static class Stack implements Slot {

		private final ItemState<?> itemState;
		private final int quantity;

		public Stack(ItemState<?> item, int quantity) {
			this.itemState = item;
			this.quantity = quantity;
		}

		@Override
		public ItemState<?> getItem() {
			return itemState;
		}

		public int getQuantity() {
			return quantity;
		}

		@Override
		public String toString() {
			return itemState + " x" + quantity;
		}

	}

	public static class Single implements Slot {

		private final ItemState<?> itemState;

		public Single(ItemState<?> item) {
			this.itemState = item;
		}

		@Override
		public ItemState<?> getItem() {
			return itemState;
		}

		@Override
		public String toString() {
			return itemState.toString();
		}

	}

	public static Stack stack(ItemState<?> item, int quantity) {
		return new Stack(item, quantity);
	}

	public static Single single(ItemState<?> item) {
		return new Single(item);
	}

}
