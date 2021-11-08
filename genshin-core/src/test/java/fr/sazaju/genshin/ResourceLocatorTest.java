package fr.sazaju.genshin;

import static fr.sazaju.genshin.Enemy.*;
import static fr.sazaju.genshin.Resource.*;
import static fr.sazaju.genshin.Resource.Type.*;
import static fr.sazaju.genshin.ResourceLocator.EnemyPredicate.*;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.*;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.resourceType;
import static fr.sazaju.genshin.Shop.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.ResourceLocator.EnemyPredicate;
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
		return Stream.of(//
				arguments(Shop.class, resourceType(COOKED_DISH), Set.of(GOOD_HUNTER)), //
				arguments(Shop.class, resourceType(ENEMIES_DROP), Set.of()) //
		);
	}

	private static Stream<Arguments> testCasesToLocateShopsFromSoldItems() {
		return Stream.of(//
				arguments(Shop.class, selling(SWEET_FLOWER), Set.of(FLORA)), //
				arguments(Shop.class, selling(SLIME_CS), Set.of()), //
				arguments(Shop.class, selling(CECILIA), Set.of(FLORA)), //
				arguments(Shop.class, selling(WHEAT), Set.of(BLANCHE, DONGSHENG)) //
		);
	}

	private static Stream<Arguments> testCasesToLocateEnemiesFromDroppedItems() {
		return Stream.of(//
				arguments(Enemy.class, dropping(SLIME_CS), Set.of(SLIMES)), //
				arguments(Enemy.class, dropping(D_MASKS), Set.of(HILICURLS, LAWACURLS, MITACURLS, SAMACURLS)), //
				arguments(Enemy.class, dropping(F_ARROWHEADS), Set.of(HILICURL_SHOOTERS)), //
				arguments(Enemy.class, dropping(D_SCROLLS), Set.of(SAMACURLS)), //
				arguments(Enemy.class, dropping(TH_INSIGNIA), Set.of(TREASURE_HOARDERS)), //
				arguments(Enemy.class, dropping(R_INSIGNIA), Set.of(FATUIS)), //
				arguments(Enemy.class, dropping(W_NECTAR), Set.of(WHOPPERFLOWERS)) //
		);
	}

	static Stream<Arguments> testsCasesToLocateEnemiesFromResourceTypes() {

		Stream<Arguments> enemiesDrop = Stream.of(//
				arguments(Enemy.class, EnemyPredicate.resourceType(ENEMIES_DROP), Set.of(Enemy.values())) //
		);

		Stream<Arguments> otherTypes = Stream.of(Resource.Type.values()) //
				.filter(type -> !type.equals(ENEMIES_DROP))//
				.map(type -> arguments(Enemy.class, EnemyPredicate.resourceType(type), emptySet()));

		return Stream.concat(enemiesDrop, otherTypes);
	}

	@ParameterizedTest
	@MethodSource("allTestCases")
	<T extends Enum<?>> void testLocate(Class<T> enumClass, Predicate<T> predicate, Set<T> expected) {
		assertEquals(expected, new ResourceLocator().locate(enumClass, predicate));
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
