package fr.sazaju.genshin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LesRessources {

	private static enum Resource {

		SLIME_CS("Slime Condensates"), //
		D_MASKS("Damaged Masks"), //
		F_ARROWHEADS("Firm Arrowheads"), //
		D_SCROLLS("Divining Scrolls"), //
		TH_INSIGNIA("Treasure Hoarder Insignia"), //
		R_INSIGNIA("Recruit's Insignia"), //
		W_NECTAR("Whopperflower Nectar");

		private final String name;

		Resource(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	private static enum Enemy {
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

		Enemy(String name) {
			this.name = name;
			this.droppedResources = List.of();
		}

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

	public static void main(String[] args) {
		for (Resource resource : Resource.values()) {
			System.out.println(resource + " : " + locate(resource));
		}
	}

	private static List<Enemy> locate(Resource resource) {
		return Stream.of(Enemy.values())//
				.filter(enemy -> enemy.getDroppedResources().contains(resource))//
				.collect(Collectors.toList());
	}
}
