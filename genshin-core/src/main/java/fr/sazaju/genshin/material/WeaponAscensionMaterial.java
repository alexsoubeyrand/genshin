package fr.sazaju.genshin.material;

import fr.sazaju.genshin.Rarity;

public enum WeaponAscensionMaterial implements MaterialType {
	GUYUN_PILAR;
	
	@Override
	public boolean hasRarity(Rarity rarity) {
		return Rarity.range(2, 5).contains(rarity);
	}
}
