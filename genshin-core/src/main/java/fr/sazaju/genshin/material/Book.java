package fr.sazaju.genshin.material;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum Book implements MaterialType.WithMultipleStarRanks {
	PROSPERITY, GOLD;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 4).contains(rarity);
	}

	@Override
	public Material<Book> material(Rarity rarity) {
		return new Material<>(this, rarity);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return MaterialType.WithMultipleStarRanks.recipesOn3Submaterials(this, rarity);
	}
}
