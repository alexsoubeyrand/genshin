package fr.sazaju.genshin;

import java.util.List;

public enum Enemy {
	SLIMES("Slimes", List.of(Resource.SLIME_CS)), //
	HILICURLS("Hilicurls", List.of(Resource.D_MASKS)), //
	SAMACURLS("Samacurls", List.of(Resource.D_MASKS, Resource.D_SCROLLS)), //
	MITACURLS("Mitacurls", List.of(Resource.D_MASKS)), //
	LAWACURLS("Lawacurls", List.of(Resource.D_MASKS)), //
	HILICURL_SHOOTERS("Hilicurl Shooters", List.of(Resource.F_ARROWHEADS)), //
	TREASURE_HOARDERS("Treasure Hoarders", List.of(Resource.TH_INSIGNIA)), //
	FATUIS("Fatuis", List.of(Resource.R_INSIGNIA)), //
	WHOPPERFLOWERS("Whopperflowers", List.of(Resource.W_NECTAR));

	private final String name;
	private final List<Resource> droppedResources;

	Enemy(String name, List<Resource> droppedResources) {
		this.name = name;
		this.droppedResources = droppedResources;
	}

	public List<Resource> getDroppedResources() {
		return droppedResources;
	}

	@Override
	public String toString() {
		return name;
	}
}
