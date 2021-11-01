package fr.sazaju.genshin;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLocator {
	
	// locateEnemies(Resource.Type type) et factoriser pour n'avoir qu'une seule méthode
//	public Set<Enemy> locateEnemies(Resource resource) {
//
//		return Stream.of(Enemy.values())//
//				.filter(enemy -> enemy.getDroppedResources().contains(resource))//
//				.collect(Collectors.toSet());
//	}
	
	public static interface EnemyPredicate extends Predicate<Enemy> {

		
		
		public static EnemyPredicate dropping(Resource resource) {
			return enemy -> enemy.drops(resource);
		}

		public static EnemyPredicate resourceType(Resource.Type resourceType) {
			return enemy -> enemy.getDroppedResources().stream() //
					.filter(resource -> resource.getType().equals(resourceType)) //
					.findFirst().isPresent();
		}
	}
	
	public static interface ShopPredicate extends Predicate<Shop> {

		public static ShopPredicate selling(Resource resource) {
			return new ShopPredicate() {
				@Override
				public boolean test(Shop shop) {
					return shop.sells(resource);
				}
			};
		}

		public static ShopPredicate resourceType(Resource.Type resourceType) {
			return shop -> shop.getItemsSold().stream() //
					.filter(resource -> resource.getType().equals(resourceType)) //
					.findFirst().isPresent();
		}
	}

	public <T extends Enum<?>> Set<T> locate(Class <T> enumClass, Predicate<? super T> predicate) {
		return Stream.of(enumClass.getEnumConstants())//
				.filter(predicate)//
				.collect(Collectors.toSet())//
		;
	}

	public Set<Shop> test(Resource res) {
		return Set.of();
	}

}
