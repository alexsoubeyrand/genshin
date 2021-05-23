package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.Billet.*;
import static fr.sazaju.genshin.item.BossDrop.*;
import static fr.sazaju.genshin.item.CharacterAscensionMaterial.*;
import static fr.sazaju.genshin.item.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.ForgingMaterial.*;
import static fr.sazaju.genshin.item.ItemStack.Filter.*;
import static fr.sazaju.genshin.item.LocalSpecialty.*;
import static fr.sazaju.genshin.item.Mora.*;
import static fr.sazaju.genshin.item.PlayerDataHistory.HistorySelector.*;
import static fr.sazaju.genshin.item.TalentLevelUpMaterial.*;
import static fr.sazaju.genshin.item.WeaponAscensionMaterial.*;
import static fr.sazaju.genshin.leveling.Levels.Filter.*;
import static java.util.Collections.*;
import static java.util.Comparator.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerData;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.PlayerDataHistory;
import fr.sazaju.genshin.item.StackableItem;
import fr.sazaju.genshin.item.weapon.Weapon;
import fr.sazaju.genshin.item.weapon.WeaponType;
import fr.sazaju.genshin.leveling.Level;
import fr.sazaju.genshin.leveling.Levels;
import fr.sazaju.genshin.recipe.Recipe;

public class Main {

	public static class InventoryTab implements Iterable<ItemEntry> {

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
			this.stackableItems =unmodifiableMap(stackableItems);
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
				return add(stackableItem, 1);
			} else {
				Set<Item<?>> newNonStackableItems = new HashSet<>(this.nonStackableItems);
				boolean wasPresent = newNonStackableItems.remove(item);
				if (!wasPresent) {
					throw new IllegalArgumentException("Cannot remove absent item: " + item);
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
					return quantity;
				} else {
					if (current < quantity) {
						throw new IllegalArgumentException(
								"Cannot remove " + quantity + " of " + item + " if there is only " + current);
					} else if (current == quantity) {
						return null;
					} else {
						return current - quantity;
					}
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
	}

	public static void main(String[] args) {
		PlayerData data = PlayerData.empty();

		Weapon weapon = WeaponType.LION_S_ROAR.buildInstance()//
				.withWeaponLevel(1)//
				.create();
		data.add(weapon);
		Character character = CharacterProfile.KEQING.buildInstance(weapon)//
				.withCharacterLevel(1)//
				.create();
		// TODO Add to data

		int adventureRank = 40;
		Levels characterAscensionLevels = character.profile.ascensionLevels//
				.filter(remainingForCharacterAscension(character).and(allowedForAdventureRank(adventureRank)));
		ItemStack characterAscensionsCost = characterAscensionLevels.getCost();

		int characterTalentLevelLimit = 6;
		ItemStack characterTalentsCost = Stream.of(character.normalAttackLevel)//
				.map(talentLevel -> {
					return character.profile.talentLevels//
							.filter(remainingForTalent(talentLevel).and(atMostLevel(characterTalentLevelLimit))
									.and(allowedForAdventureRank(adventureRank)))
							.getCost();
				})//
				.reduce(ItemStack::addStack).get();

		int weaponLevelLimit = 4;
		Levels weaponAscensionLevels = weapon.type.ascensionLevels//
				.filter(remainingForWeaponAscension(weapon).and(atMostLevel(weaponLevelLimit))
						.and(allowedForAdventureRank(adventureRank)));
		ItemStack weaponAscensionCost = weaponAscensionLevels.getCost();

		Recipe totalCost = Recipe
				.fromDiff(characterAscensionsCost.addStack(characterTalentsCost).addStack(weaponAscensionCost))
				.reverse();
		data = data.addAll(List.of(//
				ItemEntry.of(weapon), //
				ItemEntry.of(MORA.item(), Integer.MAX_VALUE), //
				ItemEntry.of(LAPIS.item(), 200), //
				ItemEntry.of(PRISM.item(), 26), //
				ItemEntry.of(VAJRADA.item(TWO_STARS), 26), //
				ItemEntry.of(VAJRADA.item(THREE_STARS), 29), //
				ItemEntry.of(NECTAR.item(ONE_STAR), 196), //
				ItemEntry.of(NECTAR.item(TWO_STARS), 46), //
				ItemEntry.of(NECTAR.item(THREE_STARS), 12), //
				ItemEntry.of(GUYUN_PILAR.item(TWO_STARS), 14), //
				ItemEntry.of(GUYUN_PILAR.item(THREE_STARS), 15), //
				ItemEntry.of(GUYUN_PILAR.item(FOUR_STARS), 2), //
				ItemEntry.of(PROSPERITY.item(TWO_STARS), 41), //
				ItemEntry.of(PROSPERITY.item(THREE_STARS), 53), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(TWO_STARS), 78), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(THREE_STARS), 15), //
				ItemEntry.of(SACRIFICIAL_KNIFE.item(FOUR_STARS), 4), //
				ItemEntry.of(INSIGNIA.item(ONE_STAR), 357), //
				ItemEntry.of(INSIGNIA.item(TWO_STARS), 79), //
				ItemEntry.of(INSIGNIA.item(THREE_STARS), 12), //
				ItemEntry.of(WHITE_IRON_CHUNK.item(), 50), //
				ItemEntry.of(NORTHLANDER_CATALYST_BILLET.item(), 1), //
				ItemEntry.of(CRYSTAL_CHUNK.item(), 50)//
		));
		PlayerData target = PlayerData.fromItemEntries(totalCost.getCost());
		PlayerDataHistory conversionHistory = PlayerDataHistory.search(data, target, ONLY_IF_SUCCESSFUL);
		PlayerData dataAfterConversion = conversionHistory.getResultingData();

