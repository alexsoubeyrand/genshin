package fr.sazaju.genshin.material;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum WeaponAscensionMaterial implements MaterialType.WithMultipleStarRanks {
	GUYUN_PILAR;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 5).contains(rarity);
	}

	@Override
	public Material<WeaponAscensionMaterial> material(Rarity rarity) {
		return new Material<>(this, rarity);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return MaterialType.WithMultipleStarRanks.recipesOn3Submaterials(this, rarity);
	}
}
