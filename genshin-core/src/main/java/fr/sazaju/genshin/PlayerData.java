package fr.sazaju.genshin;

import static fr.sazaju.genshin.InventoryTab.Definition.*;
import static fr.sazaju.genshin.item.simple.Mora.*;
import static fr.sazaju.genshin.item.simple.OriginalResin.*;
import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import fr.sazaju.genshin.InventoryTab.NotEnoughItemsException;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.StackableItem;
import fr.sazaju.genshin.item.artifact.Artifact;
import fr.sazaju.genshin.item.simple.Billet;
import fr.sazaju.genshin.item.simple.BossDrop;
import fr.sazaju.genshin.item.simple.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.simple.CommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EnhancementOre;
import fr.sazaju.genshin.item.simple.EventMaterial;
import fr.sazaju.genshin.item.simple.Food;
import fr.sazaju.genshin.item.simple.ForgingMaterial;
import fr.sazaju.genshin.item.simple.Gadget;
import fr.sazaju.genshin.item.simple.LocalSpecialty;
import fr.sazaju.genshin.item.simple.Potion;
import fr.sazaju.genshin.item.simple.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.simple.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.recipe.Recipe;

public class PlayerData implements Iterable<ItemEntry> {
	public static enum Tab {
		WEAPONS(new InventoryTab.Definition(//
				items(Weapon.class), //
				stacks(EnhancementOre.class)//
		)), //
		ARTIFACTS(new InventoryTab.Definition(//
				items(Artifact.class)//
		)), //
		ASCENSION_MATERIALS(new InventoryTab.Definition(//
				stacks(BossDrop.class), //
				stacks(CharacterAscensionMaterial.class), //
				stacks(CommonAscensionMaterial.class), //
				stacks(EliteCommonAscensionMaterial.class), //
				stacks(EventMaterial.class), //
				stacks(TalentLevelUpMaterial.class), //
				stacks(WeaponAscensionMaterial.class)//
		)), //
		FOOD(new InventoryTab.Definition(//
				stacks(Food.class), //
				stacks(Potion.class)//
		)), //
		MATERIALS(new InventoryTab.Definition(//
				stacks(Billet.class), //
				stacks(ForgingMaterial.class), //
				stacks(LocalSpecialty.class)//
		)), //
		GADGETS(new InventoryTab.Definition(//
				stacks(Gadget.class)//
		)), //
		QUEST_ITEMS(new InventoryTab.Definition(//
				stacks(null)// TODO
		)), //
		PRECIOUS_ITEMS(new InventoryTab.Definition(//
				stacks(null)// TODO
		)), //
		FURNISHINGS(new InventoryTab.Definition(//
				stacks(null)// TODO
		)), //
		;

		private final InventoryTab.Definition definition;

		private Tab(InventoryTab.Definition definition) {
			this.definition = definition;
		}
	}

	private final int moras;
	private final int resin;
	private final Map<Tab, InventoryTab> tabs;

	private PlayerData(int moras, int resin, Map<Tab, InventoryTab> tabs) {
		this.moras = moras;
		this.resin = resin;
		this.tabs = tabs;
	}