		///////////////////////////////////////
		System.out.println("[Weapon]");
		displayLevels("Ascension", weaponAscensionLevels);

		System.out.println("[Character]");
		System.out.println(character);
		displayLevels("Ascension", characterAscensionLevels);
		System.out.println("[Talents total]");
		displayStack(characterTalentsCost);
		System.out.println("[Total cost]");
		displayStack(totalCost.getCost());

		System.out.println("[Available]");
		displayPlayerData(data);

		System.out.println("[Recipes]");
		Recipe.streamRecipes().forEach(recipe -> {
			displayDiff(recipe.getDiff());
		});

		System.out.println("[Required Conversions]");
		conversionHistory.streamRecipes().forEach(Main::displayRecipe);

		System.out.println("[Available After Conversion]");
		displayPlayerData(dataAfterConversion);

		System.out.println("[Consumed]");
		displayStack(totalCost.getCost());

		System.out.println("[Remaining]");
		displayPlayerData(dataAfterConversion.apply(totalCost));

		// TODO Expose on service
		// TODO Test on service
		// TODO Expose on website
		// TODO Resolve TODOs
	}

	private static void displayDiff(ItemStack diff) {
		ItemStack consumed = diff.filter(strictlyNegative()).times(-1);
		ItemStack produced = diff.filter(strictlyPositive());
		Function<ItemStack, String> formater = stack -> {
			return stack.stream().map(entry -> {
				Item<?> material = entry.getItem();
				int quantity = entry.getQuantity();
				return (quantity == 1 ? "" : quantity + " x ") + material;
			}).collect(Collectors.joining(" + "));
		};
		System.out.println("  " + formater.apply(consumed) + " => " + formater.apply(produced));
	}

	private static void displayRecipe(Recipe recipe) {
		System.out.println("  " + recipe);
	}

	private static void displayLevels(String levelsType, Levels levels) {
		for (Entry<Integer, Level> levelEntry : sortedByKeys(levels.toMap().entrySet())) {
			System.out.println("[" + levelsType + " level " + levelEntry.getKey() + "]");
			displayStack(levelEntry.getValue().getCost());
		}
		String range;
		if (levels.toMap().isEmpty()) {
			range = "none";
		} else {
			int minIndex = levels.toMap().keySet().stream().mapToInt(i -> i).min().orElseThrow();
			int maxIndex = levels.toMap().keySet().stream().mapToInt(i -> i).max().orElseThrow();
			range = minIndex + "-" + maxIndex;
		}
		System.out.println("[" + levelsType + " total (" + range + ")]");
		displayStack(levels.toMap().values().stream().map(Level::getCost).reduce(ItemStack::addStack)
				.orElse(ItemStack.empty()));
	}

	private static <K extends Comparable<K>, T> List<Entry<K, T>> sortedByKeys(Set<Entry<K, T>> entrySet) {
		return entrySet.stream().sorted(Comparator.comparing(Entry::getKey)).collect(Collectors.toList());
	}

	private static void displayStack(ItemStack stack) {
		stack.stream()//
				.sorted(Comparator.comparing(entry -> entry.getItem(), Item.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}

	private static void displayPlayerData(PlayerData data) {
		data.stream()//
				.sorted(comparing(entry -> entry.getItem(), Item.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println("  " + entry.getItem() + " x" + entry.getQuantity());
				});
	}
}
