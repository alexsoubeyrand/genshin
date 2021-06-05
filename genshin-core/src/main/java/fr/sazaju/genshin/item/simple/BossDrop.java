package fr.sazaju.genshin.item.simple;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public enum BossDrop implements ItemType.WithSingleRarity {
	/**************/
	/* Hypostasis */
	/**************/

	PRISM(FOUR_STARS), HURRICANE_SEED(FOUR_STARS),

	/***********/
	/* Andrius */
	/***********/

	RING_OF_BOREAS(FIVE_STARS),

	/**************/
	/* Geovishaps */
	/**************/

	JUVENILE_JADE(FOUR_STARS),

	/***********/
	/* Azhdaha */
	/***********/

	BLOODJADE_BRANCH(FIVE_STARS);

	private final Rarity rarity;

	BossDrop(Rarity rarity) {
		this.rarity = rarity;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public ItemState<BossDrop> itemState() {
		return new ItemState<BossDrop>(this, getRarity());
	}
}
