package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum EventMaterial implements MaterialType {
	CROWN_OF_INSIGHT;
	
	@Override
	public boolean hasRarity(Rarity rarity) {
		return FIVE_STARS.equals(rarity);
	}
}
