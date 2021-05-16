package fr.sazaju.genshin.item;

import java.util.Objects;

import fr.sazaju.genshin.Rarity;

public class StackableItem<T extends ItemType> extends Item<T> {

	public StackableItem(T type, Rarity rarity) {
		super(type, rarity);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof StackableItem) {
			StackableItem<?> that = (StackableItem<?>) obj;
			return Objects.equals(this.getType(), that.getType()) //
					&& Objects.equals(this.getRarity(), that.getRarity());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(getType(), getRarity());
	}

}
