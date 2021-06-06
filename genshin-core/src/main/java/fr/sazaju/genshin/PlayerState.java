package fr.sazaju.genshin;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;

public class PlayerState implements Iterable<ItemEntry> {

	private final Map<ItemState<?>, Integer> map;

	private PlayerState(Map<ItemState<?>, Integer> items) {
		this.map = Collections.unmodifiableMap(items);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int getQuantity(ItemState<?> item) {
		return map.getOrDefault(item, 0);
	}

	public boolean contains(Stream<ItemEntry> content) {
		return !content//
				.filter(entry -> entry.getQuantity() > getQuantity(entry.getItem()))//
				.findAny().isPresent();
	}

	public Stream<ItemEntry> stream() {
		return map.entrySet().stream().map(ItemEntry::fromMapEntry);
	}

	@Override
	public Iterator<ItemEntry> iterator() {
		return stream().iterator();
	}

	public PlayerState update(Stream<ItemEntry> updates) {
		Map<ItemState<?>, Integer> newMap = new HashMap<>(this.map);
		updates.forEach(entry -> {
			ItemState<?> item = entry.getItem();
			int quantity = entry.getQuantity();
			int currentQuantity = newMap.getOrDefault(item, 0);
			int newQuantity = currentQuantity + quantity;
			if (newQuantity < 0) {
				throw new NotEnoughItemsException(item, currentQuantity, -quantity);
			} else if (newQuantity == 0) {
				newMap.remove(item);
			} else {
				newMap.put(item, newQuantity);
			}
		});
		return new PlayerState(newMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof PlayerState) {
			PlayerState that = (PlayerState) obj;
			return Objects.equals(this.map, that.map);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public String toString() {
		return stream().collect(toList()).toString();
	}

	public static PlayerState empty() {
		return fromMap(Map.of());
	}

	public static PlayerState fromMap(Map<ItemState<?>, Integer> items) {
		return new PlayerState(items);
	}

	public static PlayerState fromItemEntries(Stream<ItemEntry> entries) {
		return PlayerState.empty().update(entries);
	}

	@SuppressWarnings("serial")
	public static class NotEnoughItemsException extends IllegalArgumentException {
		public NotEnoughItemsException(ItemState<?> item, int currentQuantity, int quantityToRemove) {
			super("Cannot remove " + quantityToRemove + " of " + item + " if there is only " + currentQuantity);
		}
	}
}
