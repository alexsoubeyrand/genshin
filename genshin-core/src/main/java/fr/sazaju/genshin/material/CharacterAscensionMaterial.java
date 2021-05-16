package fr.sazaju.genshin.material;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum CharacterAscensionMaterial implements MaterialType.WithMultipleStarRanks {
	VAJRADA, AGNIDUS;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 5).contains(rarity);
	}

	@Override
	public Material<CharacterAscensionMaterial> material(Rarity rarity) {
		return new Material<>(this, rarity);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return MaterialType.WithMultipleStarRanks.recipesOn3Submaterials(this, rarity);
	}
}
