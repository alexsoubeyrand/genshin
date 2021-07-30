package fr.sazaju.genshin;

import java.util.Map;
import java.util.Set;

public enum Shop {
	FLORA(), //
	BLANCHE()

	;

	// Comment factoriser "sells" et "getPrice"
	
	boolean sells(Resource resource) {
		Set<Resource> resources = Set.of();
		if (this == FLORA) {
			resources = Set.of(Resource.SWEET_FLOWER, Resource.CECILIA);
		} else if (this == BLANCHE) {
			resources = Set.of(Resource.WHEAT);
		} else {
			throw new RuntimeException(this + " isn't supported yet");
		}
		return resources.contains(resource);
	}

	Integer getPrice(Resource resource) {
		Map<Resource, Integer> map;
		if (this == FLORA) {
			map = Map.of(Resource.SWEET_FLOWER, 200, Resource.CECILIA, 1000);
		} else if (this == BLANCHE) {
			map = Map.of(Resource.WHEAT, 100);
		} else {
			throw new RuntimeException(this + " isn't supported yet");
		}

		if (map.containsKey(resource)) {
			return map.get(resource);
		} else {
			throw new IllegalArgumentException(resource + " isn't sold by " + this);
		}
	}
}
