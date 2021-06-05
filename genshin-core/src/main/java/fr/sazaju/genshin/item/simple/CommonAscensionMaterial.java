package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum CommonAscensionMaterial implements ItemType.WithMultipleRarities {
	NECTAR, //
	INSIGNIA;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(1, 3);
	}

	@Override
	public ItemState<CommonAscensionMaterial> itemState(Rarity rarity) {
		return new ItemState<>(this, rarity);
	}
}
