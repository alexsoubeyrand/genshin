package fr.sazaju.genshin.item.artifact;

import java.util.Collection;
import java.util.Set;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.Item;
import fr.sazaju.genshin.item.ItemType;

public class ArtifactType implements ItemType.WithMultipleRarities {
	public final ArtifactCategory category;
	public final Set<Rarity> rarities;

	ArtifactType(ArtifactCategory category, Set<Rarity> rarities) {
		this.category = category;
		this.rarities = rarities;
	}

	@Override
	public Collection<Rarity> getRarities() {
		return rarities;
	}

	@Override
	public Item<ArtifactType> item(Rarity rarity) {
		return new Artifact(this, rarity);
	}
}
