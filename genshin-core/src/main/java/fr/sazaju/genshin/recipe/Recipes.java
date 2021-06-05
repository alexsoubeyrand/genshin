package fr.sazaju.genshin.recipe;

import static fr.sazaju.genshin.Rarity.*;
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
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

public class Recipes {
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
				recipeForItem(ADEPTI_SEEKER_S_STOVE.itemState(), Map.of(//
						FLAMING_ESSENTIAL_OIL.itemState(), 1, //
						CRYSTAL_CORE.itemState(), 2, //
						IRON_CHUNK.itemState(), 2, //
						MORA.itemState(), 500//
				)), //
				recipeForItem(ANEMOCULUS_RESONANCE_STONE.itemState(), Map.of(//
						DANDELION_SEED.itemState(), 5, //
						CECILIA.itemState(), 5, //
						CRYSTAL_CHUNK.itemState(), 1, //
						MORA.itemState(), 500//
				)), //
				recipeForItem(GEOCULUS_RESONANCE_STONE.itemState(), Map.of(//
						LAPIS.itemState(), 5, //
						GLAZE_LILY.itemState(), 5, //
						CRYSTAL_CHUNK.itemState(), 1, //
						MORA.itemState(), 500//
				)), //
				recipeForItem(WARMING_BOTTLE.itemState(), Map.of(//
						FLAMING_FLOWER_STAMEN.itemState(), 2, //
						STARSILVER.itemState(), 2, //
						MORA.itemState(), 500//
				)), //
				recipeForItem(ANEMO_TREASURE_COMPASS.itemState(), Map.of(//
						INSIGNIA.itemState(THREE_STARS), 10, //
						PHILANEMO_MUSHROOM.itemState(), 30, //
						CRYSTAL_CHUNK.itemState(), 50, //
						MORA.itemState(), 50000//
				)), //
				recipeForItem(GEO_TREASURE_COMPASS.itemState(), Map.of(//
						INSIGNIA.itemState(THREE_STARS), 10, //
						LAPIS.itemState(), 30, //
						CRYSTAL_CHUNK.itemState(), 50, //
						MORA.itemState(), 50000//
				)), //
				recipeForItem(NRE_MENU_30.itemState(), Map.of(//
						CHAOS.itemState(THREE_STARS), 20, //
						LIZARD_TAIL.itemState(), 20, //
						ELECTRO_CRYSTAL.itemState(), 50, //
						MORA.itemState(), 50000//
				)), //
				recipeForItem(PORTABLE_WAYPOINT.itemState(), Map.of(//
						LEY_LINE.itemState(THREE_STARS), 1, //
						LUMINESCENT_SPINE.itemState(), 2, //
						CRYSTAL_CHUNK.itemState(), 5, //
						MORA.itemState(), 500//
				)), //
				recipeForItem(WIND_CATCHER.itemState(), Map.of(//
						HURRICANE_SEED.itemState(), 10, //
						WINDWHEEL_ASTER.itemState(), 30, //
						CRYSTAL_CHUNK.itemState(), 50, //
						MORA.itemState(), 50000//
				))//
		);
	}

	private static Stream<Recipe> gadgetRecipes() {
		BiFunction<WeaponType, ForgingMaterial, Recipe> recipeFactory = (weaponType, specificMineral) -> {
			return recipeFromTypes(weaponType, Map.of(//
					Billet.selectFor(weaponType.category), 1, //
					CRYSTAL_CHUNK, 50, //
					specificMineral, 50, //
					MORA, 500//
			));
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
				recipeFromTypes(DRAGONSPINE_SPEAR, Map.of(//
						NORTHLANDER_POLEARM_BILLET, 1, //
						VITALIZED_DRAGONTOOTH, 8, // Particular item & quantity here
						STARSILVER, 50, //
						MORA, 500//
				))//
		);
	}

	private static Stream<Recipe> potionRecipes() {
		BiFunction<Potion, Stream<ItemType.WithSingleRarity>, Recipe> recipeFactory = (potion, specificItemTypes) -> {
			Map<ItemState<?>, Integer> cost = Stream.concat(//
					specificItemTypes.map(type -> Map.entry(type.itemState(), 1)), // Add one of each
					Stream.of(Map.entry(MORA.itemState(), 100))// Add 100 Moras
			).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			return recipeForItem(potion.itemState(), cost);
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

	private static Recipe recipeForItem(ItemState<?> item, Map<ItemState<?>, Integer> cost) {
		return Recipe.fromDiff(cost).reverse().produces(item, 1);
	}

	private static Stream<Recipe> recipesOn3SubItems() {
		BiFunction<Class<? extends ItemType.WithMultipleRarities>, Map<Rarity, Integer>, Stream<Recipe>> //
		recipesFactory = (clazz, morasCosts) -> {
			return Stream.of(clazz.getEnumConstants())//
					.flatMap(itemType -> {
						return itemType.getRarities().stream()//
								.sorted().skip(1)// Ignore lowest rarity (no sub to craft it)
								.map(rarity -> itemType.itemState(rarity));// Get all possible items
					})//
					.map(item -> {
						ItemState<?> subItem = item.getType().itemState(item.getRarity().below());
						int moras = morasCosts.get(item.getRarity());
						return recipeForItem(item, Map.of(subItem, 3, MORA.itemState(), moras));
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
			return recipeFromTypes(ore, costs);
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

	private static Recipe recipeFromTypes(ItemType.WithSingleRarity typeProduct,
			Map<ItemType.WithSingleRarity, Integer> typesCosts) {
		return recipeForItem(//
				typeProduct.itemState(), //
				typesCosts.entrySet().stream()//
						.map(entry -> Map.entry(entry.getKey().itemState(), entry.getValue()))//
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))//
		);
	}

	public static Stream<Recipe> streamRecipesProducing(ItemState<?> item) {
		return streamMihoyoRecipes().filter(recipe -> recipe.getQuantity(item) > 0);
	}
}
