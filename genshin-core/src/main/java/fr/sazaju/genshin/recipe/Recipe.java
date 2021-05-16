package fr.sazaju.genshin.recipe;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.item.Billet.*;
import static fr.sazaju.genshin.item.CharacterAscensionMaterialSingle.*;
import static fr.sazaju.genshin.item.CommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.EliteCommonAscensionMaterial.*;
import static fr.sazaju.genshin.item.EnhancementOre.*;
import static fr.sazaju.genshin.item.ForgingMaterial.*;
import static fr.sazaju.genshin.item.Gadget.*;
import static fr.sazaju.genshin.item.ItemStack.Filter.*;
import static fr.sazaju.genshin.item.LocalSpecialty.*;
import static fr.sazaju.genshin.item.Mora.*;
import static fr.sazaju.genshin.item.Potion.*;
import static fr.sazaju.genshin.weapon.WeaponType.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.Billet;
import fr.sazaju.genshin.item.CharacterAscensionMaterialMulti;
import fr.sazaju.genshin.item.CommonAscensionMaterial;
import fr.sazaju.genshin.item.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.EnhancementOre;
import fr.sazaju.genshin.item.ForgingMaterial;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemStack;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.Potion;
import fr.sazaju.genshin.item.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.WeaponAscensionMaterial;
import fr.sazaju.genshin.weapon.WeaponType;

public class Recipe {

	private final ItemStack diff;

	private Recipe(ItemStack diff) {
		this.diff = diff;
	}

	public ItemStack getDiff() {
		return diff;
	}

	public ItemStack getProducts() {
		return diff.filter(strictlyPositive());
	}

	public ItemStack getCost() {
		return diff.filter(strictlyNegative()).times(-1);
	}

	public Recipe times(int multiplier) {
		return Recipe.fromDiff(diff.times(multiplier));
	}

	public static Recipe fromDiff(ItemStack diff) {
		return new Recipe(diff);
	}

	public static Recipe fromCost(Item<?> itemProduced, ItemStack cost) {
		return new Recipe(cost.times(-1).addMaterial(itemProduced, 1));
	}

	public static Recipe fromCost(int quantityProduced, Item<?> itemProduced, ItemStack cost) {
		return new Recipe(cost.times(-1).addMaterial(itemProduced, quantityProduced));
	}

	public static Stream<Recipe> streamRecipes() {
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
				Recipe.fromCost(ADEPTI_SEEKER_S_STOVE.item(), ItemStack.fromItemsMap(Map.of(//
						FLAMING_ESSENTIAL_OIL.item(), 1, //
						CRYSTAL_CORE.item(), 2, //
						IRON_CHUNK.item(), 2, //
						MORA.item(), 500//
				))), //
				Recipe.fromCost(ANEMOCULUS_RESONANCE_STONE.item(), ItemStack.fromItemsMap(Map.of(//
						DANDELION_SEED.item(), 5, //
						CECILIA.item(), 5, //
						CRYSTAL_CHUNK.item(), 1, //
						MORA.item(), 500//
				))), //
				Recipe.fromCost(GEOCULUS_RESONANCE_STONE.item(), ItemStack.fromItemsMap(Map.of(//
						LAPIS.item(), 5, //
						GLAZE_LILY.item(), 5, //
						CRYSTAL_CHUNK.item(), 1, //
						MORA.item(), 500//
				))), //
				Recipe.fromCost(WARMING_BOTTLE.item(), ItemStack.fromItemsMap(Map.of(//
						FLAMING_FLOWER_STAMEN.item(), 2, //
						STARSILVER.item(), 2, //
						MORA.item(), 500//
				))), //
				Recipe.fromCost(ANEMO_TREASURE_COMPASS.item(), ItemStack.fromItemsMap(Map.of(//
						INSIGNIA.item(THREE_STARS), 10, //
						PHILANEMO_MUSHROOM.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.fromCost(GEO_TREASURE_COMPASS.item(), ItemStack.fromItemsMap(Map.of(//
						INSIGNIA.item(THREE_STARS), 10, //
						LAPIS.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.fromCost(NRE_MENU_30.item(), ItemStack.fromItemsMap(Map.of(//
						CHAOS.item(THREE_STARS), 20, //
						LIZARD_TAIL.item(), 20, //
						ELECTRO_CRYSTAL.item(), 50, //
						MORA.item(), 50000//
				))), //
				Recipe.fromCost(PORTABLE_WAYPOINT.item(), ItemStack.fromItemsMap(Map.of(//
						LEY_LINE.item(THREE_STARS), 1, //
						LUMINESCENT_SPINE.item(), 2, //
						CRYSTAL_CHUNK.item(), 5, //
						MORA.item(), 500//
				))), //
				Recipe.fromCost(WIND_CATCHER.item(), ItemStack.fromItemsMap(Map.of(//
						HURRICANE_SEED.item(), 10, //
						WINDWHEEL_ASTER.item(), 30, //
						CRYSTAL_CHUNK.item(), 50, //
						MORA.item(), 50000//
				)))//
		);
	}

	private static Stream<Recipe> gadgetRecipes() {
		BiFunction<WeaponType, ForgingMaterial, Recipe> recipeFactory = (weaponType, specificMineral) -> {
			return Recipe.fromCost(weaponType.item(), ItemStack.fromTypesMap(Map.of(//
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
				Recipe.fromCost(DRAGONSPINE_SPEAR.item(), ItemStack.fromTypesMap(Map.of(//
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
			return Recipe.fromCost(potion.item(), ItemStack.fromItemsMap(cost));
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
						return Recipe.fromCost(item, ItemStack.fromItemsMap(Map.of(subItem, 3, MORA.item(), moras)));
					});
		};
		return Stream.of(//
				recipesFactory.apply(//
						CharacterAscensionMaterialMulti.class, //
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
			return Recipe.fromCost(ore.item(), ItemStack.fromTypesMap(costs));
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
		return streamRecipes().filter(recipe -> recipe.diff.getQuantity(item) > 0);
	}

}
