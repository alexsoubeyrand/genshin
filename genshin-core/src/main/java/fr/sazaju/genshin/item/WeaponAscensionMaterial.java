package fr.sazaju.genshin.item;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum WeaponAscensionMaterial implements ItemType.WithMultipleRarities {
	GUYUN_PILAR;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 5);
	}

	@Override
	public StackableItem<WeaponAscensionMaterial> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}

}
