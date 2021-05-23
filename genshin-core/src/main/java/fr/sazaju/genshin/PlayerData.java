package fr.sazaju.genshin;

import static fr.sazaju.genshin.character.Main.InventoryTab.Definition.*;
import static fr.sazaju.genshin.item.Mora.*;
import static java.util.stream.Collectors.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import fr.sazaju.genshin.character.Main.InventoryTab;
import fr.sazaju.genshin.item.Billet;
import fr.sazaju.genshin.item.BossDrop;
import fr.sazaju.genshin.item.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.CommonAscensionMaterial;
import fr.sazaju.genshin.item.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.EnhancementOre;
import fr.sazaju.genshin.item.EventMaterial;
import fr.sazaju.genshin.item.Food;
import fr.sazaju.genshin.item.ForgingMaterial;
import fr.sazaju.genshin.item.Gadget;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.LocalSpecialty;
import fr.sazaju.genshin.item.Potion;
import fr.sazaju.genshin.item.StackableItem;
import fr.sazaju.genshin.item.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.artifact.Artifact;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.recipe.Recipe;

public class PlayerData implements Iterable<Item<?>> {
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

	public final int moras;
	private final Map<Tab, InventoryTab> tabs;

	private PlayerData(int moras, Map<Tab, InventoryTab> tabs) {
		this.moras = moras;
		this.tabs = tabs;
	}

	public PlayerData add(Item<?> item) {
		int newMoras = this.moras;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras + 1;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().add(item));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newTabs);
	}

	private PlayerData remove(Item<?> item) {
		int newMoras = this.moras;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras - 1;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().remove(item));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newTabs);
	}

	public PlayerData add(StackableItem<?> item, int quantity) {
		int newMoras = this.moras;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras + quantity;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().add(item, quantity));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newTabs);
	}

	private PlayerData remove(StackableItem<?> item, int quantity) {
		int newMoras = this.moras;
		Map<Tab, InventoryTab> newTabs = new HashMap<>(tabs);
		Optional<Entry<Tab, InventoryTab>> tab = tabs.entrySet().stream().filter(entry -> entry.getValue().accept(item))
				.findFirst();
		if (item.getType().equals(MORA)) {
			newMoras = newMoras - quantity;
		} else if (tab.isPresent()) {
			Entry<Tab, InventoryTab> entry = tab.get();
			newTabs.put(entry.getKey(), entry.getValue().remove(item, quantity));
		} else {
			throw new IllegalArgumentException("Unmanaged item: " + item);
		}
		return new PlayerData(newMoras, newTabs);
	}

	public PlayerData addAll(Map<StackableItem<?>, Integer> stackableItems) {
		PlayerData data = this;
		for (Entry<StackableItem<?>, Integer> entry : stackableItems.entrySet()) {
			data = data.add(entry.getKey(), entry.getValue());
		}
		return data;
	}

	public PlayerData addAll(List<ItemEntry> items) {
		PlayerData data = this;
		for (ItemEntry itemEntry : items) {
			Item<?> item = itemEntry.getItem();
			if (item instanceof StackableItem<?>) {
				StackableItem<?> stackableItem = (StackableItem<?>) item;
				data = data.add(stackableItem, itemEntry.getQuantity());
			} else {
				data = data.add(item);
			}
		}
		return data;
	}

	public PlayerData removeAll(List<ItemEntry> items) {
		PlayerData data = this;
		for (ItemEntry itemEntry : items) {
			Item<?> item = itemEntry.getItem();
			if (item instanceof StackableItem<?>) {
				StackableItem<?> stackableItem = (StackableItem<?>) item;
				data = data.remove(stackableItem, itemEntry.getQuantity());
			} else {
				data = data.remove(item);
			}
		}
		return data;
	}

	public int getQuantity(Item<?> item) {
		if (item.getType().equals(MORA)) {
			return moras;
		} else {
			return tabs.values().stream()//
					.filter(tab -> tab.accept(item))//
					.findFirst().orElseThrow(() -> new IllegalArgumentException("No tab accepts " + item))//
					.stream()//
					.filter(entry -> entry.getItem().equals(item))//
					.mapToInt(entry -> entry.getQuantity()).findFirst().orElse(0);
		}
	}

	public Stream<ItemEntry> stream() {
		return Stream.concat(//
				Stream.of(ItemEntry.of(MORA.item(), moras)), //
				tabs.values().stream().flatMap(InventoryTab::stream)//
		);
	}

	public PlayerData apply(Recipe recipe) {
		List<ItemEntry> costs = recipe.getCost().stream().collect(toList());
		List<ItemEntry> products = recipe.getProducts().stream().collect(toList());
		return this.addAll(products).removeAll(costs);
	}

	@Override
	public Iterator<Item<?>> iterator() {
		return stream().<Item<?>>map(ItemEntry::getItem).iterator();
	}

	public static PlayerData empty() {
		int moras = 0;
		Map<Tab, InventoryTab> tabs = Stream.of(Tab.values())//
				.collect(toMap(//
						tab -> tab, //
						tab -> new InventoryTab(tab.definition)//
				));
		return new PlayerData(moras, tabs);
	}

	public static PlayerData fromItemEntries(Iterable<ItemEntry> entries) {
		PlayerData data = PlayerData.empty();
		for (ItemEntry entry : entries) {
			Item<?> item = entry.getItem();
			if (item instanceof StackableItem<?>) {
				data = data.add((StackableItem<?>) item, entry.getQuantity());
			} else {
				data = data.add(item);
			}
		}
		return data;
	}
}
