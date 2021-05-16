package fr.sazaju.genshin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Rarity {
	SPECIALTY(0), ONE_STAR(1), TWO_STARS(2), THREE_STARS(3), FOUR_STARS(4), FIVE_STARS(5);

	public final int stars;

	Rarity(int stars) {
		this.stars = stars;
	}

	public Rarity below() {
		return Stream.of(values()).filter(rarity -> rarity.stars == this.stars - 1).findFirst().orElseThrow();
	}

	@Override
	public String toString() {
		if (stars == 0) {
			return name();
		} else {
			return stars + "☆";
		}
	}

	public static Set<Rarity> range(int minStars, int maxStars) {
		return Stream.of(values())//
				.filter(rarity -> rarity.stars >= minStars && rarity.stars <= maxStars)//
				.collect(Collectors.toSet());
	}
}
