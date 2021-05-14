package fr.sazaju.genshin.material;

import fr.sazaju.genshin.Rarity;

public enum CommonMobDrop implements MaterialType {
	NECTAR, INSIGNIA;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(1, 3).contains(rarity);
	}
}
