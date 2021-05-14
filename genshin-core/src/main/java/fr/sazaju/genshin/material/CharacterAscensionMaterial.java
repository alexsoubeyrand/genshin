package fr.sazaju.genshin.material;

import java.util.Optional;

import fr.sazaju.genshin.Rarity;

public enum CharacterAscensionMaterial implements MaterialType {
	VAJRADA, AGNIDUS;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 5).contains(rarity);
	}
	
	@Override
	public Optional<MaterialStack> getConversionRecipeAt(Rarity rarity) {
		return MaterialType.recipeOn3Submaterials(this, rarity);
	}
}
