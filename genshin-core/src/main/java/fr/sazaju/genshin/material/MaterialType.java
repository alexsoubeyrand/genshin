package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.material.Mora.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.sazaju.genshin.Rarity;

public interface MaterialType {

	boolean hasRarity(Rarity rarity);

	Collection<MaterialStack> getConversionRecipesAt(Rarity rarity);

	public interface WithMultipleStarRanks extends MaterialType {

		Material<? extends WithMultipleStarRanks> material(Rarity rarity);

		public static Collection<MaterialStack> recipesOn3Submaterials(WithMultipleStarRanks type, Rarity rarity) {
			Rarity belowRarity = rarity.below();
			if (!type.hasRarity(belowRarity)) {
				return Collections.emptyList();
			}
			return List.of(MaterialStack.fromMap(Map.of(//
					type.material(rarity), 1, //
					type.material(belowRarity), -3, //
					MORA.material(), -costInMora(type, rarity)//
			)));
		}

		private static int costInMora(MaterialType type, Rarity rarity) {
			// TODO Compute cost in mora
			return 0;
		}

	}
}
