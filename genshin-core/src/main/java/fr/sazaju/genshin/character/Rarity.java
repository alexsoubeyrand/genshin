package fr.sazaju.genshin.character;

import fr.sazaju.genshin.material.Material;
import fr.sazaju.genshin.material.MaterialType;

public enum Rarity {
	ONE_STAR(1), TWO_STARS(2), THREE_STARS(3), FOUR_STARS(4), FIVE_STARS(5);

	public final int stars;

	Rarity(int stars) {
		this.stars = stars;
	}

	@Override
	public String toString() {
		return stars + "☆";
	}

	public <T extends MaterialType> Material<T> of(T material) {
		return new Material<>(material, this);
	}
}
