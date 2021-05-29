package fr.sazaju.genshin;

import static java.util.Collections.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

public class InventoryTab implements Iterable<ItemEntry> {

	private final InventoryTab.Definition definition;
	private final Set<Item<?>> nonStackableItems;
	private final Map<StackableItem<?>, Integer> stackableItems;

	public InventoryTab(InventoryTab.Definition definition) {
		this(definition, emptySet(), emptyMap());
	}

	private InventoryTab(//
			InventoryTab.Definition definition, //
			Set<Item<?>> nonStackableItems, //
			Map<StackableItem<?>, Integer> stackableItems) {
		this.definition = definition;
		this.nonStackableItems = unmodifiableSet(nonStackableItems);
		this.stackableItems = unmodifiableMap(stackableItems);
	}

	public Stream<ItemEntry> stream() {
		return Stream.concat(//
				nonStackableItems.stream().map(item -> ItemEntry.of(item)), //
				stackableItems.entrySet().stream().map(entry -> ItemEntry.of(entry.getKey(), entry.getValue()))//
		);
	}

	@Override
	public Iterator<ItemEntry> iterator() {
		return stream().iterator();
	}

	public boolean accept(Item<?> item) {
		return definition.accept(item);
	}

	public InventoryTab add(Item<?> item) {
		if (!accept(item)) {
			throw new IllegalArgumentException("This tab does not accept " + item);
		}
		if (item instanceof StackableItem<?>) {
			StackableItem<?> stackableItem = (StackableItem<?>) item;
			return add(stackableItem, 1);
		} else if (this.nonStackableItems.contains(item)) {
			throw new IllegalArgumentException("This item is already present: " + item);
		} else {
			Set<Item<?>> newNonStackableItems = new HashSet<>(this.nonStackableItems);
			newNonStackableItems.add(item);
			return new InventoryTab(//
					this.definition, //
					unmodifiableSet(newNonStackableItems), //
					this.stackableItems//
			);
		}
	}

	public InventoryTab remove(Item<?> item) {
		if (!accept(item)) {
			throw new IllegalArgumentException("This tab does not accept " + item);
		}
		if (item instanceof StackableItem<?>) {
			StackableItem<?> stackableItem = (StackableItem<?>) item;
			return remove(stackableItem, 1);
		} else {
			Set<Item<?>> newNonStackableItems = new HashSet<>(this.nonStackableItems);
			boolean wasPresent = newNonStackableItems.remove(item);
			if (!wasPresent) {
				throw new NotEnoughItemsException(item, 0, 1);
			}
			return new InventoryTab(//
					this.definition, //
					unmodifiableSet(newNonStackableItems), //
					this.stackableItems//
			);
		}
	}

	public InventoryTab add(StackableItem<?> item, int quantity) {
		if (!accept(item)) {
			throw new IllegalArgumentException("This tab does not accept " + item);
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot add negative quantity of " + item);
		}
		Map<StackableItem<?>, Integer> newStackableItems = new HashMap<>(this.stackableItems);
		newStackableItems.compute(item, (k, current) -> current == null ? quantity : current + quantity);
		return new InventoryTab(//
				this.definition, //
				this.nonStackableItems, //
				unmodifiableMap(newStackableItems)//
		);
	}

	public InventoryTab remove(StackableItem<?> item, int quantity) {
		if (!accept(item)) {
			throw new IllegalArgumentException("This tab does not accept " + item);
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot remove negative quantity of " + item);
		}
		Map<StackableItem<?>, Integer> newStackableItems = new HashMap<>(this.stackableItems);
		newStackableItems.compute(item, (k, current) -> {
			if (current == null) {
				current = 0;
			}
			if (current < quantity) {
				throw new NotEnoughItemsException(item, current, quantity);
			} else if (current == quantity) {
				return null;
			} else {
				return current - quantity;
			}
		});
		return new InventoryTab(//
				this.definition, //
				this.nonStackableItems, //
				unmodifiableMap(newStackableItems)//
		);
	}

	@Override
	public String toString() {
		return nonStackableItems.toString() + stackableItems.toString();
	}

	public static class Definition {

		private final Set<Class<? extends Item<?>>> nonStackableItemClasses;
		private final Set<Class<? extends ItemType>> stackableTypeClasses;

		public Definition(DefinitionPart... parts) {
			Set<Class<? extends Item<?>>> nonStackableItemClasses = new HashSet<>();
			Set<Class<? extends ItemType>> stackableTypeClasses = new HashSet<>();
			for (DefinitionPart part : parts) {
				part.feed(nonStackableItemClasses, stackableTypeClasses);
			}
			this.nonStackableItemClasses = unmodifiableSet(nonStackableItemClasses);
			this.stackableTypeClasses = unmodifiableSet(stackableTypeClasses);
		}

		public boolean accept(Item<?> item) {
			if (item instanceof StackableItem<?>) {
				return stackableTypeClasses.contains(item.getType().getClass());
			} else {
				return nonStackableItemClasses.contains(item.getClass());
			}
		}

		public static interface DefinitionPart {

			void feed(Set<Class<? extends Item<?>>> nonStackableItemClasses,
					Set<Class<? extends ItemType>> stackableTypeClasses);

		}

		// TODO Item or ItemType?
		public static DefinitionPart items(Class<? extends Item<?>> itemClass) {
			return (nonStackableItemClasses, stackableTypeClasses) -> nonStackableItemClasses.add(itemClass);
		}

		// TODO Item or ItemType?
		// TODO Restrict to stackable class through generics
		public static DefinitionPart stacks(Class<? extends ItemType> stackableTypeClass) {
			return (nonStackableItemClasses, stackableTypeClasses) -> stackableTypeClasses.add(stackableTypeClass);
		}

	}

	@SuppressWarnings("serial")
	public static class NotEnoughItemsException extends IllegalArgumentException {
		public NotEnoughItemsException(Item<?> item, int currentQuantity, int quantityToRemove) {
			super("Cannot remove " + quantityToRemove + " of " + item + " if there is only " + currentQuantity);
		}
	}
}
