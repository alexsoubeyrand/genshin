package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

public enum CommonAscensionMaterial implements ItemType.WithMultipleRarities {
	NECTAR, //
	INSIGNIA;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(1, 3);
	}

	@Override
	public StackableItem<CommonAscensionMaterial> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}
}
