package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum CharacterAscensionMaterialSingle implements ItemType.WithSingleRarity {
	HURRICANE_SEED, //
	;

	@Override
	public Rarity getRarity() {
		return FOUR_STARS;
	}

	@Override
	public StackableItem<CharacterAscensionMaterialSingle> item() {
		return new StackableItem<>(this, getRarity());
	}

}
