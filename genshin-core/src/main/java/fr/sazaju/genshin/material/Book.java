package fr.sazaju.genshin.material;

import fr.sazaju.genshin.Rarity;

public enum Book implements MaterialType {
	PROSPERITY, GOLD;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 4).contains(rarity);
	}
}
