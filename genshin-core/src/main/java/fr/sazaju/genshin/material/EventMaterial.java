package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import java.util.Collection;
import java.util.Collections;

import fr.sazaju.genshin.Rarity;

public enum EventMaterial implements MaterialType {
	CROWN_OF_INSIGHT;

	public final Material<EventMaterial> material = new Material<>(this, FIVE_STARS);

	@Override
	public boolean hasRarity(Rarity rarity) {
		return FIVE_STARS.equals(rarity);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return Collections.emptyList();
	}
}
