package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum BossDrop implements ItemType.WithSingleRarity {
	/**************/
	/* Hypostasis */
	/**************/

	PRISM(FOUR_STARS),
	HURRICANE_SEED(FOUR_STARS),

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
	public StackableItem<BossDrop> item() {
		return new StackableItem<BossDrop>(this, getRarity());
	}
}
