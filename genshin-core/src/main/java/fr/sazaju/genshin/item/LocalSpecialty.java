package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum LocalSpecialty implements ItemType.WithSingleRarity {
	/*************/
	/* MONDSTADT */
	/*************/
	WINDWHEEL_ASTER, //
	PHILANEMO_MUSHROOM, //
	CECILIA, //
	/*********/
	/* LIYUE */
	/*********/
	GLAZE_LILY, //
	LAPIS, //
	NOCTILUCOUS_JADE;//

	@Override
	public Rarity getRarity() {
		return NO_RARITY;
	}

	@Override
	public StackableItem<LocalSpecialty> item() {
		return new StackableItem<>(this, getRarity());
	}
}
