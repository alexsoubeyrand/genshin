package fr.sazaju.genshin.item;

public interface ItemEntry {
	Item<?> getItem();

	int getQuantity();

	public static ItemEntry of(StackableItem<?> item, int quantity) {
		return new ItemEntry() {

			@Override
			public Item<?> getItem() {
				return item;
			}

			@Override
			public int getQuantity() {
				return quantity;
			}
		};
	}

	public static ItemEntry of(Item<?> item) {
		return new ItemEntry() {

			@Override
			public Item<?> getItem() {
				return item;
			}

			@Override
			public int getQuantity() {
				return 1;
			}
		};
	}
}
