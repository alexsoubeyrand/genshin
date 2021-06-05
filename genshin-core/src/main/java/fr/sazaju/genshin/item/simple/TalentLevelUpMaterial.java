package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum TalentLevelUpMaterial implements ItemType.WithMultipleRarities {
	PROSPERITY, GOLD;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 4);
	}

	@Override
	public ItemState<TalentLevelUpMaterial> itemState(Rarity rarity) {
		return new ItemState<>(this, rarity);
	}
}
