package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum Mora implements MaterialType {
	MORA;
	
	@Override
	public boolean hasRarity(Rarity rarity) {
		return THREE_STARS.equals(rarity);
	}
	
	public Material<Mora> material() {
		return new Material<>(this, THREE_STARS);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		throw new RuntimeException("Not yet implemented");
	}
}
