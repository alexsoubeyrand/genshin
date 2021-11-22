package fr.sazaju.genshin;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLocator {

	// locateEnemies(Resource.Type type) et factoriser pour n'avoir qu'une seule
	// méthode
//	public Set<Enemy> locateEnemies(Resource resource) {
//
//		return Stream.of(Enemy.values())//
//				.filter(enemy -> enemy.getDroppedResources().contains(resource))//
//				.collect(Collectors.toSet());
//	}
	public static interface EnemyPredicate extends Predicate<Enemy> {

		public static EnemyPredicate dropping(Resource resource) {
			return new EnemyPredicate() {
				@Override
				public boolean test(Enemy enemy) {
					return enemy.drops(resource);
				}
				
				@Override
				public String toString() {
					return "dropping " + resource.toString();
				}
			};
		}
	}

	public static interface ShopPredicate extends Predicate<Shop> {

		public static ShopPredicate selling(Resource resource) {
			return shop -> shop.sells(resource);
		}
	}

	public static interface Browser<T> {
		Stream<Resource> browse(T source);
	}

	public static <T> Predicate<T> resourceType(Resource.Type resourceType, Browser<T> browser) {
		return source -> browser.browse(source) //
				.filter(resource -> resource.getType().equals(resourceType)) //
				.findFirst().isPresent();
	}

	public <T extends Enum<?>> Set<T> locate(Class<T> enumClass, Predicate<? super T> predicate) {
		return Stream.of(enumClass.getEnumConstants())//
				.filter(predicate)//
				.collect(Collectors.toSet())//
		;
	}

	public Set<Shop> test(Resource res) {
		return Set.of();
	}

}
