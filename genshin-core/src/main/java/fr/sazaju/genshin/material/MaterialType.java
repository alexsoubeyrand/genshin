package fr.sazaju.genshin.material;

import java.util.Map;
import java.util.Optional;

import fr.sazaju.genshin.Rarity;

public interface MaterialType {

	boolean hasRarity(Rarity rarity);

	default Optional<MaterialStack> getConversionRecipeAt(Rarity rarity) {
		return Optional.empty();
	}

	public static Optional<MaterialStack> recipeOn3Submaterials(MaterialType type, Rarity rarity) {
		Rarity belowRarity = rarity.below();
		if (!type.hasRarity(belowRarity)) {
			return Optional.empty();
		}
		return Optional.of(MaterialStack.fromMap(Map.of(//
				rarity.of(type), 1, //
				belowRarity.of(type), -3//
		)));
	}
}
