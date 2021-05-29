package fr.sazaju.genshin.item.simple;

import java.util.Collection;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.StackableItem;

public enum CharacterAscensionMaterial implements ItemType.WithMultipleRarities {
	VAJRADA, //
	AGNIDUS,//
	;

	@Override
	public Collection<Rarity> getRarities() {
		return Rarity.range(2, 5);
	}

	@Override
	public StackableItem<CharacterAscensionMaterial> item(Rarity rarity) {
		return new StackableItem<>(this, rarity);
	}

}
