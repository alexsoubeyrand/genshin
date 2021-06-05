package fr.sazaju.genshin.item.artifact;

import java.util.Collection;
import java.util.Set;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;

public class ArtifactType implements ItemType.WithMultipleRarities {
	private final ArtifactSet set;
	public final ArtifactCategory category;
	public final Set<Rarity> rarities;

	ArtifactType(ArtifactSet set, ArtifactCategory category, Set<Rarity> rarities) {
		this.set = set;
		this.category = category;
		this.rarities = rarities;
	}

	@Override
	public Collection<Rarity> getRarities() {
		return rarities;
	}

	@Override
	public ItemState<ArtifactType> itemState(Rarity rarity) {
		return new Artifact(this, rarity);
	}

	@Override
	public String toString() {
		return set + " " + category;
	}
}
