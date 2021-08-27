package fr.sazaju.genshin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLocator {

	public Set<Enemy> locateEnemies(Resource resource) {

		return Stream.of(Enemy.values())//
				.filter(enemy -> enemy.getDroppedResources().contains(resource))//
				.collect(Collectors.toSet());
	}

	public Set<Shop> locateShops(Resource resource) {
		return Stream.of(Shop.values())//
				.filter(shop2 -> shop2.sells(resource))//
				.collect(Collectors.toSet());
	}

	// Factoriser les deux locateShops
	public Set<Shop> locateShops(Resource.Type resourceType) {
		return Stream.of(Shop.values())//
				.filter(shop -> {
					return shop.getItemsSold().stream() //
							.filter(resource -> resource.getType().equals(resourceType)) //
							.findFirst().isPresent();
				})//
				.collect(Collectors.toSet())//
		;
	}

}
