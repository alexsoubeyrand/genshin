package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum Mora implements MaterialType {
	MORA;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return THREE_STARS.equals(rarity);
	}
}
