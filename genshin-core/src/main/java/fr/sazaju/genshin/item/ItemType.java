package fr.sazaju.genshin.item;

import java.util.Collection;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.recipe.Recipe;

public interface ItemType {

	boolean hasRarity(Rarity rarity);

	public interface WithSingleRarity extends ItemType {

		Rarity getRarity();

		@Override
		default boolean hasRarity(Rarity rarity) {
			return getRarity().equals(rarity);
		}

		Item<? extends WithSingleRarity> item();

		default Stream<Recipe> streamRecipes() {
			return Recipe.streamRecipesProducing(this.item());
		}
	}

	public interface WithMultipleRarities extends ItemType {

		Collection<Rarity> getRarities();

		@Override
		default boolean hasRarity(Rarity rarity) {
			return getRarities().contains(rarity);
		}

		Item<? extends WithMultipleRarities> item(Rarity rarity);

		default Stream<Recipe> streamRecipesAt(Rarity rarity) {
			return Recipe.streamRecipesProducing(this.item(rarity));
		}

	}
}
