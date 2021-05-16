package fr.sazaju.genshin.item;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;

public enum CharacterAscensionMaterialMulti implements ItemType.WithMultipleRarities {
	VAJRADA, //
	AGNIDUS,//
	;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 5);
	}

	@Override
	public StackableItem<CharacterAscensionMaterialMulti> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}

}
