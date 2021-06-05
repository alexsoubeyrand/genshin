package fr.sazaju.genshin.item.artifact;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.ItemState;

public class Artifact extends ItemState<ArtifactType> {

	Artifact(ArtifactType type, Rarity rarity) {
		super(type, rarity);
	}
}
