package fr.sazaju.genshin.item.artifact;

import static fr.sazaju.genshin.Rarity.*;

import java.util.Set;

import fr.sazaju.genshin.Rarity;

public enum ArtifactSet {
	MAIDEN_BELOVED(Set.of(ArtifactCategory.values()), Set.of(FOUR_STARS, FIVE_STARS)), //
	TRAVELING_DOCTOR(Set.of(ArtifactCategory.values()), Set.of(THREE_STARS)), //
	;

	public final Set<ArtifactCategory> categories;
	public final Set<Rarity> rarities;

	ArtifactSet(Set<ArtifactCategory> categories, Set<Rarity> rarities) {
		this.categories = categories;
		this.rarities = rarities;
	}

	ArtifactType ofCategory(ArtifactCategory category) {
		if (!categories.contains(category)) {
			throw new IllegalArgumentException(String.format("'%s' does not exist with category '%s'", this, category));
		}
		return new ArtifactType(category, rarities);
	}
}
