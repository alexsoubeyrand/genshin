package fr.sazaju.genshin.material;

import java.util.Comparator;
import java.util.Objects;

import fr.sazaju.genshin.character.Rarity;

public class Material<T extends MaterialType> {
	public final T type;
	public final Rarity rarity;

	public Material(T type, Rarity rarity) {
		this.type = type;
		this.rarity = rarity;
	}

	@Override
	public String toString() {
		return type + " " + rarity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Material) {
			Material<?> that = (Material<?>) obj;
			return Objects.equals(this.type, that.type) //
					&& Objects.equals(this.rarity, that.rarity);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, rarity);
	}

	public static Comparator<Material<?>> syntaxicComparator() {
		Comparator<Material<?>> materialType = Comparator.comparing(material -> {
			return ((Enum<?>) material.type).name();
		});
		Comparator<Material<?>> rarity = Comparator.comparing(material -> material.rarity.stars);
		Comparator<Material<?>> materialComparator = materialType.thenComparing(rarity);
		return materialComparator;
	}
}
