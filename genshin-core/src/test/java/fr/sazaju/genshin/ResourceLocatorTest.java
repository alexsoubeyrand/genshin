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
import fr.sazaju.genshin.ResourceLocator.EnemyPredicate;
import fr.sazaju.genshin.ResourceLocator.ShopPredicate;

class ResourceLocatorTest {
	// TODO: Adapter le toString de la class de la methode de test (testLocate)
	// (essayer avec le décorateur)
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
		Decorateur<Shop> decorateur = new Decorateur<>(Shop.class);
		return Stream.of(//
				arguments(decorateur, resourceType(COOKED_DISH, browser), Set.of(GOOD_HUNTER)), //
				arguments(decorateur, resourceType(ENEMIES_DROP, browser), Set.of()) //
		);
	}

	private static Stream<Arguments> testCasesToLocateShopsFromSoldItems() {
		Decorateur<Shop> decorateur = new Decorateur<>(Shop.class);
		return Stream.of(//
				arguments(decorateur, selling(SWEET_FLOWER), Set.of(FLORA)), //
				arguments(decorateur, selling(SLIME_CS), Set.of()), //
				arguments(decorateur, selling(CECILIA), Set.of(FLORA)), //
				arguments(decorateur, selling(WHEAT), Set.of(BLANCHE, DONGSHENG)) //
		);
	}
	
	//TODO : Factoriser les arguments (decorateur, arguments qui se répète)
	private static Stream<Arguments> testCasesToLocateEnemiesFromDroppedItems() {
		Decorateur<Enemy> decorateur = new Decorateur<>(Enemy.class);
		return Stream.of(//
				arguments(decorateur, dropping(SLIME_CS), Set.of(SLIMES)), //
				arguments(decorateur, dropping(D_MASKS), Set.of(HILICURLS, LAWACURLS, MITACURLS, SAMACURLS)), //
				arguments(decorateur, dropping(F_ARROWHEADS), Set.of(HILICURL_SHOOTERS)), //
				arguments(decorateur, dropping(D_SCROLLS), Set.of(SAMACURLS)), //
				arguments(decorateur, dropping(TH_INSIGNIA), Set.of(TREASURE_HOARDERS)), //
				arguments(decorateur, dropping(R_INSIGNIA), Set.of(FATUIS)), //
				arguments(decorateur, dropping(W_NECTAR), Set.of(WHOPPERFLOWERS)) //
		);
	}

	static Stream<Arguments> testsCasesToLocateEnemiesFromResourceTypes() {

		Browser<Enemy> browser = enemy -> enemy.getDroppedResources().stream();
		Decorateur<Enemy> decorateur = new Decorateur<>(Enemy.class);
		Stream<Arguments> enemiesDrop = Stream.of(//
				arguments(decorateur, resourceType(ENEMIES_DROP, browser), Set.of(Enemy.values())) //
		);

		Stream<Arguments> otherTypes = Stream.of(Resource.Type.values()) //
				.filter(type -> !type.equals(ENEMIES_DROP))//
				.map(type -> arguments(decorateur, resourceType(type, browser), emptySet()));

		return Stream.concat(enemiesDrop, otherTypes);
	}

	// Composition pour détourner le extends de Class<T>
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
//		fail();

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

	// TODO testLocateShops(Resource.Type type)

//	arguments(Resource.SWEET_FLOWER, Set.of(Shop.FLORA)),
//	arguments(Resource.CECILIA, Set.of(Shop.FLORA)),
//	arguments(Resource.SMALL_LAMP_GRASS, Set.of(Shop.FLORA)),
//	arguments(Resource.CALLA_LILLY, Set.of(Shop.FLORA)),
//	arguments(Resource.WINDWHEEL_ASTER, Set.of(Shop.FLORA))
}
