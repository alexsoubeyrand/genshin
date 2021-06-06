package fr.sazaju.genshin.recipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemState;

public class Recipe {

	private final Map<ItemState<?>, Integer> map;

	private Recipe(Map<ItemState<?>, Integer> map) {
		this.map = Collections.unmodifiableMap(map);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Map<ItemState<?>, Integer> getDiff() {
		return map;
	}

	public int getQuantity(ItemState<?> item) {
		return map.getOrDefault(item, 0);
	}

	public Stream<ItemEntry> streamProducts() {
		return map.entrySet().stream()//
				.filter(entry -> entry.getValue() > 0)//
				.map(ItemEntry::fromMapEntry);
	}

	public int getProducedQuantity(ItemState<?> item) {
		return Math.max(0, map.getOrDefault(item, 0));
	}

	public Stream<ItemEntry> streamCosts() {
		return map.entrySet().stream()//
				.filter(entry -> entry.getValue() < 0)//
				.map(ItemEntry::fromMapEntry)//
				.map(entry -> entry.reverse());
	}

	public int getConsumedQuantity(ItemState<?> item) {
		return -Math.min(0, map.getOrDefault(item, 0));
	}

	public Recipe consumes(ItemState<?> item, int quantity) {
		return add(Recipe.fromDiff(Map.of(item, -quantity)));
	}

	public Recipe produces(ItemState<?> item, int quantity) {
		return add(Recipe.fromDiff(Map.of(item, quantity)));
	}

	public Recipe add(Recipe recipe) {
		Map<ItemState<?>, Integer> newMap = new HashMap<ItemState<?>, Integer>(map);
		recipe.map.entrySet().forEach(entry -> {
			newMap.merge(entry.getKey(), entry.getValue(), (v1, v2) -> {
				int sum = v1 + v2;
				return sum == 0 ? null : sum;
			});
		});
		return Recipe.fromDiff(newMap);
	}

	public Recipe times(int multiplier) {
		return Recipe.fromDiff(map.entrySet().stream()//
				.flatMap(entry -> Stream.of(Map.entry(entry.getKey(), entry.getValue() * multiplier)))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public Recipe reverse() {
		return times(-1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Recipe) {
			Recipe that = (Recipe) obj;
			return Objects.equals(this.map, that.map);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public String toString() {
		Function<Stream<ItemEntry>, String> stackFormater = entries -> {
			String content = entries//
					.map(entry -> (entry.getQuantity() == 1 ? "" : entry.getQuantity() + " x ") + entry.getItem())//
					.collect(Collectors.joining(" + "));
			return content.isBlank() ? "∅" : content;
		};
		return stackFormater.apply(streamCosts()) + " => " + stackFormater.apply(streamProducts());
	}

	public static Recipe empty() {
		return new Recipe(Map.of());
	}

	public static Recipe fromDiff(Map<ItemState<?>, Integer> map) {
		Map<ItemState<?>, Integer> cleanedMap = new HashMap<>();
		map.entrySet().forEach(entry -> {
			Integer value = entry.getValue();
			if (value == 0) {
				// Ignore
			} else {
				cleanedMap.put(entry.getKey(), value);
			}
		});
		return new Recipe(cleanedMap);
	}
}
