package fr.sazaju.genshin;

import static fr.sazaju.genshin.item.simple.Mora.*;
import static fr.sazaju.genshin.item.simple.OriginalResin.*;
import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.recipe.Recipe;

// TODO Test coverage
public class PlayerData implements Iterable<ItemEntry> {

	private final Map<ItemState<?>, Integer> map;

	private PlayerData(Map<ItemState<?>, Integer> items) {
		this.map = Collections.unmodifiableMap(items);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int getQuantity(ItemState<?> item) {
		return map.getOrDefault(item, 0);
	}

	public int getMoras() {
		return getQuantity(MORA.itemState());
	}

	public int getResins() {
		return getQuantity(ORIGINAL_RESIN.itemState());
	}

	public PlayerData update(ItemState<?> item, int quantity) {
		int currentQuantity = getQuantity(item);
		int newQuantity = currentQuantity + quantity;
		if (newQuantity < 0) {
			throw new NotEnoughItemsException(item, currentQuantity, -quantity);
		} else if (newQuantity == 0) {
			Map<ItemState<?>, Integer> newMap = new HashMap<>(this.map);
			newMap.remove(item);
			return new PlayerData(newMap);
		} else {
			Map<ItemState<?>, Integer> newMap = new HashMap<>(this.map);
			newMap.put(item, newQuantity);
			return new PlayerData(newMap);
		}
	}

	public PlayerData updateAll(Stream<ItemEntry> updates) {
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
		return new PlayerData(newMap);
	}

	public Stream<ItemEntry> stream() {
		return map.entrySet().stream().map(entry -> ItemEntry.of(entry.getKey(), entry.getValue()));
	}

	public PlayerData update(Recipe recipe) {
		Stream<ItemEntry> costs = recipe.streamCosts().map(ItemEntry::reverse);
		Stream<ItemEntry> products = recipe.streamProducts();
		return this.updateAll(Stream.concat(products, costs));
	}

	@Override
	public Iterator<ItemEntry> iterator() {
		return stream().iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof PlayerData) {
			PlayerData that = (PlayerData) obj;
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

	public static PlayerData empty() {
		return fromMap(Map.of());
	}

	public static PlayerData fromMap(Map<ItemState<?>, Integer> items) {
		return new PlayerData(items);
	}

	public static PlayerData fromItemEntries(Stream<ItemEntry> entries) {
		return PlayerData.empty().updateAll(entries);
	}

	public boolean contains(Iterable<ItemEntry> content) {
		return !StreamSupport.stream(content.spliterator(), false)//
				.filter(entry -> entry.getQuantity() > getQuantity(entry.getItem()))//
				.findAny().isPresent();
	}

	@SuppressWarnings("serial")
	public static class NotEnoughItemsException extends IllegalArgumentException {
		public NotEnoughItemsException(ItemState<?> item, int currentQuantity, int quantityToRemove) {
			super("Cannot remove " + quantityToRemove + " of " + item + " if there is only " + currentQuantity);
		}
	}
}
