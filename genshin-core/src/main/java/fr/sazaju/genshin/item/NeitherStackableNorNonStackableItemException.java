package fr.sazaju.genshin.item;

@SuppressWarnings("serial")
public class NeitherStackableNorNonStackableItemException extends IllegalStateException {

	public NeitherStackableNorNonStackableItemException(ItemState<?> item) {
		super("Neither stackable or non stackable item: " + item);
	}
}
