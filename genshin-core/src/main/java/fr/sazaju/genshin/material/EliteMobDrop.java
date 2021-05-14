package fr.sazaju.genshin.material;

import java.util.Optional;

import fr.sazaju.genshin.Rarity;

public enum EliteMobDrop implements MaterialType {
	SACRIFICIAL_KNIFE;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 4).contains(rarity);
	}

	@Override
	public Optional<MaterialStack> getConversionRecipeAt(Rarity rarity) {
		return MaterialType.recipeOn3Submaterials(this, rarity);
	}

}
