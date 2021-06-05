package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

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
	public ItemState<LocalSpecialty> itemState() {
		return new ItemState<>(this, getRarity());
	}
}
