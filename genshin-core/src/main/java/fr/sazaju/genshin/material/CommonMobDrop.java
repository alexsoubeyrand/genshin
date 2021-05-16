package fr.sazaju.genshin.material;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum CommonMobDrop implements MaterialType.WithMultipleStarRanks {
	NECTAR, INSIGNIA;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(1, 3).contains(rarity);
	}

	@Override
	public Material<CommonMobDrop> material(Rarity rarity) {
		return new Material<>(this, rarity);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return MaterialType.WithMultipleStarRanks.recipesOn3Submaterials(this, rarity);
	}
}
