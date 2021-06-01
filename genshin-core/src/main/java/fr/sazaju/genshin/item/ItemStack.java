package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.item.simple.Mora.*;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerData;

// TODO Deprecate
// TODO Remove
public class ItemStack implements Iterable<ItemEntry> {
	private final Map<Item<?>, Integer> map;

	private ItemStack(Map<Item<?>, Integer> map) {
		Objects.requireNonNull(map);
		this.map = map;
	}
	
	public Map<Item<?>, Integer> getMap() {
		return Collections.unmodifiableMap(map);
	}

	public static ItemStack fromItemsMap(Map<Item<?>, Integer> map) {
		return new ItemStack(map);
	}

	public static ItemStack fromTypesMap(Map<ItemType.WithSingleRarity, Integer> map) {
		return fromItemsMap(map.entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey().item(), entry.getValue()))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public static ItemStack fromPlayerData(PlayerData data) {
		return fromItemsMap(data.stream().collect(toMap(//
				ItemEntry::getItem, //
				ItemEntry::getQuantity//
		)));
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Set<Item<?>> getItems() {
		return map.keySet();
	}

	public int getQuantity(Item<?> item) {
		return map.getOrDefault(item, 0);
	}

	public ItemStack addStack(ItemStack that) {
		return ItemStack.fromItemsMap(Stream.of(this, that)//
				// Retrieve each unique key
				.map(ItemStack::getItems)//
				.flatMap(Set::stream)//
				.distinct()//
				// Retrieve the value of each key into a map
				.collect(Collectors.toMap(//
						key -> key, //
						key -> this.getQuantity(key) //
								+ that.getQuantity(key)//
				)));
	}

	public ItemStack addStack(Collection<ItemEntry> stack) {
		return addStack(ItemStack.fromItemsMap(stack.stream()//
				.collect(Collectors.toMap(ItemEntry::getItem, ItemEntry::getQuantity))));
	}

	public ItemStack addMaterial(Item<?> item, int quantity) {
		return addStack(ItemStack.fromItemsMap(Map.of(item, quantity)));
	}

	public ItemStack minusStack(ItemStack stack) {
		return addStack(stack.times(-1));
	}

	public ItemStack minusStack(Collection<ItemEntry> stack) {
		return minusStack(ItemStack.fromItemsMap(stack.stream()//
				.collect(Collectors.toMap(ItemEntry::getItem, ItemEntry::getQuantity))));
	}

	public ItemStack minusMaterial(Item<?> item, int quantity) {
		return minusStack(ItemStack.fromItemsMap(Map.of(item, quantity)));
	}

	public ItemStack times(int multiplier) {
		return ItemStack.fromItemsMap(map.entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey(), entry.getValue() * multiplier))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public ItemStack filter(Filter filter) {
		return ItemStack.fromItemsMap(map.entrySet().stream()//
				.filter(entry -> filter.test(entry.getKey(), entry.getValue()))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public boolean contains(ItemStack stack) {
		// TODO What about negative quantities?
		return map.entrySet().stream()//
				.allMatch(entry -> {
					Item<?> item = entry.getKey();
					int quantity = entry.getValue();
					int currentQuantity = getQuantity(item);
					return currentQuantity >= quantity;
				});
	}

	public boolean contains(Item<?> item) {
		// TODO What about negative quantities?
		return getQuantity(item) != 0;
	}
	
	@Override
	public Iterator<ItemEntry> iterator() {
		return map.entrySet().stream().map(entry -> {
			Item<?> item = entry.getKey();
			if (item instanceof StackableItem<?>) {
				return ItemEntry.of((StackableItem<?>)item, entry.getValue());
			} else {
				return ItemEntry.of(item);
			}
		}).iterator();
	}

	public static ItemStack empty() {
		return ItemStack.fromItemsMap(Collections.emptyMap());
	}

	public static interface Filter {
		boolean test(Item<?> item, int quantity);

		default Filter and(Filter otherFilter) {
			return (item, quantity) -> this.test(item, quantity) && otherFilter.test(item, quantity);
		}

		default Filter or(Filter otherFilter) {
			return (item, quantity) -> this.test(item, quantity) || otherFilter.test(item, quantity);
		}

		public static Filter nonZero() {
			return (item, quantity) -> quantity != 0;
		}

		public static Filter strictlyPositive() {
			return (item, quantity) -> quantity > 0;
		}

		public static Filter strictlyNegative() {
			return (item, quantity) -> quantity < 0;
		}

		public static Filter noMora() {
			return (item, quantity) -> !item.getType().equals(MORA);
		}

		public static Filter items(Collection<Item<?>> items) {
			return (item, quantity) -> items.contains(item);
		}

		public static Filter items(Item<?>... items) {
			return items(Set.of(items));
		}

		public static Filter itemsIn(ItemStack stack) {
			return items(stack.getItems());
		}

	}

	public Stream<ItemEntry> stream() {
		return map.entrySet().stream().map(entry -> {
			return new ItemEntry(entry.getKey(), entry.getValue());
		});
	}

}
