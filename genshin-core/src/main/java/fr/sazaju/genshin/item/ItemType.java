package fr.sazaju.genshin.item;

import java.util.Collection;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.recipe.Recipe;
import fr.sazaju.genshin.recipe.Recipes;

public interface ItemType {

	boolean hasRarity(Rarity rarity);

	public interface WithSingleRarity extends ItemType {

		Rarity getRarity();

		@Override
		default boolean hasRarity(Rarity rarity) {
			return getRarity().equals(rarity);
		}

		ItemState<? extends WithSingleRarity> itemState();

		default Stream<Recipe> streamRecipes() {
			return Recipes.streamRecipesProducing(this.itemState());
		}
	}

	public interface WithMultipleRarities extends ItemType {

		Collection<Rarity> getRarities();

		@Override
		default boolean hasRarity(Rarity rarity) {
			return getRarities().contains(rarity);
		}

		ItemState<? extends WithMultipleRarities> itemState(Rarity rarity);

		default Stream<Recipe> streamRecipesAt(Rarity rarity) {
			return Recipes.streamRecipesProducing(this.itemState(rarity));
		}

	}
}
