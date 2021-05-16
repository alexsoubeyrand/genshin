package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.Rarity.*;

import java.util.Collection;
import java.util.Collections;

import fr.sazaju.genshin.Rarity;

public enum LocalSpecialty implements MaterialType {
	LAPIS, NOCTILUCOUS_JADE;

	@Override
	public boolean hasRarity(Rarity rarity) {
		return SPECIALTY.equals(rarity);
	}

	public Material<LocalSpecialty> material() {
		return new Material<>(this, SPECIALTY);
	}

	@Override
	public Collection<MaterialStack> getConversionRecipesAt(Rarity rarity) {
		return Collections.emptyList();
	}
}
