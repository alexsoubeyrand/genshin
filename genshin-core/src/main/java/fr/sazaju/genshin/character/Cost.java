package fr.sazaju.genshin.character;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.material.Material;

public interface Cost {
	Map<Material<?>, Integer> toMap();

	public static Cost fromMap(Map<Material<?>, Integer> map) {
		return new Cost() {
			@Override
			public Map<Material<?>, Integer> toMap() {
				return map;
			}
		};
	}

	default Cost add(Cost that) {
		Map<Material<?>, Integer> map1 = this.toMap();
		Map<Material<?>, Integer> map2 = that.toMap();
		Map<Material<?>, Integer> aggregate = Stream.of(map1, map2)//
				.map(Map::keySet)//
				.flatMap(Set::stream)//
				.distinct()//
				.collect(Collectors.toMap(//
						key -> key, //
						key -> map1.getOrDefault(key, 0) //
								+ map2.getOrDefault(key, 0)//
				));
		return Cost.fromMap(aggregate);
	}

	static Cost empty() {
		return Cost.fromMap(Collections.emptyMap());
	}
}
