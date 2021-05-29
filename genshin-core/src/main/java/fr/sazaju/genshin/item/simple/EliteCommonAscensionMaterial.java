package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

public enum EliteCommonAscensionMaterial implements ItemType.WithMultipleRarities {
	CHAOS, //
	LEY_LINE, //
	SACRIFICIAL_KNIFE;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 4);
	}

	@Override
	public StackableItem<EliteCommonAscensionMaterial> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}

}
