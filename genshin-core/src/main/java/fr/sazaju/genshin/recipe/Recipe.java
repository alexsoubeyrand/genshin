package fr.sazaju.genshin.recipe;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.ItemStack.Filter.*;
import static fr.sazaju.genshin.item.simple.Billet.*;
import static fr.sazaju.genshin.item.simple.BossDrop.*;
import static fr.sazaju.genshin.item.simple.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.simple.EnhancementOre.*;
import static fr.sazaju.genshin.item.simple.ForgingMaterial.*;
import static fr.sazaju.genshin.item.simple.Gadget.*;
import static fr.sazaju.genshin.item.simple.LocalSpecialty.*;
import static fr.sazaju.genshin.item.simple.Mora.*;
import static fr.sazaju.genshin.item.simple.OriginalResin.*;
import static fr.sazaju.genshin.item.simple.Potion.*;
import static fr.sazaju.genshin.item.weapon.WeaponType.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemEntry;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.simple.Billet;
import fr.sazaju.genshin.item.simple.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.simple.CommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EnhancementOre;
import fr.sazaju.genshin.item.simple.ForgingMaterial;
import fr.sazaju.genshin.item.simple.Potion;
import fr.sazaju.genshin.item.simple.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.simple.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.weapon.WeaponType;

public class Recipe {

	private final ItemStack diff;
	private final Map<Item<?>, Integer> map;

	@Deprecated
	private Recipe(ItemStack diff) {
		this.diff = diff;
		this.map = diff.getMap();
	}

