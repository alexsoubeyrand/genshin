package fr.sazaju.genshin;

import static fr.sazaju.genshin.Enemy.*;
import static fr.sazaju.genshin.Resource.*;
import static fr.sazaju.genshin.Resource.Type.*;
import static fr.sazaju.genshin.ResourceLocator.*;
import static fr.sazaju.genshin.ResourceLocator.EnemyPredicate.*;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.*;
import static fr.sazaju.genshin.Shop.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.ResourceLocator.Browser;
import fr.sazaju.genshin.ResourceLocator.ShopPredicate;

class ResourceLocatorTest {
	static Stream<Arguments> allTestCases() {
		return Stream.of(//
				testsCasesToLocateEnemiesFromResourceTypes(), //
				testCasesToLocateShopsFromResourceTypes(), //
				testCasesToLocateEnemiesFromDroppedItems(), //
				testCasesToLocateShopsFromSoldItems() //
		).flatMap(testCases -> testCases);
	}

	private static Stream<Arguments> testCasesToLocateShopsFromResourceTypes() {
		Browser<Shop> browser = shop -> shop.getItemsSold().stream();
		return Stream.of(//
				testCase(Shop.class, resourceType(COOKED_DISH, browser), Set.of(GOOD_HUNTER)), //
				testCase(Shop.class, resourceType(ENEMIES_DROP, browser), Set.of()) //
		);
	}

	private static Stream<Arguments> testCasesToLocateShopsFromSoldItems() {
		return Stream.of(//
				testCase(Shop.class, selling(SWEET_FLOWER), Set.of(FLORA)), //
				testCase(Shop.class, selling(SLIME_CS), Set.of()), //
				testCase(Shop.class, selling(CECILIA), Set.of(FLORA)), //
				testCase(Shop.class, selling(WHEAT), Set.of(BLANCHE, DONGSHENG)) //
		);
	}

	// TODO : Mettre au propre
	private static Stream<Arguments> testCasesToLocateEnemiesFromDroppedItems() {
		return Stream.of(//
				enemyDroppingCase(SLIME_CS, Set.of(SLIMES)), //
				enemyDroppingCase(D_MASKS, Set.of(HILICURLS, LAWACURLS, MITACURLS, SAMACURLS)), //
				enemyDroppingCase(F_ARROWHEADS, Set.of(HILICURL_SHOOTERS)), //
				enemyDroppingCase(D_SCROLLS, Set.of(SAMACURLS)), //
				enemyDroppingCase(TH_INSIGNIA, Set.of(TREASURE_HOARDERS)), //
				enemyDroppingCase(R_INSIGNIA, Set.of(FATUIS)), //
				enemyDroppingCase(W_NECTAR, Set.of(WHOPPERFLOWERS)) //
		);

//		BiFunction<Resource, Set<Enemy>, Arguments> testCaseFactory = (resource, expected) -> testCase(Enemy.class, dropping(resource), expected);
//		return Map.of( //
//				SLIME_CS, Set.of(SLIMES), //
//				D_MASKS, Set.of(HILICURLS, LAWACURLS, MITACURLS, SAMACURLS), //
//				F_ARROWHEADS, Set.of(HILICURL_SHOOTERS), //
//				D_SCROLLS, Set.of(SAMACURLS), //
//				TH_INSIGNIA, Set.of(TREASURE_HOARDERS), //
//				R_INSIGNIA, Set.of(FATUIS), //
//				W_NECTAR, Set.of(WHOPPERFLOWERS)//
//		)//
//				.entrySet().stream().map(entry -> testCaseFactory.apply(entry.getKey(), entry.getValue()));
	}

	private static Arguments enemyDroppingCase(Resource resource, Set<Enemy> expected) {
		return testCase(Enemy.class, dropping(resource), expected);
	}

	private static <E extends Enum<E>> Arguments testCase(Class<E> enumClass, Predicate<E> predicate, Set<E> expected) {
		return arguments(new Decorateur<>(enumClass), predicate, expected);
	}

	static Stream<Arguments> testsCasesToLocateEnemiesFromResourceTypes() {

		Browser<Enemy> browser = enemy -> enemy.getDroppedResources().stream();
		Stream<Arguments> enemiesDrop = Stream.of(//
				testCase(Enemy.class, resourceType(ENEMIES_DROP, browser), Set.of(Enemy.values())) //
		);

		Stream<Arguments> otherTypes = Stream.of(Resource.Type.values()) //
				.filter(type -> !type.equals(ENEMIES_DROP))//
				.map(type -> testCase(Enemy.class, resourceType(type, browser), emptySet()));

		return Stream.concat(enemiesDrop, otherTypes);
	}

	//Composition pour détourner le extends de Class<T>
	static class Decorateur<T> {
		final Class<T> myClass;

		public String toString() {
			return "The instances of type " + myClass.getSimpleName();
		}

		public Decorateur(Class<T> myClass) {
			this.myClass = myClass;
		}
	}

	@ParameterizedTest(name = "{0} {1} should be {2}")
	@MethodSource("allTestCases")
	<T extends Enum<?>> void testLocate(Decorateur<T> enumClass, Predicate<T> predicate, Set<T> expected) {

		assertEquals(expected, new ResourceLocator().locate(enumClass.myClass, predicate));
	}

	private static ShopPredicate numberOfItemsSold(int numberOfItemsSold) {
		return shop -> shop.getItemsSold().size() == numberOfItemsSold;
	}

	@Test
	void testPredicate() {
		Predicate<Object> predicate = new Predicate<Object>() {
			@Override
			public boolean test(Object enumValue) {
				return enumValue.toString().startsWith("F");
			}
		};
		assertEquals(Set.of(FLORA), new ResourceLocator().locate(Shop.class, predicate));
		assertEquals(Set.of(FATUIS), new ResourceLocator().locate(Enemy.class, predicate));
	}
}
