package fr.sazaju.genshin.item;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum TalentLevelUpMaterial implements ItemType.WithMultipleRarities {
	PROSPERITY, GOLD;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 4);
	}

	@Override
	public StackableItem<TalentLevelUpMaterial> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}
}
