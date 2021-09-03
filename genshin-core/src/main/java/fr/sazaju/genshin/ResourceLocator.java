package fr.sazaju.genshin;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLocator {
	
	// locateEnemies(Resource.Type type) et factoriser pour n'avoir qu'une seule méthode
	public Set<Enemy> locateEnemies(Resource resource) {

		return Stream.of(Enemy.values())//
				.filter(enemy -> enemy.getDroppedResources().contains(resource))//
				.collect(Collectors.toSet());
	}

	public static interface ShopPredicate extends Predicate<Shop> {

		public static ShopPredicate resource(Resource resource) {
			return shop -> shop.sells(resource);
		}

		public static ShopPredicate resourceType(Resource.Type resourceType) {
			return shop -> shop.getItemsSold().stream() //
					.filter(resource -> resource.getType().equals(resourceType)) //
					.findFirst().isPresent();
		}
	}

	public Set<Shop> locateShops(ShopPredicate predicate) {
		return Stream.of(Shop.values())//
				.filter(predicate)//
				.collect(Collectors.toSet())//
		;
	}

	public Set<Shop> test(Resource res) {
		return Set.of();
	}

}