	public PlayerData add(Item<?> item) {
		int newMoras = this.moras;
		int newResin = this.resin;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras + 1;
		} else if (item.getType().equals(ORIGINAL_RESIN)) {
			newResin = newResin + 1;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().add(item));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newResin, newTabs);
	}

	public PlayerData remove(Item<?> item) {
		int newMoras = this.moras;
		int newResin = this.resin;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			if (moras < 1) {
				throw new NotEnoughItemsException(MORA.item(), moras, 1);
			} else {
				newMoras = newMoras - 1;
			}
		} else if (item.getType().equals(ORIGINAL_RESIN)) {
			if (resin < 1) {
				throw new NotEnoughItemsException(ORIGINAL_RESIN.item(), resin, 1);
			} else {
				newResin = newResin - 1;
			}
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().remove(item));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newResin, newTabs);
	}

	public PlayerData add(StackableItem<?> item, int quantity) {
		int newMoras = this.moras;
		int newResin = this.resin;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras + quantity;
		} else if (item.getType().equals(ORIGINAL_RESIN)) {
			newResin = newResin + quantity;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().add(item, quantity));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newResin, newTabs);
	}

	public PlayerData remove(StackableItem<?> item, int quantity) {
		int newMoras = this.moras;
		int newResin = this.resin;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			if (moras < quantity) {
				throw new NotEnoughItemsException(MORA.item(), moras, quantity);
			} else {
				newMoras = newMoras - quantity;
			}
		} else if (item.getType().equals(ORIGINAL_RESIN)) {
			if (resin < quantity) {
				throw new NotEnoughItemsException(ORIGINAL_RESIN.item(), resin, quantity);
			} else {
				newResin = newResin - quantity;
			}
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().remove(item, quantity));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newResin, newTabs);
	}

	public PlayerData addAll(Iterable<ItemEntry> items) {
		PlayerData data = this;
		for (ItemEntry itemEntry : items) {
			Item<?> item = itemEntry.getItem();
			if (item instanceof StackableItem<?>) {
				StackableItem<?> stackableItem = (StackableItem<?>) item;
				data = data.add(stackableItem, itemEntry.getQuantity());
			} else if (itemEntry.getQuantity() > 1) {
				throw new IllegalArgumentException("Cannot add more than one non stackable item: " + item);
			} else {
				data = data.add(item);
			}
		}
		return data;
	}

	public PlayerData removeAll(Iterable<ItemEntry> items) {
		PlayerData data = this;
		for (ItemEntry itemEntry : items) {
			Item<?> item = itemEntry.getItem();
			if (item instanceof StackableItem<?>) {
				StackableItem<?> stackableItem = (StackableItem<?>) item;
				data = data.remove(stackableItem, itemEntry.getQuantity());
			} else if (itemEntry.getQuantity() > 1) {
				throw new IllegalArgumentException("Cannot remove more than one non stackable item: " + item);
			} else {
				data = data.remove(item);
			}
		}
		return data;
	}

	public int getQuantity(Item<?> item) {
		if (item.getType().equals(MORA)) {
			return moras;
		} else if (item.getType().equals(ORIGINAL_RESIN)) {
			return resin;
		} else {
			return tabs.values().stream()//
					.filter(tab -> tab.accept(item))//
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Item not managed: " + item))//
					.stream()//
					.filter(entry -> entry.getItem().equals(item))//
					.mapToInt(entry -> entry.getQuantity()).findFirst().orElse(0);
		}
	}

	public Stream<ItemEntry> stream() {
		Collection<Stream<ItemEntry>> streams = new LinkedList<>();

		if (moras != 0) {
			streams.add(Stream.of(ItemEntry.of(MORA.item(), moras)));
		}
		if (resin != 0) {
			streams.add(Stream.of(ItemEntry.of(ORIGINAL_RESIN.item(), resin)));
		}
		streams.add(tabs.values().stream().flatMap(InventoryTab::stream));

		return streams.stream().flatMap(stream -> stream);
	}

	public PlayerData update(Recipe recipe) {
		List<ItemEntry> costs = recipe.getCost().stream().collect(toList());
		List<ItemEntry> products = recipe.getProducts().stream().collect(toList());
		return this.addAll(products).removeAll(costs);
	}

	@Override
	public Iterator<ItemEntry> iterator() {
		return stream().iterator();
	}

	public static PlayerData empty() {
		int moras = 0;
		int resin = 0;
		Map<Tab, InventoryTab> tabs = Stream.of(Tab.values())//
				.collect(toMap(//
						tab -> tab, //
						tab -> new InventoryTab(tab.definition)//
				));
		return new PlayerData(moras, resin, tabs);
	}

	public static PlayerData fromItemEntries(Iterable<ItemEntry> entries) {
		return PlayerData.empty().addAll(entries);
	}
}
