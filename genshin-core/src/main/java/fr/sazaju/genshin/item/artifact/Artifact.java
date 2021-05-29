package fr.sazaju.genshin.item.artifact;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.Item;

public class Artifact extends Item<ArtifactType> {

	Artifact(ArtifactType type, Rarity rarity) {
		super(type, rarity);
	}
}
