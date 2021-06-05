package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum CharacterAscensionMaterial implements ItemType.WithMultipleRarities {
	VAJRADA, //
	AGNIDUS,//
	;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 5);
	}

	@Override
	public ItemState<CharacterAscensionMaterial> itemState(Rarity rarity) {
		return new ItemState<>(this, rarity);
	}

}
