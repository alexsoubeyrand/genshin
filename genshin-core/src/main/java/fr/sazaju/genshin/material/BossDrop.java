package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum BossDrop implements MaterialType {
	/**************/
	/* Hypostasis */
	/**************/

	PRISM(FOUR_STARS),

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
		this.rarity=rarity;
	}
	
	@Override
	public boolean hasRarity(Rarity rarity) {
		return this.rarity.equals(rarity);
	}
}
