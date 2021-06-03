package fr.sazaju.genshin.item.artifact;

import java.util.function.Supplier;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.item.NonStackableItem;

public class Artifact extends NonStackableItem<ArtifactType> {

	private final Supplier<Artifact> cloner;

	Artifact(ArtifactType type, Rarity rarity) {
		super(type, rarity);
		this.cloner = () -> new Artifact(type, rarity);
	}

	@Override
	public NonStackableItem<ArtifactType> duplicate() {
		return cloner.get();
	}
}