	private Recipe(Map<Item<?>, Integer> map) {
		this.diff = ItemStack.fromItemsMap(map);
		this.map = map;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public ItemStack getDiff() {
		return diff;
	}

	public Stream<ItemEntry> streamProducts() {
		return diff.filter(strictlyPositive()).stream();
	}

	public int getProducedQuantity(Item<?> item) {
		return map.getOrDefault(item, 0);
	}

	public Stream<ItemEntry> streamCosts() {
		return diff.filter(strictlyNegative()).times(-1).stream();
	}

	public int getConsumedQuantity(Item<?> item) {
		return -map.getOrDefault(item, 0);
	}

	public Recipe add(Recipe recipe) {
		Map<Item<?>, Integer> newMap = new HashMap<Item<?>, Integer>(map);
		recipe.map.entrySet().forEach(entry -> {
			newMap.merge(entry.getKey(), entry.getValue(), (v1, v2) -> {
				int sum = v1 + v2;
				return sum == 0 ? null : sum;
			});
		});
		return Recipe.fromDiff(newMap);
	}

	public Recipe times(int multiplier) {
		return Recipe.fromDiff(diff.times(multiplier));
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
			return Objects.equals(this.diff, that.diff);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(diff);
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

	@Deprecated
	public static Recipe fromDiff(ItemStack diff) {
		return new Recipe(diff);
	}

	public static Recipe fromDiff(Map<Item<?>, Integer> map) {
		Map<Item<?>, Integer> cleanedMap = new HashMap<>();
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

	@Deprecated
	public static Recipe forItem(Item<?> itemProduced, ItemStack cost) {
		return new Recipe(cost.times(-1).addMaterial(itemProduced, 1));
	}

	public static Recipe forItem(Item<?> itemProduced, Map<Item<?>, Integer> cost) {
		return Recipe.fromDiff(cost).reverse().produces(itemProduced, 1);
	}

	@Deprecated
	public static Recipe forItems(int quantityProduced, Item<?> itemProduced, ItemStack cost) {
		return new Recipe(cost.times(-1).addMaterial(itemProduced, quantityProduced));
	}

	public static Recipe forItems(int quantityProduced, Item<?> itemProduced, Map<Item<?>, Integer> cost) {
		return Recipe.fromDiff(cost).reverse().produces(itemProduced, quantityProduced);
	}

	@Deprecated
	public static Recipe forItems(ItemStack products, ItemStack cost) {
		return new Recipe(cost.times(-1).addStack(products));
	}

	public static Recipe forItems(Map<Item<?>, Integer> products, Map<Item<?>, Integer> cost) {
		return Recipe.fromDiff(products).add(Recipe.fromDiff(cost).reverse());
	}

	public static Stream<Recipe> streamMihoyoRecipes() {
		return Stream.of(//
				recipesOn3SubItems(), //
				enhancementOreRecipes(), //
				potionRecipes(), //
				weaponRecipes(), //
				gadgetRecipes()//
		).flatMap(stream -> stream);
	}

	private static Stream<Recipe> weaponRecipes() {
		return Stream.of(//
				Recipe.forItem(ADEPTI_SEEKER_S_STOVE.item(), ItemStack.fromItemsMap(Map.of(//
						FLAMING_ESSENTIAL_OIL.item(), 1, //
						CRYSTAL_CORE.item(), 2, //
						IRON_CHUNK.item(), 2, //
						MORA.item(), 500//
				))), //
				Recipe.forItem(ANEMOCULUS_RESONANCE_STONE.item(), ItemStack.fromItemsMap(Map.of(//
						DANDELION_SEED.item(), 5, //
						CECILIA.item(), 5, //
						CRYSTAL_CHUNK.item(), 1, //
						MORA.item(), 500//
				))), //
				Recipe.forItem(GEOCULUS_RESONANCE_STONE.item(), ItemStack.fromItemsMap(Map.of(//
						LAPIS.item(), 5, //
						GLAZE_LILY.item(), 5, //
						CRYSTAL_CHUNK.item(), 1, //
						MORA.item(), 500//
				))), //
				Recipe.forItem(WARMING_BOTTLE.item(), ItemStack.fromItemsMap(Map.of(//
						FLAMING_FLOWER_STAMEN.item(), 2, //
						STARSILVER.item(), 2, //
						MORA.item(), 500//
				))), //
				Recipe.forItem(ANEMO_TREASURE_COMPASS.item(), ItemStack.fromItemsMap(Map.of(//
						INSIGNIA.item(THREE_STARS), 10, //
						PHILANEMO_MUSHROOM.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.forItem(GEO_TREASURE_COMPASS.item(), ItemStack.fromItemsMap(Map.of(//
						INSIGNIA.item(THREE_STARS), 10, //
						LAPIS.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.forItem(NRE_MENU_30.item(), ItemStack.fromItemsMap(Map.of(//
						CHAOS.item(THREE_STARS), 20, //
						LIZARD_TAIL.item(), 20, //
						ELECTRO_CRYSTAL.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.forItem(PORTABLE_WAYPOINT.item(), ItemStack.fromItemsMap(Map.of(//
						LEY_LINE.item(THREE_STARS), 1, //
						LUMINESCENT_SPINE.item(), 2, //
						CRYSTAL_CHUNK.item(), 5, //
						MORA.item(), 500//
				))), //
				Recipe.forItem(WIND_CATCHER.item(), ItemStack.fromItemsMap(Map.of(//
						HURRICANE_SEED.item(), 10, //
						WINDWHEEL_ASTER.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				)))//
		);
	}

	private static Stream<Recipe> gadgetRecipes() {
		BiFunction<WeaponType, ForgingMaterial, Recipe> recipeFactory = (weaponType, specificMineral) -> {
			return Recipe.forItem(weaponType.item(), ItemStack.fromTypesMap(Map.of(//
					Billet.selectFor(weaponType.category), 1, //
					CRYSTAL_CHUNK, 50, //
					specificMineral, 50, //
					MORA, 500//
			)));
		};
		Function<WeaponType, Recipe> basicRecipeFactory = weaponType -> {
			return recipeFactory.apply(weaponType, WHITE_IRON_CHUNK);
		};
		Function<WeaponType, Recipe> dragonspineRecipeFactory = weaponType -> {
			return recipeFactory.apply(weaponType, STARSILVER);
		};
		return Stream.of(//
				// Prototypes
				basicRecipeFactory.apply(PROTOTYPE_CRESCENT), //
				basicRecipeFactory.apply(PROTOTYPE_AMBER), //
				basicRecipeFactory.apply(PROTOTYPE_ARCHAIC), //
				basicRecipeFactory.apply(PROTOTYPE_STARGLITTER), //
				basicRecipeFactory.apply(PROTOTYPE_RANCOUR), //
				// Named
				basicRecipeFactory.apply(COMPOUND_BOW), //
				basicRecipeFactory.apply(MAPPA_MARE), //
				basicRecipeFactory.apply(WHITEBLIND), //
				basicRecipeFactory.apply(CRESCENT_PIKE), //
				basicRecipeFactory.apply(IRON_STING), //
				// Dragonspine
				dragonspineRecipeFactory.apply(FROSTBEARER), //
				dragonspineRecipeFactory.apply(SNOW_TOMBED_STARSILVER), //
				Recipe.forItem(DRAGONSPINE_SPEAR.item(), ItemStack.fromTypesMap(Map.of(//
						NORTHLANDER_POLEARM_BILLET, 1, //
						VITALIZED_DRAGONTOOTH, 8, // Particular item & quantity here
						STARSILVER, 50, //
						MORA, 500//
				)))//
		);
	}

	private static Stream<Recipe> potionRecipes() {
		BiFunction<Potion, Stream<ItemType.WithSingleRarity>, Recipe> recipeFactory = (potion, specificItemTypes) -> {
			Map<Item<?>, Integer> cost = Stream.concat(//
					specificItemTypes.map(type -> Map.entry(type.item(), 1)), // Add one of each
					Stream.of(Map.entry(MORA.item(), 100))// Add 100 Moras
			).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			return Recipe.forItem(potion.item(), ItemStack.fromItemsMap(cost));
		};
		return Stream.of(//
				// Oils
				recipeFactory.apply(FLAMING_ESSENTIAL_OIL, Stream.of(FROG, FLAMING_FLOWER_STAMEN)), //
				recipeFactory.apply(FROSTING_ESSENTIAL_OIL, Stream.of(LIZARD_TAIL, MIST_FLOWER_COROLLA)), //
				recipeFactory.apply(GUSHING_ESSENTIAL_OIL, Stream.of(LIZARD_TAIL, DANDELION_SEED)), //
				recipeFactory.apply(SHOCKING_ESSENTIAL_OIL, Stream.of(FROG, ELECTRO_CRYSTAL)), //
				recipeFactory.apply(STREAMING_ESSENTIAL_OIL, Stream.of(FROG, LOTUS_HEAD)), //
				recipeFactory.apply(UNMOVING_ESSENTIAL_OIL, Stream.of(FROG, LAPIS)), //
				// Potions
				recipeFactory.apply(DESICCANT_POTION, Stream.of(BUTTERFLY_WINGS, LOTUS_HEAD)), //
				recipeFactory.apply(DUSTPROOF_POTION, Stream.of(BUTTERFLY_WINGS, LAPIS)), //
				recipeFactory.apply(FROSTSHIELD_POTION, Stream.of(CRYSTAL_CORE, MIST_FLOWER_COROLLA)), //
				recipeFactory.apply(HEATSHIELD_POTION, Stream.of(BUTTERFLY_WINGS, FLAMING_FLOWER_STAMEN)), //
				recipeFactory.apply(INSULATION_POTION, Stream.of(BUTTERFLY_WINGS, ELECTRO_CRYSTAL)), //
				recipeFactory.apply(WINDBARRIER_POTION, Stream.of(CRYSTAL_CORE, DANDELION_SEED)) //
		);
	}

	private static Stream<Recipe> recipesOn3SubItems() {
		BiFunction<Class<? extends ItemType.WithMultipleRarities>, Map<Rarity, Integer>, Stream<Recipe>> //
		recipesFactory = (clazz, morasCosts) -> {
			return Stream.of(clazz.getEnumConstants())//
					.flatMap(itemType -> {
						return itemType.getRarities().stream()//
								.sorted().skip(1)// Ignore lowest rarity (no sub to craft it)
								.map(rarity -> itemType.item(rarity));// Get all possible items
					})//
					.map(item -> {
						Item<?> subItem = item.getType().item(item.getRarity().below());
						int moras = morasCosts.get(item.getRarity());
						return Recipe.forItem(item, ItemStack.fromItemsMap(Map.of(subItem, 3, MORA.item(), moras)));
					});
		};
		return Stream.of(//
				recipesFactory.apply(//
						CharacterAscensionMaterial.class, //
						Map.of(THREE_STARS, 300, FOUR_STARS, 900, FIVE_STARS, 2700)//
				), //
				recipesFactory.apply(//
						CommonAscensionMaterial.class, //
						Map.of(TWO_STARS, 25, THREE_STARS, 50)//
				), //
				recipesFactory.apply(//
						EliteCommonAscensionMaterial.class, //
						Map.of(THREE_STARS, 50, FOUR_STARS, 125)//
				), //
				recipesFactory.apply(//
						TalentLevelUpMaterial.class, //
						Map.of(THREE_STARS, 175, FOUR_STARS, 550)//
				), //
				recipesFactory.apply(//
						WeaponAscensionMaterial.class, //
						Map.of(THREE_STARS, 125, FOUR_STARS, 350, FIVE_STARS, 1075)//
				)//
		).flatMap(stream -> stream);
	}

	private static Stream<Recipe> enhancementOreRecipes() {
		BiFunction<EnhancementOre, Map<ItemType.WithSingleRarity, Integer>, Recipe> recipeFactory = (ore, costs) -> {
			return Recipe.forItem(ore.item(), ItemStack.fromTypesMap(costs));
		};
		return Stream.of(//
				recipeFactory.apply(ENHANCEMENT_ORE, Map.of(IRON_CHUNK, 2, MORA, 5)), //
				recipeFactory.apply(FINE_ENHANCEMENT_ORE, Map.of(WHITE_IRON_CHUNK, 3, MORA, 10)), //
				recipeFactory.apply(FINE_ENHANCEMENT_ORE, Map.of(STARSILVER, 3, MORA, 10)), //
				recipeFactory.apply(MYSTIC_ENHANCEMENT_ORE, Map.of(CRYSTAL_CHUNK, 4, MORA, 50)), //
				recipeFactory.apply(MYSTIC_ENHANCEMENT_ORE,
						Map.of(MAGICAL_CRYSTAL_CHUNK, 3, ORIGINAL_RESIN, 10, MORA, 100)) //
		);
	}

	public static Stream<Recipe> streamRecipesProducing(Item<?> item) {
		return streamMihoyoRecipes().filter(recipe -> recipe.diff.getQuantity(item) > 0);
	}

	public static Recipe empty() {
		return new Recipe(Map.of());
	}

	public Recipe consumes(Item<?> item, int quantity) {
		return add(Recipe.fromDiff(Map.of(item, -quantity)));
	}

	public Recipe produces(Item<?> item, int quantity) {
		return add(Recipe.fromDiff(Map.of(item, quantity)));
	}

}
