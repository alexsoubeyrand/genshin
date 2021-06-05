package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum WeaponAscensionMaterial implements ItemType.WithMultipleRarities {
	GUYUN_PILAR;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 5);
	}

	@Override
	public ItemState<WeaponAscensionMaterial> itemState(Rarity rarity) {
		return new ItemState<>(this, rarity);
	}

}
